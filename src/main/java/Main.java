import javax.swing.*;

public class Main {
    private static InputState inputState;
    private static GameInputListener gameInputListener;
    private static PlayerObject mainPlayer;
    private static GameWindow gameWindow;
    private static float playerSpeed = 10;
    private static final int TARGET_FPS = 60;
    private static final long TARGET_TIME = 1000000000 / TARGET_FPS; // Nanoseconds per frame

    public static void main(String[] args) {
        inputState = new InputState();
        gameInputListener = new GameInputListener(inputState);
        mainPlayer = new PlayerObject(0.0f, 0.0f);
        gameWindow = new GameWindow(gameInputListener, mainPlayer);

        run();
    }

    public static void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        int frames = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / (double) TARGET_TIME;
            lastTime = now;

            while (delta >= 1) {
                update((float)delta);
                render();
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

    private static void update(float delta) {
        float distance = delta * playerSpeed;
        if (inputState.isUpKeyPressed()) {
            mainPlayer.setPlayerY(mainPlayer.getPlayerY() - distance);
        }
        if (inputState.isDownKeyPressed()) {
            mainPlayer.setPlayerY(mainPlayer.getPlayerY() + distance);
        }
        if (inputState.isLeftKeyPressed()) {
            mainPlayer.setPlayerX(mainPlayer.getPlayerX() - distance);
        }
        if (inputState.isRightKeyPressed()) {
            mainPlayer.setPlayerX(mainPlayer.getPlayerX() + distance);
        }
        double centerX = (double)PaintingConstants.View.WIDTH / 2;
        double centerY = (double)PaintingConstants.View.HEIGHT / 2;
        double angle = Math.atan2(inputState.getCursorY() - centerY, inputState.getCursorX() - centerX);
        mainPlayer.setAngle(angle);
    }

    private static void render() {
        gameWindow.repaint();
    }

}
