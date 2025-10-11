package entities;

import bagel.Image;
import bagel.util.Point;
import java.util.Properties;

public class Wall {
    private final Image wallImage;
    private final Point[] positions;

    public Wall(Properties gameProps, String wallConfig) {
        this.wallImage = new Image("res/wall.png");
        String raw = gameProps.getProperty(wallConfig);
        if (raw == null || raw.equals("0")) {
            this.positions = new Point[0];   // No Wall
        } else {
            // first split by ';'
            String[] pairs = raw.split(";");
            this.positions = new Point[pairs.length];

            // second split by ','
            for (int i = 0; i < pairs.length; i++) {
                String[] xy = pairs[i].split(",");
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                this.positions[i] = new Point(x, y);
            }
        }
    }

    // get all positions, exposed to player
    public Point[] getPositions() { return positions; }

    // get wall status
    public boolean isWall() {
        return positions.length != 0;
    }

    // draw
    public void draw() {
        if (positions == null) { return; }
        for (Point position: positions) {
            wallImage.draw(position.x, position.y);
        }
    }

    public bagel.util.Rectangle getBoundingBoxAt(Point position) {
        return wallImage.getBoundingBoxAt(position);
    }
}
