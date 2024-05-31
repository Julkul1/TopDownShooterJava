package project;

import project.gamelogic.Game;
import project.gamelogic.objects.Player;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        Game objectToSerialize = new Game(true);
        int id = Player.getNextID();
        Player newPlayer = new Player(null, id);
        objectToSerialize.addPlayer(newPlayer);

        try {
            // Write object to file
            FileOutputStream fileOutputStream = new FileOutputStream("object.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(objectToSerialize);
            objectOutputStream.close();
            fileOutputStream.close();
            System.out.println("Object serialized successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Deserialization
        try {
            // Read object from file
            FileInputStream fileInputStream = new FileInputStream("object.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Game deserializedObject = (Game) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            System.out.println("Deserialized Object:");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
