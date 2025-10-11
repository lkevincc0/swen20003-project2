package entities;

import bagel.Image;
import bagel.util.Point;
import java.util.List;

/**
 * Key Bullet Kin enemy - moves along path, drops key on death
 */
public class KeyBulletKin extends Enemy {
    private static final String KEY_BULLET_KIN_IMAGE = "res/key_bullet_kin.png";
    private static final int MAX_HEALTH = 30;
    private static final double MOVEMENT_SPEED = 4.0; // pixels per frame
    private static final double CONTACT_DAMAGE = 0.2; // per frame
    private static final int COIN_REWARD = 0; // no coins, drops key instead
    private static final int ROBOT_BONUS = 0;
    
    private List<Point> movementPath;
    private int currentTargetIndex;
    private Key droppedKey;
    
    /**
     * Create KeyBulletKin with movement path
     */
    public KeyBulletKin(Point startPosition, List<Point> path) {
        super(new Image(KEY_BULLET_KIN_IMAGE), startPosition, MAX_HEALTH);
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
        
        if (distance <= MOVEMENT_SPEED) {
            // Reached target, move to next point
            setPosition(target);
            currentTargetIndex = (currentTargetIndex + 1) % movementPath.size();
        } else {
            // Move towards target
            double moveX = (dx / distance) * MOVEMENT_SPEED;
            double moveY = (dy / distance) * MOVEMENT_SPEED;
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
        return CONTACT_DAMAGE;
    }
    
    @Override
    public int getCoinReward() {
        return COIN_REWARD;
    }
    
    @Override
    public int getRobotBonus() {
        return ROBOT_BONUS;
    }
    
    @Override
    public void onDeath() {
        // Drop key at current position
        droppedKey = new Key(getPosition());
    }
}