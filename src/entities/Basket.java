package entities;

import bagel.Image;
import bagel.util.Point;

import java.util.Properties;

public class Basket {
    private final Image basketImage;
    private final Point basketPoint;

    public Basket(Properties gameProps, String basketConfig) {
        this.basketImage = new Image("res/basket.png");

        String[] parts = gameProps.getProperty(basketConfig).split(",");
        this.basketPoint = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }

    public void draw() {
        basketImage.draw(basketPoint.x, basketPoint.y);
    }

    public Point getPosition() {
        return basketPoint;
    }

    public bagel.util.Rectangle getBoundingBox() {
        return basketImage.getBoundingBoxAt(basketPoint);
    }
}
