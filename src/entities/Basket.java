package entities;

import bagel.Image;
import bagel.util.Point;

import java.util.Properties;

// Basket
public class Basket {
    private final Image basketImage;
    private final Point basketPoint;
    private boolean destroyed;
    private final int coinReward;

    public Basket(Properties gameProps, String basketConfig) {
        this.basketImage = new Image("res/basket.png");
        this.destroyed = false;
        this.coinReward = Integer.parseInt(gameProps.getProperty("basketCoin"));

        String[] parts = gameProps.getProperty(basketConfig).split(",");
        this.basketPoint = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }

    public void draw() {
        if (!destroyed) {
            basketImage.draw(basketPoint.x, basketPoint.y);
        }
    }

    public Point getPosition() {
        return basketPoint;
    }

    public bagel.util.Rectangle getBoundingBox() {
        return basketImage.getBoundingBoxAt(basketPoint);
    }

    public boolean isDestroyed() {
        return !destroyed;
    }

    public void destroy() {
        this.destroyed = true;
    }

    public int getCoinReward() {
        return coinReward;
    }
}
