package entities;

import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.Window;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.Map;
import java.util.Properties;

public class Player {
    private Point position;
    private double health;
    private int coins;
    private int speed;
    public Image playerLeft;
    public Image playerRight;
    private boolean facingRight;

    // game logic
    public boolean Restart = false;

    // const damage values
    public final double riverDamagePerFrame = 0.2;

    // others
    private final Image wallImage;
    private final Image riverImage;
    private final Image restartImage;
    private final Image boxImage;

    public Player(Point startPos, double health, int coins, int speed) {
        this.position = startPos;
        this.health = health;
        this.coins = coins;
        this.speed = speed;
        this.playerLeft = new Image("res/player_left.png"); // default
        this.playerRight = new Image("res/player_right.png");
        this.facingRight = true;
        // images - used for get rectangle
        this.wallImage = new Image("res/wall.png");
        this.riverImage = new Image("res/river.png");
        this.restartImage = new Image("res/restart_area.png");
        this.boxImage = new Image("res/treasure_box.png");
    }

    // player action logic
    private void move(Input input,
                      Point[] wallBoundaries,
                      boolean isWall,
                      Point[] riverBoundaries,
                      boolean isRiver,
                      Point[] restartBoundaries,
                      boolean isRestart,
                      boolean isBox,
                      Map<Point, Integer> boxMap,
                      Map<Point, Boolean> openedMap
                      ) {
        /* according to input change position */
        double x = position.x;
        double y = position.y;
        // up
        if (input.isDown(Keys.W)) {
            y -= speed;
        }

        // down
        if (input.isDown(Keys.S)) {
            y += speed;
        }

        // left
        if (input.isDown(Keys.A)) {
            x -= speed;
        }

        // right
        if (input.isDown(Keys.D)) {
            x += speed;
        }

        // boundaries
        Rectangle playerBox = playerLeft.getBoundingBoxAt(new Point(x, y));

        if (playerBox.left() < 0) {
            x = position.x;
        }

        if (playerBox.right() > Window.getWidth()) {
            x = position.x;
        }

        if (playerBox.top() < 0) {
            y = position.y;
        }

        if (playerBox.bottom() > Window.getHeight()) {
            y = position.y;
        }

        // boundaries of wall
        // get rectangle of wall
        if (isWall) {
            for (Point wp : wallBoundaries) {
                Rectangle wallBox = wallImage.getBoundingBoxAt(wp);
                if (playerBox.intersects(wallBox)) {
                    x = position.x;
                    y = position.y;
                    break;
                }
            }
        }
        // river logic
        if (isRiver) {
            for (Point wp : riverBoundaries) {
                Rectangle riverBox = riverImage.getBoundingBoxAt(wp);
                if (playerBox.intersects(riverBox)) {
                    health -= riverDamagePerFrame;
                    // System.out.println(health);
                }
            }
        }
        // restart area
        if (isRestart) {
            for (Point wp : restartBoundaries) {
                Rectangle restartBox = restartImage.getBoundingBoxAt(wp);
                if (playerBox.intersects(restartBox)) {
                    // if press enter, restart
                    if (input.wasPressed(Keys.ENTER)) {
                        Restart = true;
                    }
                }
            }
        }
        // box
        if (isBox) {
            for (Point wp : boxMap.keySet()) {
                Rectangle boxRect = boxImage.getBoundingBoxAt(wp);
                if (getBoundingBox().intersects(boxRect)) {
                    if (input.wasPressed(Keys.K) && !openedMap.get(wp)) {
                        int coinsGained = boxMap.get(wp);
                        addCoins(coinsGained);
                        openedMap.put(wp, true);
                        // System.out.println(wp + ", " + coinsGained);
                    }
                }
            }
        }
        position = new Point(x, y);
    }

    public void setPosition(Point newPosition) {
        position = newPosition;
    }

    // judge whether touch door or not
    private Image getCurrentImage() {
        return facingRight ? playerRight : playerLeft;
    }

    public Rectangle getBoundingBox() {
        return getCurrentImage().getBoundingBoxAt(position);
    }

    // draw
    public void draw() {
        /* draw player according to direction and position */
        if (facingRight) {
            playerRight.draw(position.x, position.y);
        } else {
            playerLeft.draw(position.x, position.y);
        }
    }

    // get values
    public double getHealth() { return health; }
    public int getCoins() { return coins; }
    public void addCoins(int val) { coins += val; }
    
    // damage and key methods
    public void takeDamage(double damage) { 
        health -= damage; 
    }
    
    public void takeDamage(int damage) { 
        health -= damage; 
    }
    
    public void addKey() {
        // Key count is managed by the game state
        // This method is called when player collects a key
    }

    // reset function
    public void reset(Properties gameProps) {
        String posParams = gameProps.getProperty("player.start");
        String[] posParts = posParams.split(",");
        position = new Point(Double.parseDouble(posParts[0]), Double.parseDouble(posParts[1]));
        health = Integer.parseInt(gameProps.getProperty("initialHealth"));
        coins = Integer.parseInt(gameProps.getProperty("initialCoins"));
        speed = Integer.parseInt(gameProps.getProperty("movingSpeed"));
    }

    // update function
    public void update(
            Input input,
            Point[] wallBoundaries,
            boolean isWall,
            Point[] riverBoundaries,
            boolean isRiver,
            Point[] restartBoundaries,
            boolean isRestart,
            boolean isBox,
            Map<Point, Integer> boxMap,
            Map<Point, Boolean> openedMap) {
        /* Regular update during game */
        move(input, wallBoundaries, isWall, riverBoundaries, isRiver, restartBoundaries, isRestart, isBox, boxMap, openedMap);
        Point mouse = input.getMousePosition();
        facingRight = mouse.x >= position.x;
        draw();
    }
}
