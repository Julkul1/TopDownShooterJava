package project.gamelogic;

import project.gamelogic.objects.*;
import project.InputState;
import project.window.PaintingConstants;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Game implements Runnable {
    private static final int TARGET_TICK_RATE = 120;
    private static final long TARGET_TIME = 1000000000 / TARGET_TICK_RATE;
    private static final int pointsToWin = 100;
    private static final int pointsPerKill = 5;
    @Getter
    private List<Player> players =  new LinkedList<>();
    @Getter
    private List<Bullet> bullets = new LinkedList<>();
    @Getter
    private List<PowerUp> powerUps = new LinkedList<>();
    private Map<Player, Integer> scoreTable = new HashMap<>();
    @Getter
    Player mainPlayer;
    private final InputState inputState;

    public Game(InputState inputState) {
        mainPlayer = new Player(new Point2D.Float(100,100), Color.CYAN);
        this.inputState = inputState;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        int ticks = 0;

        while (!Thread.interrupted()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / (double) TARGET_TIME;
            lastTime = now;

            while (delta >= 1) {
                update(delta);
                delta--;
                ticks++;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                System.out.println("TICK RATE: " + ticks);
                ticks = 0;
                timer += 1000;
            }
        }
    }

    public void update(double deltaTime) {
        float x = 0, y = 0;
        boolean isMoving = false;
        if (inputState.isUpKeyPressed()) {
            y = -1.0f;
            isMoving = true;
        }
        if (inputState.isDownKeyPressed()) {
            y = 1.0f;
            isMoving = true;
        }
        if (inputState.isLeftKeyPressed()) {
            x = -1.0f;
            isMoving = true;
        }
        if (inputState.isRightKeyPressed()) {
            x = 1.0f;
            isMoving = true;
        }

        float centerX = (float)PaintingConstants.View.WIDTH / 2;
        float centerY = (float)PaintingConstants.View.HEIGHT / 2;
        double facingAngle = Math.atan2(inputState.getCursorY() - centerY, inputState.getCursorX() - centerX);
        double moveAngle = Math.atan2(y - 0.0, x - 0.0);

        mainPlayer.setMoveAngle(moveAngle);
        mainPlayer.setFacingAngle(facingAngle);
        mainPlayer.setMoving(isMoving);

        mainPlayer.update(deltaTime);
        GameMap.collide(mainPlayer);
    }

    public void addPlayer(Player newPlayer) {
        players.add(newPlayer);
        scoreTable.put(newPlayer, 0);
    }
}
