package rooms;

import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.Window;
import bagel.util.Point;
import entities.*;

import java.util.Map;
import java.util.Properties;

public class BattleRoomB {
    private final Image background;
    // door
    private Door doorToA;
    private Door doorToEnd;
    // enemy manager
    private EnemyManager enemyManager;
    // wall
    private final Wall wall;
    // river
    private final River river;
    // box
    private TreasureBox box;
    // basket
    private Basket basket;
    // table
    private Table table;

    private boolean encounterStarted = false;

    public BattleRoomB(Properties gameProps) {
        // background
        this.background = new Image("res/background.png");

        // door to prep
        this.doorToA = new Door(gameProps, "primarydoor.B");
        this.doorToEnd = new Door(gameProps, "secondarydoor.B");

        // enemies
        this.enemyManager = new EnemyManager(gameProps, "B");

        // wall
        this.wall = new Wall(gameProps, "wall.B");

        // river
        this.river = new River(gameProps, "river.B");

        // door
        this.box = new TreasureBox(gameProps, "treasurebox.B");

        // basket
        this.basket = new Basket(gameProps, "basket.B");

        // table
        this.table = new Table(gameProps, "table.B");
    }

    /* Fetch the entities */
    public Door[] getDoors() {
        return new Door[]{doorToA, doorToEnd};
    }

    public EnemyManager getEnemyManager() { return enemyManager; }

    // war status
    public void setEncounterStarted() {
        if (!encounterStarted) {
            encounterStarted = true;
        }
    }

    // wall status
    public boolean isWall() { return wall.isWall(); }

    // river status
    public boolean isRiver() { return river.isRiver(); }

    // get all positions of wall, exposed to players
    public Point[] getWallPositions() { return wall.getPositions(); }

    // get all positions of river
    public Point[] getRiverPositions() { return river.getPositions(); }

    // get box maps
    public Map<Point, Integer> getTreasureBoxes() {
        return box.getBoxMap();
    }

    public Map<Point, Boolean> getTreasureStatus() {
        return box.getOpenedMap();
    }
    // reset
    public void reset(Properties gameProps) {
        enemyManager = new EnemyManager(gameProps, "B");
        doorToA = new Door(gameProps, "primarydoor.B");
        doorToEnd = new Door(gameProps, "secondarydoor.B");
        box = new TreasureBox(gameProps, "treasurebox.B");
    }

    public void update(Input input) {
        // draw background
        background.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        // door
        if (input.wasPressed(Keys.R) && !enemyManager.hasAliveEnemies()) {
            doorToA.doDoor();
            doorToEnd.doDoor();
        }

        if (encounterStarted) {
            doorToA.lock();
            doorToEnd.lock();
        }

        // enemies and doors animation
        if (enemyManager.hasAliveEnemies() && encounterStarted) {
            enemyManager.draw();
        } else {
            if (!encounterStarted) {
                doorToA.unlock();
            }
        }

        if (!enemyManager.hasAliveEnemies()) {
            encounterStarted = false;
            doorToA.unlock();
            doorToEnd.unlock();
        }

        doorToA.draw();
        doorToEnd.draw();

        // wall
        wall.draw();

        // rivers
        river.draw();

        // box
        box.draw();

        // basket
        basket.draw();

        // table
        table.draw();
    }
}
