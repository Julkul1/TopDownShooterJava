package project.server;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class LobbyData implements Serializable {
    @Getter @Setter
    private int readyPlayers;
    @Getter @Setter
    private int requiredMinPlayers;
    @Getter @Setter
    private int lobbyStartTimer;
    @Getter @Setter
    private boolean gameStarted;

    public LobbyData(int readyPlayers, int requiredMinPlayers) {
        this.readyPlayers = readyPlayers;
        this.requiredMinPlayers = requiredMinPlayers;
        this.lobbyStartTimer = 1;
        this.gameStarted = false;
    }
}
