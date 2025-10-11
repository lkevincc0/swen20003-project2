package entities;

import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;

/**
 * Fireball projectile fired by enemies
 */
public class Fireball {
    private static final String FIREBALL_IMAGE = "res/bullet_kin.png"; // TODO: replace with fireball.png when available
    private static final double SPEED = 4.0; // pixels per frame
    private static final int DAMAGE = 40; // damage to player
    
    private Image image;
    private Point position;
    private Vector2 direction;
    private boolean active;
    
    /**
     * Create fireball at position targeting player
     */
    public Fireball(Point startPos, Point targetPos) {
        this.image = new Image(FIREBALL_IMAGE);
        this.position = startPos;
        this.active = true;
        
        // Calculate direction vector
        double dx = targetPos.x - startPos.x;
        double dy = targetPos.y - startPos.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            this.direction = new Vector2(dx / distance, dy / distance);
        } else {
            this.direction = new Vector2(1, 0); // default direction
        }
    }
    
    /**
     * Update fireball position
     */
    public void update() {
        if (!active) return;
        
        // Move fireball
        position = new Point(
            position.x + direction.x * SPEED,
            position.y + direction.y * SPEED
        );
        
        // Check screen boundaries
        if (position.x < 0 || position.x > 1024 || 
            position.y < 0 || position.y > 768) {
            active = false;
        }
    }
    
    /**
     * Draw fireball
     */
    public void draw() {
        if (active) {
            image.draw(position.x, position.y);
        }
    }
    
    /**
     * Get fireball position
     */
    public Point getPosition() {
        return position;
    }
    
    /**
     * Get fireball bounding box
     */
    public bagel.util.Rectangle getBoundingBox() {
        return image.getBoundingBoxAt(position);
    }
    
    /**
     * Check if fireball is active
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Deactivate fireball
     */
    public void destroy() {
        active = false;
    }
    
    /**
     * Get damage value
     */
    public int getDamage() {
        return DAMAGE;
    }
}