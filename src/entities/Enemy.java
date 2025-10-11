package entities;

import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Abstract base class for enemies
 */
public abstract class Enemy {
    protected boolean alive = true;
    protected Image body;
    protected Point position;
    protected int health;
    protected int maxHealth;

    public Enemy(Image image, Point position, int maxHealth) {
        this.body = image;
        this.position = position;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public Rectangle getBoundingBox() {
        return body.getBoundingBoxAt(position);
    }

    public boolean isAlive() { 
        return alive && health > 0; 
    }
    
    public void kill() { 
        alive = false;
        onDeath();
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            kill();
        }
    }

    public void draw() {
        if (isAlive()) {
            body.draw(position.x, position.y);
        }
    }

    public Point getPosition() {
        return position;
    }
    
    public void setPosition(Point newPosition) {
        this.position = newPosition;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }

    // Abstract methods for subclasses
    public abstract void update(Point playerPosition);
    public abstract double getContactDamage();
    public abstract int getCoinReward();
    public abstract int getRobotBonus();
    public abstract void onDeath();
}