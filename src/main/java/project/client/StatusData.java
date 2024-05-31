package project.client;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class StatusData implements Serializable {
    @Getter @Setter
    private int playerID;
    @Getter @Setter
    private ClientStatus clientStatus;

    public StatusData(int playerID) {
        this.playerID = playerID;
        this.clientStatus = ClientStatus.WAITING_FOR_ID;
    }

    public StatusData(ClientStatus clientStatus) {
        this.playerID = 0;
        this.clientStatus = clientStatus;
    }
}
