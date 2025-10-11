package entities;

import bagel.Image;
import bagel.util.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Bullet Kin enemy - stationary, shoots fireballs
 */
public class BulletKin extends Enemy {
    private static final String BULLET_KIN_IMAGE = "res/bullet_kin.png";
    private static final int MAX_HEALTH = 100;
    private static final int FIRE_RATE = 360; // frames between shots
    private static final double CONTACT_DAMAGE = 0.2; // per frame
    private static final int COIN_REWARD = 10;
    private static final int ROBOT_BONUS = 5;
    
    private int framesSinceLastShot;
    private List<Fireball> fireballs;
    private Point playerPosition; // track player for targeting
    
    /**
     * Create BulletKin at position
     */
    public BulletKin(Point position) {
        super(new Image(BULLET_KIN_IMAGE), position, MAX_HEALTH);
        this.framesSinceLastShot = 0;
        this.fireballs = new ArrayList<>();
        
        // Fire immediately when spawned
        this.framesSinceLastShot = FIRE_RATE;
    }
    
    @Override
    public void update(Point playerPos) {
        if (!isAlive()) return;
        
        this.playerPosition = playerPos;
        framesSinceLastShot++;
        
        // Fire fireball if enough time passed
        if (framesSinceLastShot >= FIRE_RATE) {
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
            Fireball fireball = new Fireball(getPosition(), playerPosition);
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
        // Clear fireballs when enemy dies
        clearFireballs();
    }
}