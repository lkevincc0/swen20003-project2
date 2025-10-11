package entities;

import bagel.Font;
import bagel.util.Point;
import java.util.Properties;

public class PlayerUI {
    // stat
    private final Point healthStat;
    private final Point coinStat;
    private final Point weaponStat;
    private final Point keyStat;

    // font
    private final Font font;

    public PlayerUI(Properties gameProps) {
        // stat
        String[] healthPart = gameProps.getProperty("healthStat").split(",");
        String[] coinPart = gameProps.getProperty("coinStat").split(",");
        String[] weaponPart = gameProps.getProperty("weaponStat").split(",");
        String[] keyPart = gameProps.getProperty("keyStat").split(",");
        this.healthStat = new Point(Double.parseDouble(healthPart[0]), Double.parseDouble(healthPart[1]));
        this.coinStat = new Point(Double.parseDouble(coinPart[0]), Double.parseDouble(coinPart[1]));
        this.weaponStat = new Point(Double.parseDouble(weaponPart[0]), Double.parseDouble(weaponPart[1]));
        this.keyStat = new Point(Double.parseDouble(keyPart[0]), Double.parseDouble(keyPart[1]));

        // font
        String fontFile = gameProps.getProperty("font");
        int fontSize = Integer.parseInt(gameProps.getProperty("playerStats.fontSize"));
        this.font = new Font(fontFile, fontSize);
    }

    public void draw(Player player) {
        if (player.getHealth() >= 0) {
            font.drawString("HEALTH" + " " + String.format("%.1f", player.getHealth()),
                    healthStat.x, healthStat.y);
        } else {
            font.drawString("HEALTH" + "-0.0" , healthStat.x, healthStat.y);
        }
        font.drawString("COINS" + " " + player.getCoins(), coinStat.x, coinStat.y);
        font.drawString("WEAPON LEVEL" + " " + player.getCoins(), weaponStat.x, weaponStat.y);
        font.drawString("KEYS" + " " + player.getCoins(), keyStat.x, keyStat.y);
    }
}
