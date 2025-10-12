package entities;

import bagel.Image;
import bagel.util.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Ashen Bullet Kin enemy - stronger variant of Bullet Kin
 */
public class AshenBulletKin extends Enemy {
    private static final String ASHEN_BULLET_KIN_IMAGE = "res/ashen_bullet_kin.png";

    private final int fireRate;
    private final double contactDamage;
    private final int coinReward;
    private final int robotBonus;
    
    private int framesSinceLastShot;
    private final List<Fireball> fireballs;
    private Point playerPosition; // track player for targeting
    private final Properties gameProps;
    
    /**
     * Create AshenBulletKin at position
     */
    public AshenBulletKin(Point position, Properties gameProps) {
        super(new Image(ASHEN_BULLET_KIN_IMAGE), position, Integer.parseInt(gameProps.getProperty("ashenBulletKinHealth")));
        this.fireRate = Integer.parseInt(gameProps.getProperty("ashenBulletKinShootFrequency"));
        this.contactDamage = 0.2; // Contact damage per frame (not in properties)
        this.coinReward = Integer.parseInt(gameProps.getProperty("ashenBulletKinCoin"));
        this.robotBonus = Integer.parseInt(gameProps.getProperty("robotExtraCoin"));
        
        this.framesSinceLastShot = 0;
        this.fireballs = new ArrayList<>();
        this.gameProps = gameProps;
        
        // Fire immediately when spawned
        this.framesSinceLastShot = fireRate;
    }
    
    @Override
    public void update(Point playerPos) {
        if (!isAlive()) return;
        
        this.playerPosition = playerPos;
        framesSinceLastShot++;
        
        // Fire fireball if enough time passed
        if (framesSinceLastShot >= fireRate) {
            fireFireball();
            framesSinceLastShot = 0;
        }
        
        // Update fireballs
        fireballs.removeIf(fireball -> {
            fireball.update();
            return !fireball.isActive();
        });
    }
    
    /**
     * Fire fireball at player
     */
    private void fireFireball() {
        if (playerPosition != null) {
            Fireball fireball = new Fireball(getPosition(), playerPosition, gameProps);
            fireballs.add(fireball);
        }
    }
    
    @Override
    public void draw() {
        super.draw(); // draw enemy sprite
        
        // Draw fireballs
        for (Fireball fireball : fireballs) {
            fireball.draw();
        }
    }
    
    /**
     * Get all active fireballs
     */
    public List<Fireball> getFireballs() {
        return new ArrayList<>(fireballs);
    }
    
    /**
     * Clear all fireballs
     */
    public void clearFireballs() {
        fireballs.clear();
    }
    
    /**
     * Check fireball collisions with obstacles
     */
    public void checkFireballObstacleCollisions(Wall wall, Table table, Basket basket, Door door1, Door door2) {
        fireballs.removeIf(fireball -> {
            if (!fireball.isActive()) return true;
            
            // Check collision with walls
            if (wall.isWall()) {
                for (Point wallPos : wall.getPositions()) {
                    if (fireball.getBoundingBox().intersects(wall.getBoundingBoxAt(wallPos))) {
                        fireball.destroy();
                        return true;
                    }
                }
            }
            
            // Check collision with table (only if not destroyed)
            if (table.isDestroyed() && fireball.getBoundingBox().intersects(table.getBoundingBox())) {
                fireball.destroy();
                return true;
            }
            
            // Check collision with basket (only if not destroyed)
            if (basket.isDestroyed() && fireball.getBoundingBox().intersects(basket.getBoundingBox())) {
                fireball.destroy();
                return true;
            }
            
            // Check collision with locked doors
            if (door1.getIsLocked() && fireball.getBoundingBox().intersects(door1.getBoundingBox())) {
                fireball.destroy();
                return true;
            }
            
            if (door2.getIsLocked() && fireball.getBoundingBox().intersects(door2.getBoundingBox())) {
                fireball.destroy();
                return true;
            }
            
            return false;
        });
    }
    
    @Override
    public double getContactDamage() {
        return contactDamage;
    }
    
    @Override
    public int getCoinReward() {
        return coinReward;
    }
    
    @Override
    public int getRobotBonus() {
        return robotBonus;
    }
    
    @Override
    public void onDeath() {
        // Clear fireballs when enemy dies
        clearFireballs();
    }
}