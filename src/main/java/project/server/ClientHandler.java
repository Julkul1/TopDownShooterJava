package project.server;

import project.gamelogic.Game;
import project.gamelogic.objects.Player;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Server server;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream  out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream  in = new ObjectInputStream(clientSocket.getInputStream())
        ) {

            int id = Player.getNextID();
            Game gameState = server.getGameState();
            Player newPlayer = new Player(null, id);
            gameState.addPlayer(newPlayer);

            out.writeObject(gameState);
            out.writeObject(id);

            in.read();



        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        System.out.println("Thread ended");
    }
}
