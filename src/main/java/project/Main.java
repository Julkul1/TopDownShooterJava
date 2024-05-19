package project;

import jdk.nashorn.api.tree.ObjectLiteralTree;
import project.gamelogic.Game;
import project.window.GameWindowUpdater;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        InputState inputState = new InputState();
        GameInputListener gameInputListener = new GameInputListener(inputState);
        Game game = new Game(inputState);
        GameWindowUpdater gameWindowUpdater = new GameWindowUpdater(game, gameInputListener);

        Socket playerSocket = null;
        ObjectOutputStream playerOutputStream = null;
        ObjectInputStream playerInputStream = null;
        String IPAddress = "127.0.0.1";
        int port = 3113;

        try{
            playerSocket = new Socket(IPAddress, port);
            playerOutputStream = new ObjectOutputStream(playerSocket.getOutputStream());
            playerInputStream = new ObjectInputStream(playerSocket.getInputStream());
        }
        catch(IOException i){
            i.printStackTrace();
            System.out.println("Error: connection refused on socket " + IPAddress+":"+port);
            System.exit(-1);
        }





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

        try {
            playerSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
