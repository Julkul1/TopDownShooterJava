package Project;

import Project.GameLogic.Game;
import Project.Window.GameWindow;
import Project.Window.GameWindowUpdater;

public class Main {

    public static void main(String[] args) {
        InputState inputState = new InputState();
        GameInputListener gameInputListener = new GameInputListener(inputState);
        Game game = new Game(inputState);
        GameWindowUpdater gameWindowUpdater = new GameWindowUpdater(game, gameInputListener);

        Thread gameLogic = new Thread(game);
        Thread gameView = new Thread(gameWindowUpdater);

        gameLogic.start();
        gameView.start();

        try {
            gameLogic.join();
            gameView.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
