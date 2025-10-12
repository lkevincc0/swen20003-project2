package entities;

import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.MouseButtons;
import bagel.Window;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Player {
    private Point position;
    private double health;
    private int coins;
    private int keys; // key count
    private int speed;
    public Image playerLeft;
    public Image playerRight;
    private boolean facingRight;

    // Shooting system
    private final List<Bullet> bullets;
    private int weaponLevel; // 0=Standard, 1=Advanced, 2=Elite
    private int framesSinceLastShot;
    private final int fireRate;
    private final int[] weaponDamage;
    private boolean characterSelected; // can only shoot after selecting character
    private boolean isMarine = false; // track if Marine character
    private final Properties gameProps;

    // game logic
    public boolean Restart = false;

    // const damage values
    public final double riverDamagePerFrame = 0.2;

    // others
    private final Image wallImage;
    private final Image riverImage;
    private final Image restartImage;
    private final Image boxImage;

    public Player(Point startPos, double health, int coins, int speed, Properties gameProps) {
        this.position = startPos;
        this.health = health;
        this.coins = coins;
        this.keys = 0;
        this.speed = speed;
        this.gameProps = gameProps;
        this.fireRate = Integer.parseInt(gameProps.getProperty("bulletFreq"));
        this.weaponDamage = new int[]{
            Integer.parseInt(gameProps.getProperty("weaponStandardDamage")),
            Integer.parseInt(gameProps.getProperty("weaponAdvanceDamage")),
            Integer.parseInt(gameProps.getProperty("weaponEliteDamage"))
        };
        this.playerLeft = new Image("res/player_left.png"); // default
        this.playerRight = new Image("res/player_right.png");
        this.facingRight = true;
        
        // Initialize shooting system
        this.bullets = new ArrayList<>();
        this.weaponLevel = 0; // Standard weapon
        this.framesSinceLastShot = 0;
        this.characterSelected = false;
        
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
                    // Marine is immune to river damage
                    if (!isMarine) {
                        health -= riverDamagePerFrame;
                    }
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

    public Point getPosition() {
        return position;
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
    
    public void addHealth(int amount) {
        health += amount;
    }
    
    public void addKey() {
        keys++;
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

    // Shooting system methods
    public void setCharacterSelected(boolean selected) {
        this.characterSelected = selected;
    }

    public boolean canShoot() {
        return characterSelected && framesSinceLastShot >= fireRate;
    }

    public void setMarine(boolean marine) {
        this.isMarine = marine;
    }
    
    public void shoot(Point mousePosition) {
        if (!canShoot()) return;
        
        // Create bullet immediately on click
        Bullet bullet = new Bullet(position, mousePosition, weaponDamage[weaponLevel], gameProps);
        bullets.add(bullet);
        framesSinceLastShot = 0; // Reset fire rate timer
    }
    
    public void updateBullets() {
        bullets.removeIf(bullet -> {
            bullet.update();
            return bullet.isActive();
        });
    }
    
    public void drawBullets() {
        for (Bullet bullet : bullets) {
            bullet.draw();
        }
    }
    
    public List<Bullet> getBullets() {
        return new ArrayList<>(bullets);
    }

    // Check bullet collisions with obstacles
    public void checkBulletObstacleCollisions(Wall wall, Table table, Basket basket, Door door1, Door door2) {
        bullets.removeIf(bullet -> {
            if (bullet.isActive()) return true;
            
            // Check collision with walls
            if (wall.isWall()) {
                for (Point wallPos : wall.getPositions()) {
                    if (bullet.getBoundingBox().intersects(wall.getBoundingBoxAt(wallPos))) {
                        bullet.destroy();
                        return true;
                    }
                }
            }
            
            // Check collision with table
            if (table.isDestroyed() && bullet.getBoundingBox().intersects(table.getBoundingBox())) {
                bullet.destroy();
                table.destroy(); // Destroy table
                return true;
            }
            
            // Check collision with basket
            if (basket.isDestroyed() && bullet.getBoundingBox().intersects(basket.getBoundingBox())) {
                bullet.destroy();
                basket.destroy(); // Destroy basket
                this.addCoins(basket.getCoinReward()); // Give coins to player
                return true;
            }
            
            // Check collision with locked doors
            if (door1.getIsLocked() && bullet.getBoundingBox().intersects(door1.getBoundingBox())) {
                bullet.destroy();
                return true;
            }
            
            if (door2.getIsLocked() && bullet.getBoundingBox().intersects(door2.getBoundingBox())) {
                bullet.destroy();
                return true;
            }
            
            return false;
        });
    }
    
    public int getWeaponLevel() {
        return weaponLevel;
    }
    
    public void upgradeWeapon() {
        if (weaponLevel < 2) {
            weaponLevel++;
        }
    }
    
    public int getKeys() {
        return keys;
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
        
        // Update fire rate timer
        framesSinceLastShot++;
        
        // Handle shooting
        if (input.wasPressed(MouseButtons.LEFT)) {
            shoot(mouse);
        }
        
        // Update bullets
        updateBullets();
        
        draw();
        drawBullets();
    }
    
    // Draw only method for paused state
    public void drawOnly() {
        draw();
        drawBullets();
    }
}
