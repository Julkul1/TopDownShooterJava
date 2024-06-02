package project.client;

import lombok.Getter;
import lombok.Setter;
import project.Message;
import project.gamelogic.Game;
import project.input.GameInputListener;
import project.input.InputState;
import project.server.LobbyData;
import project.server.Server;
import project.window.GameWindow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.io.*;
import java.net.*;

public class Client {
    ////////////////////////////////////////
    /////////////  MEMBERS  ////////////////
    ////////////////////////////////////////

    //  Game state members
    @Getter
    private Game game;
    private final GameWindow gameWindow;
    private InputState inputState;
    private final GameInputListener gameInputListener;
    @Getter
    private LobbyData lobbyData;
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
    private int latestServerPackageNum = 0;
    private static final int GAME_OVER_COUNTDOWN = 10; //sec


    ////////////////////////////////////////
    /////////////  METHODS  ////////////////
    ////////////////////////////////////////

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    private Client() {
        inputState = new InputState();
        gameInputListener = new GameInputListener(inputState);
        game = new Game(false);
        gameWindow = new GameWindow(this, gameInputListener);
        lobbyData = new LobbyData(0,1);
        playerID = 0;
    }

    public void run() {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            clientSocket.setSoTimeout(TIME_TILL_TIMEOUT);
            System.out.println("Client started");
            joinLobby(clientSocket);
            System.out.println("Game started");
            runGame(clientSocket);
            gameWindow.closeWindow();
            System.out.println("Client closed");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void joinLobby(DatagramSocket clientSocket) throws IOException, ClassNotFoundException {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        double deltaTime = 0;
        int ticks = 0;
        StatusData statusData = new StatusData(ClientStatus.WAITING_FOR_ID);

        while (!Thread.interrupted() && !lobbyData.isGameStarted()) {
            long now = System.nanoTime();
            deltaTime += (now - lastTime) / (double) NANOS_IN_SECOND;
            delta += (now - lastTime) / (double) TARGET_TIME;
            lastTime = now;

            // Update lobby
            while (delta >= 1) {
                if (playerID <= 0 && ticks % 3 == 0) {
                    readStatusServerInput(clientSocket);
                    if (playerID > 0) {
                        statusData.setClientStatus(ClientStatus.WAITING_IN_LOBBY);
                    }
                    sendStatusToServer(clientSocket, statusData);
                }
                else if (ticks % 3 == 0) {
                    LobbyData updatedLobby = readLobbyServerInput(clientSocket);
                    if (updatedLobby != null) {
                        lobbyData = updatedLobby;
                    }
                    sendStatusToServer(clientSocket, statusData);
                }

                //Paint lobby window every 2 ticks (60 fps)
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
                //System.out.println("TICK RATE: " + ticks);
                ticks = 0;
                timer += 1000;
            }
        }
    }

    private void runGame(DatagramSocket clientSocket) throws IOException, ClassNotFoundException {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        double deltaTime = 0;
        int ticks = 0;
        double gameFinishTimer = GAME_OVER_COUNTDOWN;

        while (!Thread.interrupted() && gameFinishTimer > 0) {
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

                if (game.isGameOver()) {
                    gameFinishTimer -= deltaTime;
                }

                deltaTime -= delta * TARGET_TIME / NANOS_IN_SECOND;
                deltaTime = Math.max(deltaTime, 0.00001);
                delta--;
                ticks++;
            }

            // Print tick rate
            if (System.currentTimeMillis() - timer > 1000) {
                //System.out.println("TICK RATE: " + ticks);
                ticks = 0;
                timer += 1000;
            }
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

    private void sendStatusToServer(DatagramSocket clientSocket, StatusData statusData) throws IOException {
        InetAddress serverAddress = InetAddress.getByName(Server.HOST_NAME);

        Message message = new Message(statusData, latestClientPackageNum);

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

    private void readStatusServerInput(DatagramSocket clientSocket) throws IOException, ClassNotFoundException {
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

                if (receivedMessage.getMessageNum() > latestServerPackageNum && receivedMessage.getData() instanceof StatusData) {
                    StatusData updatedStatus = (StatusData) receivedMessage.getData();
                    latestServerPackageNum = receivedMessage.getMessageNum();
                    playerID = updatedStatus.getPlayerID();
                }
            }catch (SocketTimeoutException e) {
                break;
            }
        }
    }

    private LobbyData readLobbyServerInput(DatagramSocket clientSocket) throws IOException, ClassNotFoundException {
        byte[] receiveData = new byte[RECEIVE_DATA_ARRAY_SIZE];
        LobbyData updatedLobby = null;

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

                if (receivedMessage.getMessageNum() > latestServerPackageNum && receivedMessage.getData() instanceof LobbyData) {
                    updatedLobby = (LobbyData) receivedMessage.getData();
                    latestServerPackageNum = receivedMessage.getMessageNum();
                }
            }catch (SocketTimeoutException e) {
                break;
            }
        }

        return  updatedLobby;
    }
}
