package entities;

import bagel.Image;
import bagel.util.Point;

import java.util.Properties;

public class Table {
    private final Image tableImage;
    private final Point tablePoint;
    private boolean destroyed;

    public Table(Properties gameProps, String doorConfig) {
        this.tableImage = new Image("res/table.png");
        this.destroyed = false;
        String[] parts = gameProps.getProperty(doorConfig).split(",");
        this.tablePoint = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }

    public void draw() {
        if (!destroyed) {
            tableImage.draw(tablePoint.x, tablePoint.y);
        }
    }

    public Point getPosition() {
        return tablePoint;
    }

    public bagel.util.Rectangle getBoundingBox() {
        return tableImage.getBoundingBoxAt(tablePoint);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        this.destroyed = true;
    }
}
