package rooms;

import bagel.*;
import bagel.util.Point;
import entities.Door;

import java.util.Properties;


public class PrepRoom {
    private final Image background;
    private final Image restartArea;
    private final Image MarineImage;
    private final Image RobotImage;

    private final Point restartPosition;
    private final Point MarinePoint;
    private final Point RobotPoint;

    private final Font titleFont;
    private final Font promptFont;

    private final String titleMessage;
    private final String moveMessage;
    private final String selectMessage;
    private final String MarineMessage;
    private final String RobotMessage;

    private final double titleY;
    private final double moveMessageY;
    private final double selectMessageY;
    private final Point MarineMessagePoint;
    private final Point RobotMessagePoint;


    private Door doorToA;

    public PrepRoom(Properties gameProps, Properties messageProps) {
        // background
        this.background = new Image("res/background.png");

        // restart area
        this.restartArea = new Image("res/restart_area.png");
        String raw = gameProps.getProperty("restartarea.prep");
        String[] parts = raw.split(",");
        this.restartPosition = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));

        // character image
        this.MarineImage = new Image("res/marine_sprite.png");
        String MarineRaw = gameProps.getProperty("Marine");
        String[] MarineParts = MarineRaw.split(",");
        this.MarinePoint = new Point(Double.parseDouble(MarineParts[0]), Double.parseDouble(MarineParts[1]));

        this.RobotImage = new Image("res/robot_sprite.png");
        String RobotRaw = gameProps.getProperty("Robot");
        String[] RobotParts = RobotRaw.split(",");
        this.RobotPoint = new Point(Double.parseDouble(RobotParts[0]), Double.parseDouble(RobotParts[1]));

        // font
        String fontFile = gameProps.getProperty("font");
        int titleSize = Integer.parseInt(gameProps.getProperty("title.fontSize"));
        int promptSize = Integer.parseInt(gameProps.getProperty("prompt.fontSize"));

        this.titleFont = new Font(fontFile, titleSize);
        this.promptFont = new Font(fontFile, promptSize);

        // text content
        this.titleMessage = messageProps.getProperty("title");
        this.selectMessage = messageProps.getProperty("selectMessage");
        this.moveMessage = messageProps.getProperty("moveMessage");
        this.RobotMessage = messageProps.getProperty("robotDescription");
        this.MarineMessage = messageProps.getProperty("marineDescription");

        // text pos
        this.titleY = Double.parseDouble(gameProps.getProperty("title.y"));
        this.moveMessageY = Double.parseDouble(gameProps.getProperty("moveMessage.y"));
        this.selectMessageY = Double.parseDouble(gameProps.getProperty("selectMessage.y"));

        String raw1 = gameProps.getProperty("robotMessage");
        String[] parts1 = raw1.split(",");
        this.RobotMessagePoint = new Point(Double.parseDouble(parts1[0]), Double.parseDouble(parts1[1]));

        String raw2 = gameProps.getProperty("marineMessage");
        String[] parts2 = raw2.split(",");
        this.MarineMessagePoint = new Point(Double.parseDouble(parts2[0]), Double.parseDouble(parts2[1]));

        // door to A
        this.doorToA = new Door(gameProps, "door.prep");
    }

    // get all doors in room
    public Door[] getDoors() {
        return new Door[]{doorToA};
    }

    // get restart positon
    public Point[] getRestartPosition() { return new Point[]{ restartPosition }; }

    public void reset(Properties gameProps) {
        doorToA = new Door(gameProps, "door.prep");
    }

    public void update(Input input) {
        // draw background
        background.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        // draw restart area
        restartArea.draw(restartPosition.x, restartPosition.y);

        // draw character
        MarineImage.draw(MarinePoint.x, MarinePoint.y);
        RobotImage.draw(RobotPoint.x, RobotPoint.y);

        // title
        double titleX = (Window.getWidth() - titleFont.getWidth(titleMessage)) / 2.0;
        titleFont.drawString(titleMessage, titleX, titleY);

        // prompt
        double promptX = (Window.getWidth() - promptFont.getWidth(moveMessage)) / 2.0;
        double moveMessageX = promptX - 70;
        promptFont.drawString(moveMessage, promptX, moveMessageY);
        promptFont.drawString(selectMessage, moveMessageX, selectMessageY);
        promptFont.drawString(MarineMessage, MarineMessagePoint.x, MarineMessagePoint.y);
        promptFont.drawString(RobotMessage, RobotMessagePoint.x, RobotMessagePoint.y);

        if (input.wasPressed(Keys.R) && doorToA.getIsLocked()) {
            doorToA.doDoor();
        }
        doorToA.draw();
    }
}