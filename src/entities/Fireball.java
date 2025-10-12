package entities;

import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;
import java.util.Properties;

/**
 * Fireball projectile fired by enemies
 */
public class Fireball {
    private static final String FIREBALL_IMAGE = "res/fireball.png";
    
    private final Image image;
    private Point position;
    private final Vector2 direction;
    private boolean active;
    private final double speed;
    private final int damage;
    
    /**
     * Create fireball at position targeting player
     */
    public Fireball(Point startPos, Point targetPos, Properties gameProps) {
        this.image = new Image(FIREBALL_IMAGE);
        this.position = startPos;
        this.active = true;
        
        // Read properties
        this.speed = Double.parseDouble(gameProps.getProperty("fireballSpeed"));
        this.damage = Integer.parseInt(gameProps.getProperty("fireballDamage"));
        
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
            position.x + direction.x * speed,
            position.y + direction.y * speed
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
        return damage;
    }
}