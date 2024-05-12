package project.window;

import project.GameInputListener;
import project.gamelogic.Game;

public class GameWindowUpdater implements Runnable {
    private static final int TARGET_FPS = 60;
    private static final long TARGET_TIME = 1000000000 / TARGET_FPS; // Nanoseconds per frame
    private static GameWindow gameWindow;

    public GameWindowUpdater(Game game, GameInputListener gameInputListener) {
        gameWindow = new GameWindow(game, gameInputListener);
    }
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        int frames = 0;

        while (!Thread.interrupted()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / (double) TARGET_TIME;
            lastTime = now;

            while (delta >= 1) {
                update();
                delta--;
                frames++;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
    }

    private void update() {
        gameWindow.repaint();
    }
}
