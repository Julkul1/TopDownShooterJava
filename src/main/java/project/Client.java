package project;

import project.gamelogic.Game;
import project.gamelogic.objects.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private Socket playerSocket = null;
    private ObjectOutputStream playerOutputStream = null;
    private ObjectInputStream playerInputStream = null;
    private String IPAddress = "127.0.0.1";
    private int port = 3113;
    private int playerId = 0;

    public void setPlayerId(int playerId){
        this.playerId = playerId;
    }
    public int getPlayerId(){
        return this.playerId;
    }
    public Game loadGame(){
        try{
            return (Game)playerInputStream.readObject();
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
                playerOutputStream.writeObject(0);
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
