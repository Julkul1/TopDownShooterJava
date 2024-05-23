package project.client;

import lombok.Getter;
import lombok.Setter;
import project.Message;
import project.gamelogic.Game;
import project.gamelogic.objects.Player;
import project.input.GameInputListener;
import project.input.InputState;
import project.server.Server;
import project.window.GameWindow;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.io.*;
import java.net.*;

public class Client {
    //  Game state members
    @Getter
    private Game game;
    private final GameWindow gameWindow;
    private InputState inputState;
    private final GameInputListener gameInputListener;
    @Getter @Setter
    private int playerID;
    // Game tick rate members
    private static final int TARGET_TICK_RATE = 120;
    private static final int NANOS_IN_SECOND = 1000000000;

    private static final long TARGET_TIME = NANOS_IN_SECOND / TARGET_TICK_RATE;

    // Server connection data
    public static int TIME_TILL_TIMEOUT = 10; // ms
    public static int RECEIVE_DATA_ARRAY_SIZE = 2058;
    private int latestClientPackageNum = 0;
    private int latestServerPackageNum = -1;




    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        client.run();
    }

    private Client() {
        inputState = new InputState();
        gameInputListener = new GameInputListener(inputState);
        game = new Game();
        gameWindow = new GameWindow(this, gameInputListener);
        playerID = 1;
    }

    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        double deltaTime = 0;
        int ticks = 0;

        try (DatagramSocket clientSocket = new DatagramSocket()) {
            clientSocket.setSoTimeout(TIME_TILL_TIMEOUT);


            while (!Thread.interrupted()) {

                long now = System.nanoTime();
                deltaTime += (now - lastTime) / (double) NANOS_IN_SECOND;
                delta += (now - lastTime) / (double) TARGET_TIME;
                lastTime = now;


                // Update game and window
                while (delta >= 1) {
                    // Update player input before game
                    if (ticks % 3 == 0) {
                        Game updatedGame = readServerInput(clientSocket);
                        if (updatedGame != null) game = updatedGame;
                        sendDataToServer(clientSocket);
                    }

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

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Game readServerInput(DatagramSocket clientSocket) throws IOException, ClassNotFoundException {
        Game updatedGame = null;
        byte[] receiveData = new byte[RECEIVE_DATA_ARRAY_SIZE];

        while (!Thread.interrupted()) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                // Deserialize received object
                ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object receivedObject = ois.readObject();

                if (!(receivedObject instanceof Message)) {
                    throw new RuntimeException("Incorrect type of object send to client");
                }
                Message receivedMessage = (Message) receivedObject;

                if (receivedMessage.getMessageNum() > latestServerPackageNum && receivedMessage.getData() instanceof Game) {
                    updatedGame = (Game)receivedMessage.getData();
                    latestServerPackageNum = receivedMessage.getMessageNum();
                }
            }catch (SocketTimeoutException e) {
                break;
            }
        }
        return updatedGame;
    }

    private void sendDataToServer(DatagramSocket clientSocket) throws IOException {
        InetAddress serverAddress = InetAddress.getByName(Server.HOST_NAME);

        Message message = new Message(inputState, latestClientPackageNum);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        oos.flush();
        byte[] sendData = baos.toByteArray();

        // Send data to server
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, Server.PORT);
        clientSocket.send(sendPacket);

        latestClientPackageNum += 1;
    }
}
