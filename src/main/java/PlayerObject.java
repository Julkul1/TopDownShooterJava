import lombok.Getter;
import lombok.Setter;

public class PlayerObject {
    @Getter @Setter private float playerX;
    @Getter @Setter private float playerY;
    @Getter @Setter private double angle = 0;

    public PlayerObject(float playerX, float playerY) {
        this.playerX = playerX;
        this.playerY = playerY;
    }
}
