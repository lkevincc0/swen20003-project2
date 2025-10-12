package entities;

import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;
import java.util.Properties;

/**
 * Player bullet projectile
 */
public class Bullet {
    private static final String BULLET_IMAGE = "res/fireball.png"; // use fireball as bullet image
    private final double speed;
    
    private Image image;
    private Point position;
    private Vector2 direction;
    private boolean active;
    private int damage;
    
    /**
     * Create bullet at position targeting mouse
     */
    public Bullet(Point startPos, Point targetPos, int damage, Properties gameProps) {
        this.speed = Double.parseDouble(gameProps.getProperty("bulletSpeed"));
        this.image = new Image(BULLET_IMAGE);
        this.position = startPos;
        this.active = true;
        this.damage = damage;
        
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
     * Update bullet position
     */
    public void update() {
        if (!active) return;
        
        // Move bullet
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
     * Draw bullet
     */
    public void draw() {
        if (active) {
            image.draw(position.x, position.y);
        }
    }
    
    /**
     * Get bullet position
     */
    public Point getPosition() {
        return position;
    }
    
    /**
     * Get bullet bounding box
     */
    public bagel.util.Rectangle getBoundingBox() {
        return image.getBoundingBoxAt(position);
    }
    
    /**
     * Check if bullet is active
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Destroy bullet
     */
    public void destroy() {
        active = false;
    }
    
    /**
     * Get bullet damage
     */
    public int getDamage() {
        return damage;
    }
}