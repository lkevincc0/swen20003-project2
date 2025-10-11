package entities;

import bagel.util.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Manages multiple enemies
 */
public class EnemyManager {
    private final List<Enemy> enemies;
    private final List<Key> droppedKeys;

    public EnemyManager(Properties gameProps, String roomId) {
        this.enemies = new ArrayList<>();
        this.droppedKeys = new ArrayList<>();
        
        // Load all enemy types for the room
        loadBulletKins(gameProps, "bulletKin." + roomId);
        loadAshenBulletKins(gameProps, "ashenBulletKin." + roomId);
        loadKeyBulletKins(gameProps, "keyBulletKin." + roomId);
    }
    
    /**
     * Load BulletKin enemies from config
     */
    private void loadBulletKins(Properties gameProps, String configKey) {
        String raw = gameProps.getProperty(configKey);
        if (raw != null && !raw.equals("0")) {
            String[] pairs = raw.split(";");
            for (String pair : pairs) {
                String[] xy = pair.split(",");
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                Point position = new Point(x, y);
                enemies.add(new BulletKin(position));
            }
        }
    }
    
    /**
     * Load AshenBulletKin enemies from config
     */
    private void loadAshenBulletKins(Properties gameProps, String configKey) {
        String raw = gameProps.getProperty(configKey);
        if (raw != null && !raw.equals("0")) {
            String[] pairs = raw.split(";");
            for (String pair : pairs) {
                String[] xy = pair.split(",");
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                Point position = new Point(x, y);
                enemies.add(new AshenBulletKin(position));
            }
        }
    }
    
    /**
     * Load KeyBulletKin enemies from config
     */
    private void loadKeyBulletKins(Properties gameProps, String configKey) {
        String raw = gameProps.getProperty(configKey);
        if (raw != null && !raw.equals("0")) {
            String[] pairs = raw.split(";");
            List<Point> path = new ArrayList<>();
            for (String pair : pairs) {
                String[] xy = pair.split(",");
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                path.add(new Point(x, y));
            }
            if (!path.isEmpty()) {
                enemies.add(new KeyBulletKin(path.get(0), path));
            }
        }
    }

    /**
     * Update all enemies
     */
    public void update(Point playerPosition) {
        for (Enemy enemy : enemies) {
            enemy.update(playerPosition);
            
            // Check for dropped keys from KeyBulletKin
            if (!enemy.isAlive() && enemy instanceof KeyBulletKin) {
                KeyBulletKin keyEnemy = (KeyBulletKin) enemy;
                if (keyEnemy.hasDroppedKey() && !droppedKeys.contains(keyEnemy.getDroppedKey())) {
                    droppedKeys.add(keyEnemy.getDroppedKey());
                }
            }
        }
        
        // Remove collected keys
        droppedKeys.removeIf(Key::isCollected);
    }

    /**
     * Check collisions with player
     */
    public void checkCollisions(Player player) {
        // Check enemy collisions
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && player.getBoundingBox().intersects(enemy.getBoundingBox())) {
                // Apply contact damage
                player.takeDamage(enemy.getContactDamage());
            }
        }
        
        // Check fireball collisions
        for (Enemy enemy : enemies) {
            if (enemy instanceof BulletKin) {
                BulletKin bulletKin = (BulletKin) enemy;
                for (Fireball fireball : bulletKin.getFireballs()) {
                    if (fireball.isActive() && player.getBoundingBox().intersects(fireball.getBoundingBox())) {
                        player.takeDamage(fireball.getDamage());
                        fireball.destroy();
                    }
                }
            } else if (enemy instanceof AshenBulletKin) {
                AshenBulletKin ashenBulletKin = (AshenBulletKin) enemy;
                for (Fireball fireball : ashenBulletKin.getFireballs()) {
                    if (fireball.isActive() && player.getBoundingBox().intersects(fireball.getBoundingBox())) {
                        player.takeDamage(fireball.getDamage());
                        fireball.destroy();
                    }
                }
            }
        }
        
        // Check key collection
        for (Key key : droppedKeys) {
            if (!key.isCollected() && player.getBoundingBox().intersects(key.getBoundingBox())) {
                key.collect();
                player.addKey();
            }
        }
    }
    
    /**
     * Check bullet collisions with enemies
     */
    public void checkBulletCollisions(Player player) {
        for (Bullet bullet : player.getBullets()) {
            if (!bullet.isActive()) continue;
            
            // Check collision with enemies
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && bullet.getBoundingBox().intersects(enemy.getBoundingBox())) {
                    // Damage enemy
                    enemy.takeDamage(bullet.getDamage());
                    bullet.destroy();
                    
                    // Award coins if enemy dies
                    if (!enemy.isAlive()) {
                        int coinReward = enemy.getCoinReward();
                        // Add robot bonus if applicable
                        if (player.playerLeft.toString().contains("robot")) {
                            coinReward += enemy.getRobotBonus();
                        }
                        player.addCoins(coinReward);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Draw all enemies and keys
     */
    public void draw() {
        for (Enemy enemy : enemies) {
            enemy.draw();
        }
        
        for (Key key : droppedKeys) {
            key.draw();
        }
    }

    /**
     * Get all enemies
     */
    public List<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Check if any enemies are alive
     */
    public boolean hasAliveEnemies() {
        return enemies.stream().anyMatch(Enemy::isAlive);
    }
    
    /**
     * Get all dropped keys
     */
    public List<Key> getDroppedKeys() {
        return droppedKeys;
    }
    
    /**
     * Clear all fireballs when leaving room
     */
    public void clearFireballs() {
        for (Enemy enemy : enemies) {
            if (enemy instanceof BulletKin) {
                ((BulletKin) enemy).clearFireballs();
            } else if (enemy instanceof AshenBulletKin) {
                ((AshenBulletKin) enemy).clearFireballs();
            }
        }
    }
}