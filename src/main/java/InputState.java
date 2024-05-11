import lombok.Setter;
import lombok.Getter;
public class InputState {
    @Getter @Setter private boolean downKeyPressed;
    @Getter @Setter private boolean upKeyPressed;
    @Getter @Setter private boolean rightKeyPressed;
    @Getter @Setter private boolean leftKeyPressed;
    @Getter @Setter private int cursorX;
    @Getter @Setter private int cursorY;
}
