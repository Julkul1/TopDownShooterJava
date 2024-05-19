package project.client;

import lombok.Getter;
import project.gamelogic.Game;
import project.gamelogic.objects.Player;
import project.input.GameInputListener;
import project.input.InputState;
import project.window.GameWindow;

import java.awt.*;
import java.awt.geom.Point2D;

public class Client implements Runnable {
    //  Game state members
    @Getter
    private Game game;
    private final GameWindow gameWindow;
    private final InputState inputState;
    private final GameInputListener gameInputListener;
    @Getter
    private int playerID;
    // Game tick rate members
    private static final int TARGET_TICK_RATE = 120;
    private static final int NANOS_IN_SECOND = 1000000000;

    private static final long TARGET_TIME = NANOS_IN_SECOND / TARGET_TICK_RATE;



    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        Thread thread = new Thread(client);
        thread.start();
        thread.join();
    }

    private Client() {
        inputState = new InputState();
        gameInputListener = new GameInputListener(inputState);
        game = new Game();
        gameWindow = new GameWindow(this, gameInputListener);
    }

    private void init() {
        playerID = 1;
        Player player = new Player(null, 1, game);
        game.addPlayer(player);

    }

    public void run() {
        this.init();
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        double deltaTime = 0;
        int ticks = 0;


        while (!Thread.interrupted()) {

            long now = System.nanoTime();
            deltaTime += (now - lastTime) / (double)NANOS_IN_SECOND;
            delta += (now - lastTime) / (double) TARGET_TIME;
            lastTime = now;

            // Update game and window
            while (delta >= 1) {
                // Update player input before game
                game.updatePlayerControl(inputState, playerID);
                game.update(deltaTime);
                // Paint window every 2 ticks (60 fps)
                if (ticks % 2 == 0) {
                    gameWindow.repaint();
                }
                deltaTime -= delta * TARGET_TIME / NANOS_IN_SECOND;
                delta--;
                ticks++;
            }

            // Print tick rate
            if (System.currentTimeMillis() - timer > 1000) {
                System.out.println("TICK RATE: " + ticks);
                ticks = 0;
                timer += 1000;
            }
        }
    }
}
