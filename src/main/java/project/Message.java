package project;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class Message implements Serializable {
    @Getter
    Object data;

    @Setter @Getter
    Integer messageNum;

    public Message(Object obj, Integer messageNum) {
        data = obj;
        this.messageNum = messageNum;
    }
}
