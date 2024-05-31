package project.server;

import lombok.Setter;
import lombok.Getter;
import project.client.ClientStatus;

public class ClientData {
    @Getter @Setter
    Object objectReceived;
    @Getter @Setter
    Integer lastPackageNum = -1;
    @Getter @Setter
    Integer clientID = 0;
    @Getter @Setter
    ClientStatus clientStatus = ClientStatus.WAITING_FOR_ID;

}
