package project.server;
import java.awt.*;
import java.awt.geom.Point2D;
import java.net.*;
import java.io.*;

import project.gamelogic.Game;
import project.gamelogic.objects.Player;


class Signaller{
    public Boolean createSignal;

    public Signaller(){
        this.createSignal = true;
    }
    void hold(){
        this.createSignal = false;
    }
    void release(){
        this.createSignal = true;
    }
}


class ServerThread implements Runnable{
    //network fields
    private ServerSocket serverSocket;
    private Socket connectionSocket = null;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;
    private Game gameState;


    //other fields
    private Signaller serverThreadSignaller = null;
    private Integer id;

    public ServerThread(ServerSocket socket, Signaller signaller, Integer threadNo, Game gameState){
        this.serverSocket = socket;
        this.serverThreadSignaller = signaller;
        this.id = threadNo;
        this.gameState = gameState;


    }

    private void handleConnection() throws IOException{
        connectionSocket = serverSocket.accept();

        synchronized(serverThreadSignaller){
            serverThreadSignaller.release();
        }
        this.outputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(connectionSocket.getInputStream());

        /*
        Code for handling the client including any communication goes below.
         */


        int id = Player.getNextID();
        Player newPlayer = new Player(new Point2D.Float(100,100), Color.CYAN, id);
        gameState.addPlayer(newPlayer);
        outputStream.writeObject(gameState);
        outputStream.writeObject(id);


        while(true){
            //writes game state to the client
            outputStream.writeObject(gameState);
            try{
                //get events from user
                inputStream.readObject();

                /*
                Update the game state based on events received from user
                 */
            }catch(ClassNotFoundException cnfe){
                cnfe.printStackTrace();
             }
        }



    }

    public void run(){
        try{
            try{
                handleConnection();
            }finally{
                connectionSocket.close();
            }
        }catch(IOException e){
            System.out.println(e);
            return;
        }
    }

}
public class Server {
    private Integer port = null;
    private ServerSocket serverSocket = null;
    private Integer lastThreadId = 0;
    private Game gameState = new Game(null);

    void NetworkCreateServer(Integer port) throws IOException{
        this.port = port;
        //bind the server socket to port
        this.serverSocket = new ServerSocket(port);
        if(this.serverSocket != null){
            System.out.println("Server created on port " + this.port);
        }

    }

    public Server(Integer port){
        try{

            //create server
            NetworkCreateServer(port);
            Signaller serverSignaller = new Signaller();
            while(true){
                //check for connections
                synchronized(serverSignaller){
                    if(serverSignaller.createSignal == true){

                        System.out.println("Called new thread creation");

                        ServerThread serverInstance = new ServerThread(serverSocket, serverSignaller, lastThreadId, gameState);
                        lastThreadId += 1;
                        Thread serverThread = new Thread(serverInstance);

                        serverThread.start();
                        serverSignaller.hold();
                    }
                }

            }
        }catch(IOException e){
            System.out.println(e);
            return;
        }
    }
    public static void main( String[] args )
    {

        System.out.println("Beginning of the Server.java main function.");

        final int port = 3113;


        Server myServer = new Server(port);

    }
}
