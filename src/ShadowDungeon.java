import bagel.*;

import java.util.Properties;

import bagel.util.Point;
import entities.Door;
import entities.EnemyManager;
import entities.PlayerUI;
import utils.PlayerUpdateConfig;
import utils.ReadDoor;
import entities.Player;
import rooms.BattleRoomA;
import rooms.BattleRoomB;
import rooms.EndRoom;
import rooms.PrepRoom;

public class ShadowDungeon extends AbstractGame {
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // rooms
    private String currRoom = "prep";
    private final PrepRoom prepRoom;
    private final BattleRoomA battleRoomA;
    private final BattleRoomB battleRoomB;
    private final EndRoom endRoom;
    private boolean justEntered = false;

    // player
    private final Player player;

    // player ui
    private final PlayerUI playerUI;

    public ShadowDungeon(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;
        /* Player */
        // position
        String posParams = gameProps.getProperty("player.start");
        String[] posParts = posParams.split(",");
        Point playerPosition = new Point(Double.parseDouble(posParts[0]), Double.parseDouble(posParts[1]));
        // health
        double health = Integer.parseInt(gameProps.getProperty("initialHealth"));
        // coins
        int coins = Integer.parseInt(gameProps.getProperty("initialCoins"));
        // speed
        int speed = Integer.parseInt(gameProps.getProperty("movingSpeed"));
        this.player = new Player(playerPosition, health, coins, speed);

        /* Room */
        this.prepRoom = new PrepRoom(gameProps, messageProps);
        this.battleRoomA = new BattleRoomA(gameProps);
        this.battleRoomB = new BattleRoomB(gameProps);
        this.endRoom = new EndRoom(gameProps);

        /* UI */
        this.playerUI = new PlayerUI(gameProps);

        // manage entry and exit point of door
        new ReadDoor(gameProps);
    }

    /*
    * Used for update player's position after exit door
    * @param doors - the door array which can get by method in door class
    */
    private void positionUpdate(Door[] doors) {
        for (Door door : doors) {
            if (!door.getIsLocked() && player.getBoundingBox().intersects(door.getBoundingBox())) {
                if (!justEntered) {
                    // switch to room
                    currRoom = door.getTargetRoom();
                    // update player position
                    player.setPosition(ReadDoor.getExit(door.getDoorConfig()));
                    justEntered = true;
                    door.unlock();
                }
                return;
            }
        }

        // only if player leave and retouch door above logic will be effected
        boolean touchingAny = false;
        for (Door door : doors) {
            if (player.getBoundingBox().intersects(door.getBoundingBox())) {
                touchingAny = true;
                break;
            }
        }
        if (!touchingAny) {
            if (justEntered) {
                // used for set war status and door animation
                if (currRoom.equals("A")) {
                    battleRoomA.setEncounterStarted();
                }

                if (currRoom.equals("B")) {
                    battleRoomB.setEncounterStarted();
                }
            }
            justEntered = false;
        }
    }

    // used for enemies status update
    private void enemiesUpdate(EnemyManager enemyManager) {
        enemyManager.checkCollisions(player);
    }

    /* player update
    * If no points and isWall and isRiver,
    * just use 'playerUpdate(input, new Point[0], false, new Point[0], false);'
    */
    private void playerUpdate(Input input, PlayerUpdateConfig conf) {
        player.update(input,
                conf.wallBoundaries(), conf.isWall(),
                conf.riverBoundaries(), conf.isRiver(),
                conf.restartBoundaries(), conf.isRestart(),
                conf.isBox(), conf.boxMap(), conf.openedMap());
    }

    /* Game Reset */
    private void resetGame(Properties gameProps) {
        player.reset(gameProps);
        prepRoom.reset(gameProps);
        battleRoomA.reset(gameProps);
        battleRoomB.reset(gameProps);
        endRoom.reset(gameProps);
    }

    /**
     * Render the relevant screen based on the keyboard input given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        switch (currRoom) {
            case "prep":
                prepRoom.update(input);
                positionUpdate(prepRoom.getDoors());
                playerUpdate(input, PlayerUpdateConfig.forPrep(prepRoom.getRestartPosition()));
                if (player.Restart) {
                    currRoom = "prep";
                    resetGame(GAME_PROPS);
                }
                player.Restart = false;

                /* update player image */
                if (input.wasPressed(Keys.M)) {
                    // marine
                    player.playerLeft = new Image("res/marine_left.png");
                    player.playerRight = new Image("res/marine_right.png");
                }

                if (input.wasPressed(Keys.R)) {
                    // Robot
                    player.playerLeft = new Image("res/robot_left.png");
                    player.playerRight = new Image("res/robot_right.png");
                }
                break;
            case "A":
                battleRoomA.update(input);
                positionUpdate(battleRoomA.getDoors());
                enemiesUpdate(battleRoomA.getEnemyManager());
                playerUpdate(input, PlayerUpdateConfig.forBattle(
                        battleRoomA.getWallPositions(), battleRoomA.isWall(),
                        battleRoomA.getRiverPositions(), battleRoomA.isRiver(),
                        battleRoomA.getTreasureBoxes(), battleRoomA.getTreasureStatus()
                ));
                if (player.getHealth() <= 0) {
                    currRoom = "end";
                    endRoom.closeDoor();
                }
                break;
            case "B":
                battleRoomB.update(input);
                positionUpdate(battleRoomB.getDoors());
                enemiesUpdate(battleRoomB.getEnemyManager());
                playerUpdate(input, PlayerUpdateConfig.forBattle(
                        battleRoomB.getWallPositions(), battleRoomB.isWall(),
                        battleRoomB.getRiverPositions(), battleRoomB.isRiver(),
                        battleRoomB.getTreasureBoxes(), battleRoomB.getTreasureStatus()
                ));
                if (player.getHealth() <= 0) {
                    currRoom = "end";
                    endRoom.closeDoor();
                }
                break;
            case "end":
                endRoom.update(input, player, MESSAGE_PROPS);
                positionUpdate(endRoom.getDoors());
                playerUpdate(input, PlayerUpdateConfig.forEnd(endRoom.getRestartPosition()));
                if (player.Restart) {
                    currRoom = "prep";
                    resetGame(GAME_PROPS);
                }
                player.Restart = false;
                break;
        }

        // place at last, avoid be hidden
        playerUI.draw(player);
    }

    /**
     * The main entry point of the Shadow Dungeon game.
     * <p>
     * This method loads the game properties and message files, initializes the game,
     * and starts the game loop.
     *
     * @param args Command-line arguments (not used in this game).
     */
    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile("res/app.properties");
        Properties messageProps = IOUtils.readPropertiesFile("res/message.properties");
        ShadowDungeon game = new ShadowDungeon(gameProps, messageProps);
        game.run();
    }
}
