package project.server;
import lombok.Getter;
import project.Message;
import project.gamelogic.Game;
import project.gamelogic.objects.Player;
import project.input.InputState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.net.*;

public class Server {
    @Getter
    private final Game game = new Game();
    public static int PORT = 9876;
    public static String HOST_NAME = "localhost";
    public static int TIME_TILL_TIMEOUT = 10; // ms
    public static int RECEIVE_DATA_ARRAY_SIZE = 512;
    private final Map<ClientDataKey, ClientData> clientAdressMap = new HashMap<>();
    private final List<ClientDataKey> lastConnectedClients = new LinkedList<>();
    private int latestServerPackageNum = 0;
    // Game tick rate members
    private static final int TARGET_TICK_RATE = 120;
    private static final int NANOS_IN_SECOND = 1000000000;
    private static final long TARGET_TIME = NANOS_IN_SECOND / TARGET_TICK_RATE;

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.run();
    }

    public void run() {
        game.addPlayer(new Player(null, 1));

        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        double deltaTime = 0;
        int ticks = 0;

        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            serverSocket.setSoTimeout(TIME_TILL_TIMEOUT);


            while (!Thread.interrupted()) {

                long now = System.nanoTime();
                deltaTime += (now - lastTime) / (double) NANOS_IN_SECOND;
                delta += (now - lastTime) / (double) TARGET_TIME;
                lastTime = now;


                // Update game and window
                while (delta >= 1) {
                    // Update player input before game
                    game.update(deltaTime);

                    if (ticks % 3 == 0) {
                        readClientsInput(serverSocket);
                        manipulateClientsInput();
                        sendDataToClients(serverSocket);
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

    private void readClientsInput(DatagramSocket serverSocket) throws IOException, ClassNotFoundException {
        lastConnectedClients.clear();
        byte[] receiveData = new byte[RECEIVE_DATA_ARRAY_SIZE];
        while (true) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                //receivePacketLast = receivePacket;

                // Deserialize received object
                ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object receivedObject = ois.readObject();

                if (!(receivedObject instanceof Message)) {
                    throw new RuntimeException("Incorrect type of object send to server");
                }
                Message receivedMessage = (Message)receivedObject;

                // Add data to latest data to client
                ClientDataKey clientDataKey = new ClientDataKey(receivePacket.getAddress(), receivePacket.getPort());
                if (!clientAdressMap.containsKey(clientDataKey)) {
                    clientAdressMap.put(clientDataKey, new ClientData());
                }
                if (clientAdressMap.get(clientDataKey).getLastPackageNum() < receivedMessage.getMessageNum()) {
                    clientAdressMap.get(clientDataKey).setObjectReceived(receivedMessage.getData());
                    clientAdressMap.get(clientDataKey).setLastPackageNum(receivedMessage.getMessageNum());
                    lastConnectedClients.add(clientDataKey);
                }

            } catch (SocketTimeoutException e) {
                break;
            }
        }
    }

    private void sendDataToClients(DatagramSocket serverSocket) throws IOException {
        if (lastConnectedClients.size() > 0) {
            Message message = new Message(game, latestServerPackageNum);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(message);
            oos.flush();
            byte[] sendData = baos.toByteArray();

            for (ClientDataKey clientKey : lastConnectedClients) {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientKey.getAddress(), clientKey.getPort());
                serverSocket.send(sendPacket);
            }

            latestServerPackageNum += 1;
        }
    }

    private void manipulateClientsInput() {
        for (ClientDataKey clientKey : lastConnectedClients) {
            if (clientAdressMap.get(clientKey).getObjectReceived() instanceof InputState) {
                game.updatePlayerControl((InputState) clientAdressMap.get(clientKey).getObjectReceived(), 1);
            }
        }
    }
}