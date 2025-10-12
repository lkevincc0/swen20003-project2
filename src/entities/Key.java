package entities;

import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Key collectible item
 */
public class Key {
    private static final String KEY_IMAGE = "res/key.png";
    
    private final Image image;
    private final Point position;
    private boolean collected;
    
    /**
     * Create key at position
     */
    public Key(Point position) {
        this.image = new Image(KEY_IMAGE);
        this.position = position;
        this.collected = false;
    }
    
    /**
     * Draw key if not collected
     */
    public void draw() {
        if (!collected) {
            image.draw(position.x, position.y);
        }
    }
    
    /**
     * Get key position
     */
    public Point getPosition() {
        return position;
    }
    
    /**
     * Get key bounding box
     */
    public Rectangle getBoundingBox() {
        return image.getBoundingBoxAt(position);
    }
    
    /**
     * Check if key is collected
     */
    public boolean isCollected() {
        return collected;
    }
    
    /**
     * Collect the key
     */
    public void collect() {
        collected = true;
    }
}