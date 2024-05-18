package project;

import project.gamelogic.Game;
import project.window.GameWindowUpdater;

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
            gameLogic.interrupt();
            gameView.interrupt();
            e.printStackTrace();
        }

    }

}
