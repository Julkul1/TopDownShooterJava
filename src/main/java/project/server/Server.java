package project.server;
import java.net.*;
import java.io.*;


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

    //other fields
    private Signaller serverThreadSignaller = null;
    private Integer id;

    public ServerThread(ServerSocket socket, Signaller signaller, Integer threadNo){
        this.serverSocket = socket;
        this.serverThreadSignaller = signaller;
        this.id = threadNo;
    }

    private void handleConnection() throws IOException{
        connectionSocket = serverSocket.accept();

        synchronized(serverThreadSignaller){
            serverThreadSignaller.release();
        }

        /*
        Code for handling the client including any communication goes below.
         */

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

                        ServerThread serverInstance = new ServerThread(serverSocket, serverSignaller, lastThreadId);
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

        Server myServer = new Server(3113);

    }
}
