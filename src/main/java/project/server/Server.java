package project.server;
import lombok.Getter;
import project.Message;
import project.client.ClientStatus;
import project.client.StatusData;
import project.gamelogic.Game;
import project.gamelogic.objects.Player;
import project.input.InputState;

import java.io.IOException;
import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    ////////////////////////////////////////
    /////////////  MEMBERS  ////////////////
    ////////////////////////////////////////

    @Getter
    private final Game game = new Game(true);
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
    // Lobby members
    private static final int PLAYERS_REQUIRED_TO_START = 2; // sec
    private static final int LOBBY_START_COUNTDOWN = 10; // sec
    private static final int PLAYER_TIMEOUT_TIME = 5; // sec


    ////////////////////////////////////////
    /////////////  METHODS  ////////////////
    ////////////////////////////////////////

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    public void run() {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("Server started");
            serverSocket.setSoTimeout(TIME_TILL_TIMEOUT);
            startLobby(serverSocket);
            System.out.println("Game Started");
            startGame(serverSocket);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startLobby(DatagramSocket serverSocket) throws IOException, ClassNotFoundException {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        double deltaTime = 0;
        int ticks = 0;
        double lobbyStartTimer = LOBBY_START_COUNTDOWN;
        int playersReady = 0;

        while (!Thread.interrupted() && !game.isGameStarted()) {

            long now = System.nanoTime();
            deltaTime += (now - lastTime) / (double) NANOS_IN_SECOND;
            delta += (now - lastTime) / (double) TARGET_TIME;
            lastTime = now;

            while (delta >= 1) {
                if (ticks % 3 == 0) {
                    readClientsInput(serverSocket);

                    if (checkIfSomePlayersDisconnected()) {
                        removeDisconnectedPlayersFromGame();
                        if (clientAdressMap.size() < PLAYERS_REQUIRED_TO_START) {
                            lobbyStartTimer = LOBBY_START_COUNTDOWN;
                        }
                    }

                    updatePlayersStatus();
                    playersReady = getNumberOfReadyPlayers();
                    if (lobbyStartTimer <= 0) {
                        game.setGameStarted(true);
                    }

                    // Send new ids or lobby data to clients
                    // These methods guarantee only 1 type of data will be sent
                    sendIDDataToClients(serverSocket);
                    sendLobbyDataToClients(serverSocket, (int)lobbyStartTimer);
                }

                if (playersReady == clientAdressMap.size() && playersReady >= PLAYERS_REQUIRED_TO_START) {
                    lobbyStartTimer -= deltaTime;
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

    private void startGame(DatagramSocket serverSocket) throws IOException, ClassNotFoundException {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        double deltaTime = 0;
        int ticks = 0;

        while (!Thread.interrupted()) {

            long now = System.nanoTime();
            deltaTime += (now - lastTime) / (double) NANOS_IN_SECOND;
            delta += (now - lastTime) / (double) TARGET_TIME;
            lastTime = now;

            while (delta >= 1) {
                // Update player input before game
                game.update(deltaTime);

                if (ticks % 3 == 0) {
                    readClientsInput(serverSocket);
                    if (checkIfSomePlayersDisconnected()) {
                        removeDisconnectedPlayersFromGame();
                    }
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
                //System.out.println("TICK RATE: " + ticks);
                ticks = 0;
                timer += 1000;
            }
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
                    // Create new client
                    clientAdressMap.put(clientDataKey, new ClientData());
                    int latestPlayerID = Player.getNextID();
                    clientAdressMap.get(clientDataKey).setClientID(latestPlayerID);
                    game.addPlayer(new Player(null, latestPlayerID));
                }
                if (clientAdressMap.get(clientDataKey).getLastPackageNum() < receivedMessage.getMessageNum()) {
                    clientAdressMap.get(clientDataKey).setObjectReceived(receivedMessage.getData());
                    clientAdressMap.get(clientDataKey).setLastPackageNum(receivedMessage.getMessageNum());
                    clientAdressMap.get(clientDataKey).setLastPackageTime(System.currentTimeMillis());
                    if (!lastConnectedClients.contains(clientDataKey)) {
                        lastConnectedClients.add(clientDataKey);
                    }
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
                if (clientAdressMap.get(clientKey).clientStatus == ClientStatus.PLAYING) {
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientKey.getAddress(), clientKey.getPort());
                    serverSocket.send(sendPacket);
                }
                else if (clientAdressMap.get(clientKey).clientStatus == ClientStatus.WAITING_IN_LOBBY) {
                    sendLobbyDataToClients(serverSocket, 0);
                    updatePlayersStatus();
                }
                else if (clientAdressMap.get(clientKey).clientStatus == ClientStatus.WAITING_FOR_ID) {
                    sendIDDataToClients(serverSocket);
                    updatePlayersStatus();
                }
            }

            latestServerPackageNum += 1;
        }
    }

    private void sendIDDataToClients(DatagramSocket serverSocket) throws IOException {
        if (lastConnectedClients.size() > 0) {
            for (ClientDataKey clientKey : lastConnectedClients) {
                if (clientAdressMap.get(clientKey).getClientStatus() != ClientStatus.WAITING_FOR_ID) {
                    continue;
                }

                StatusData statusData = new StatusData(clientAdressMap.get(clientKey).getClientID());
                Message message = new Message(statusData, latestServerPackageNum);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(message);
                oos.flush();
                byte[] sendData = baos.toByteArray();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientKey.getAddress(), clientKey.getPort());
                serverSocket.send(sendPacket);
            }

            latestServerPackageNum += 1;
        }
    }

    private void sendLobbyDataToClients(DatagramSocket serverSocket, int lobbyTimer) throws IOException {
        if (lastConnectedClients.size() > 0) {
            LobbyData lobbyData = new LobbyData(getNumberOfReadyPlayers(), PLAYERS_REQUIRED_TO_START);
            lobbyData.setGameStarted(game.isGameStarted());
            lobbyData.setLobbyStartTimer(lobbyTimer);
            Message message = new Message(lobbyData, latestServerPackageNum);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(message);
            oos.flush();
            byte[] sendData = baos.toByteArray();

            for (ClientDataKey clientKey : lastConnectedClients) {
                if (clientAdressMap.get(clientKey).clientStatus == ClientStatus.WAITING_IN_LOBBY) {
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientKey.getAddress(), clientKey.getPort());
                    serverSocket.send(sendPacket);
                }
            }

            latestServerPackageNum += 1;
        }
    }

    private void updatePlayersStatus() {
        for (ClientDataKey clientKey : lastConnectedClients) {
            if (clientAdressMap.get(clientKey).getObjectReceived() instanceof StatusData) {
                StatusData statusData = (StatusData)clientAdressMap.get(clientKey).getObjectReceived();
                if (statusData.getClientStatus() == ClientStatus.WAITING_IN_LOBBY) {
                    clientAdressMap.get(clientKey).setClientStatus(ClientStatus.WAITING_IN_LOBBY);
                }
            }
            else {
                clientAdressMap.get(clientKey).setClientStatus(ClientStatus.PLAYING);
            }
        }
    }

    private int getNumberOfReadyPlayers() {
        int playersReady = 0;

        for (ClientData status : clientAdressMap.values()) {
            if (status.clientStatus != ClientStatus.WAITING_FOR_ID) {
                playersReady++;
            }
        }

        return playersReady;
    }

    private void manipulateClientsInput() {
        for (ClientDataKey clientKey : lastConnectedClients) {
            if (clientAdressMap.get(clientKey).getObjectReceived() instanceof InputState) {
                game.updatePlayerControl((InputState) clientAdressMap.get(clientKey).getObjectReceived(), clientAdressMap.get(clientKey).getClientID());
            }
        }
    }

    private boolean checkIfSomePlayersDisconnected() {
        boolean playersDisconnected = false;
        long currentTime = System.currentTimeMillis();

        for (ClientDataKey client : clientAdressMap.keySet()) {
            long inactivityTime = currentTime - clientAdressMap.get(client).getLastPackageTime();
            inactivityTime /= 1000;
            if (inactivityTime >= PLAYER_TIMEOUT_TIME) {
                clientAdressMap.get(client).setClientStatus(ClientStatus.QUITED);
                playersDisconnected = true;
            }
        }

        return  playersDisconnected;
    }

    private void removeDisconnectedPlayersFromGame() {
        List<ClientDataKey> clientsToRemove = new ArrayList<>();

        for (ClientDataKey client : clientAdressMap.keySet()) {
            if (clientAdressMap.get(client).getClientStatus() == ClientStatus.QUITED) {
                clientsToRemove.add(client);
                Player player = game.getPlayerByID(clientAdressMap.get(client).getClientID());
                game.getPlayers().remove(player);
                game.getScoreTable().remove(player);
            }
        }

        for (ClientDataKey client : clientsToRemove) {
            clientAdressMap.remove(client);
        }
    }
}