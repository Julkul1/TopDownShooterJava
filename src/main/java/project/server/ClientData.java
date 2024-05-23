package project.server;

import lombok.Setter;
import lombok.Getter;

public class ClientData {
    @Getter @Setter
    Object objectReceived;
    @Getter @Setter
    Integer lastPackageNum = -1;

}
