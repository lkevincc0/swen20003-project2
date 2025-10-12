package entities;

import bagel.Image;
import bagel.util.Point;
import java.util.List;
import java.util.Properties;

/**
 * Key Bullet Kin enemy - moves along path, drops key on death
 */
public class KeyBulletKin extends Enemy {
    private static final String KEY_BULLET_KIN_IMAGE = "res/key_bullet_kin.png";
    private final double movementSpeed;
    private final double contactDamage;
    private final int coinReward;
    private final int robotBonus;
    
    private final List<Point> movementPath;
    private int currentTargetIndex;
    private Key droppedKey;
    
    /**
     * Create KeyBulletKin with movement path
     */
    public KeyBulletKin(Point startPosition, List<Point> path, Properties gameProps) {
        super(new Image(KEY_BULLET_KIN_IMAGE), startPosition, Integer.parseInt(gameProps.getProperty("keyBulletKinHealth")));
        this.movementSpeed = Double.parseDouble(gameProps.getProperty("keyBulletKinSpeed"));
        this.contactDamage = 0.2; // Contact damage per frame (not in properties)
        this.coinReward = 0; // KeyBulletKin doesn't give coins directly
        this.robotBonus = 0; // KeyBulletKin doesn't give robot bonus
        this.movementPath = path;
        this.currentTargetIndex = 0;
        this.droppedKey = null;
        
        // Set initial position to first path point if available
        if (!path.isEmpty()) {
            setPosition(path.get(0));
        }
    }
    
    @Override
    public void update(Point playerPos) {
        if (!isAlive()) {
            // Draw dropped key if exists
            if (droppedKey != null) {
                droppedKey.draw();
            }
            return;
        }
        
        // Move along path
        moveAlongPath();
    }
    
    /**
     * Move enemy along predefined path
     */
    private void moveAlongPath() {
        if (movementPath.isEmpty()) return;
        
        Point currentPos = getPosition();
        Point target = movementPath.get(currentTargetIndex);
        
        // Calculate direction to target
        double dx = target.x - currentPos.x;
        double dy = target.y - currentPos.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance <= movementSpeed) {
            // Reached target, move to next point
            setPosition(target);
            currentTargetIndex = (currentTargetIndex + 1) % movementPath.size();
        } else {
            // Move towards target
            double moveX = (dx / distance) * movementSpeed;
            double moveY = (dy / distance) * movementSpeed;
            setPosition(new Point(currentPos.x + moveX, currentPos.y + moveY));
        }
    }
    
    @Override
    public void draw() {
        super.draw(); // draw enemy sprite
        
        // Draw dropped key if exists and enemy is dead
        if (!isAlive() && droppedKey != null) {
            droppedKey.draw();
        }
    }
    
    /**
     * Get dropped key (if any)
     */
    public Key getDroppedKey() {
        return droppedKey;
    }
    
    /**
     * Check if key has been dropped
     */
    public boolean hasDroppedKey() {
        return droppedKey != null;
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
        // Drop key at current position
        droppedKey = new Key(getPosition());
    }
}