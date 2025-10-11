package entities;

import bagel.Image;
import bagel.util.Point;

import java.util.Properties;

public class Table {
    private final Image tableImage;
    private final Point tablePoint;

    public Table(Properties gameProps, String doorConfig) {
        this.tableImage = new Image("res/table.png");
        String[] parts = gameProps.getProperty(doorConfig).split(",");
        this.tablePoint = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }

    public void draw() {
        tableImage.draw(tablePoint.x, tablePoint.y);
    }

    public Point getPosition() {
        return tablePoint;
    }

    public bagel.util.Rectangle getBoundingBox() {
        return tableImage.getBoundingBoxAt(tablePoint);
    }
}
