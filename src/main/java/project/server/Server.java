package project.server;
import lombok.Getter;
import project.gamelogic.Game;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    @Getter
    private final Game gameState = new Game();

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        Thread thread = new Thread(server);
        thread.start();
        thread.join();
    }

    public void run() {
        int port = 3113;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started");

            ExecutorService executor = Executors.newCachedThreadPool();
            while (!Thread.interrupted()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                executor.submit(new ClientHandler(clientSocket, this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}