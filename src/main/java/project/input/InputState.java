package project.input;

import lombok.Setter;
import lombok.Getter;

import java.io.Serializable;

public class InputState implements Serializable {
    @Getter @Setter private boolean downKeyPressed;
    @Getter @Setter private boolean upKeyPressed;
    @Getter @Setter private boolean rightKeyPressed;
    @Getter @Setter private boolean leftKeyPressed;
    @Getter @Setter private int cursorX;
    @Getter @Setter private int cursorY;
    @Getter @Setter private boolean leftMouseClick;
}
