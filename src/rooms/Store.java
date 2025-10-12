package rooms;

import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.Window;
import bagel.util.Point;
import entities.Player;

import java.util.Properties;

/**
 * Store system for purchasing upgrades
 */
public class Store {
    private boolean isVisible;
    private final Image background;
    private final Font font;
    private final Point storePosition;
    private final int weaponUpgradeCost;
    private final int healthPurchaseCost;
    private final int healthBonus;
    
    public Store(Properties gameProps) {
        this.isVisible = false;
        this.background = new Image("res/store.png");
        this.font = new Font(gameProps.getProperty("font"), 24);

        this.weaponUpgradeCost = Integer.parseInt(gameProps.getProperty("weaponPurchase"));
        this.healthPurchaseCost = Integer.parseInt(gameProps.getProperty("healthPurchase"));
        this.healthBonus = Integer.parseInt(gameProps.getProperty("healthBonus"));
        
        // Get store position from properties
        String storePos = gameProps.getProperty("store");
        String[] parts = storePos.split(",");
        this.storePosition = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }
    
    public void toggle() {
        isVisible = !isVisible;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void hide() {
        isVisible = false;
    }
    
    public void update(Input input, Player player) {
        if (!isVisible) return;
        
        // Handle store purchases
        if (input.wasPressed(Keys.L)) {
            // Upgrade weapon
            if (player.getCoins() >= weaponUpgradeCost && player.getWeaponLevel() < 2) {
                player.addCoins(-weaponUpgradeCost);
                player.upgradeWeapon();
            }
        }
        
        if (input.wasPressed(Keys.E)) {
            // Purchase health
            if (player.getCoins() >= healthPurchaseCost) {
                player.addCoins(-healthPurchaseCost);
                player.addHealth(healthBonus);
            }
        }
        
        if (input.wasPressed(Keys.P)) {
            // Restart game - this will be handled by main game loop
            player.Restart = true;
        }
    }
    
    public void draw() {
        if (!isVisible) return;
        
        // Draw store at specified position
        background.draw(storePosition.x, storePosition.y);
    }
}