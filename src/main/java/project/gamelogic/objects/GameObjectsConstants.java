package project.gamelogic.objects;

import java.awt.*;

public interface GameObjectsConstants {
    public interface Player {
        float RADIUS = 40.0f;
        float HIT_POINTS = 100.0f;
        float STRENGTH = 20.0f;
        float SPEED = 700.0f;
        float SHOOTING_SPEED = 2.5f;
        double SHOOT_DELTA =  1 / SHOOTING_SPEED; // minimum time between shots in seconds
    }

    public interface Bullet {
        float RADIUS = 15.0f;
        float SPEED = 1200.0f;
    }

    public interface POWER_UP {
        float RADIUS = 20.0f;
        float MAX_LIFE_SPAN = 10f;
        float STRENGTH_POWER = 5.0f;
        float TIME_TO_SPAWN = 5f;
    }
}
