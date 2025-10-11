package rooms;

import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.Window;
import bagel.util.Point;
import entities.*;

import java.util.Map;
import java.util.Properties;

public class BattleRoomA {
    private final Image background;
    // door
    private Door doorToPrep;
    private Door doorToB;
    // enemy
    private Enemies enemy;
    // wall
    private final Wall wall;
    // river
    private final River river;
    // box
    private TreasureBox box;
    // basket
    private Basket basket;

    private boolean encounterStarted = false;

    public BattleRoomA(Properties gameProps) {
        // background
        this.background = new Image("res/background.png");

        // door to prep
        this.doorToPrep = new Door(gameProps, "primarydoor.A");
        this.doorToB = new Door(gameProps, "secondarydoor.A");

        // enemies
        this.enemy = new Enemies(gameProps, "keyBulletKin.A");

        // wall
        this.wall = new Wall(gameProps, "wall.A");

        // river
        this.river = new River(gameProps, "river.A");

        // box
        this.box = new TreasureBox(gameProps, "treasurebox.A");

        // basket
        this.basket = new Basket(gameProps, "basket.A");
    }

    // get entities in room
    public Door[] getDoors() {
        return new Door[]{doorToPrep, doorToB};
    }
    public Enemies getEnemies() { return enemy; }

    // war status
    public void setEncounterStarted() {
        if (!encounterStarted) {
            encounterStarted = true;
        }
    }

    // wall status
    public boolean isWall() { return wall.isWall(); }

    // river status
    public boolean isRiver() { return river.isRiver(); }

    // get all positions of wall, exposed to players
    public Point[] getWallPositions() { return wall.getPositions(); }

    // get all positions of river
    public Point[] getRiverPositions() { return river.getPositions(); }

    public Map<Point, Integer> getTreasureBoxes() {
        return box.getBoxMap();
    }

    public Map<Point, Boolean> getTreasureStatus() {
        return box.getOpenedMap();
    }

    // reset
    public void reset(Properties gameProps) {
        doorToPrep = new Door(gameProps, "primarydoor.A");
        doorToB = new Door(gameProps, "secondarydoor.A");
        enemy = new Enemies(gameProps, "keyBulletKin.A");
        box = new TreasureBox(gameProps, "treasurebox.A");
    }

    // update
    public void update(Input input) {
        // draw background
        background.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        // door
        if (input.wasPressed(Keys.R) && !enemy.getStatus()) {
            doorToPrep.doDoor();
            doorToB.doDoor();
        }

        if (encounterStarted) {
            doorToPrep.lock();
            doorToB.lock();
        }

        // enemies and doors animation
        if (enemy.getStatus() && encounterStarted) {
            enemy.draw();
        } else {
            if (!encounterStarted) {
                doorToPrep.unlock();
            }
        }

        // if die unlock all door
        if (!enemy.getStatus()) {
            encounterStarted = false;
            doorToPrep.unlock();
            doorToB.unlock();
        }

        doorToPrep.draw();
        doorToB.draw();

        // walls
        wall.draw();

        // rivers
        river.draw();

        // Box
        box.draw();

        // basket
        basket.draw();
    }
}
