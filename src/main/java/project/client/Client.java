package project.client;

import lombok.Getter;
import lombok.Setter;
import project.gamelogic.Game;
import project.gamelogic.objects.Player;
import project.input.GameInputListener;
import project.input.InputState;
import project.window.GameWindow;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Runnable {
    //  Game state members
    @Getter
    private Game game;
    private final GameWindow gameWindow;
    private final InputState inputState;
    private final GameInputListener gameInputListener;
    @Getter @Setter
    private int playerID;
    // Game tick rate members
    private static final int TARGET_TICK_RATE = 120;
    private static final int NANOS_IN_SECOND = 1000000000;

    private static final long TARGET_TIME = NANOS_IN_SECOND / TARGET_TICK_RATE;

    // Server connection data
    private Socket playerSocket = null;
    private ObjectOutputStream playerOutputStream = null;
    private ObjectInputStream playerInputStream = null;
    private String IPAddress = "127.0.0.1";
    private int port = 3113;



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
        Player player = new Player(null, 1);
        game.addPlayer(player);

    }

    public void run() {
        //this.init();
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        double deltaTime = 0;
        int ticks = 0;

        establishConnection();
        do {
            game = loadGame();
        }while(game == null);

        playerID = loadPlayerId();


        while (!Thread.interrupted()) {

            long now = System.nanoTime();
            deltaTime += (now - lastTime) / (double)NANOS_IN_SECOND;
            delta += (now - lastTime) / (double) TARGET_TIME;
            lastTime = now;

            /*
            do {
                game = loadGame();
            }while(game == null);
            */

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
                deltaTime = Math.max(deltaTime, 0.00001);
                delta--;
                ticks++;
            }

            // Print tick rate
            if (System.currentTimeMillis() - timer > 1000) {
                System.out.println("TICK RATE: " + ticks);
                ticks = 0;
                timer += 1000;
            }

            try{
                sendEvent("emptyTest");
            }catch(IOException io){
                io.printStackTrace();
            }
        }
        closeConnection();
    }

    public Game loadGame(){
        try{
            game = (Game)playerInputStream.readObject();
            return game;
        }
        catch(IOException i){
            i.printStackTrace();
        }
        catch(ClassNotFoundException c){
            c.printStackTrace();
        }

        return null;
    }
    public int loadPlayerId(){
        try{
            return (int)playerInputStream.readObject();
        }
        catch(IOException i){
            i.printStackTrace();
        }
        catch(ClassNotFoundException c){
            c.printStackTrace();
        }

        return -1;
    }

    public void sendEvent(String eventType) throws IOException{

        switch(eventType){
            case "emptyTest":
                //empty message for testing
                //playerOutputStream.writeObject(0);
        }
    }
    public void establishConnection(){
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
    }
    public void closeConnection(){
        try {
            playerSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
