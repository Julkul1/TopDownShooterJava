package project.window;

import project.gamelogic.GameMap;

import java.awt.*;

public interface PaintingConstants {
    interface View {
        int WIDTH = 1280;
        int HEIGHT = 720;
    }

    interface Player_Paint {
        float OUTLINE_THICKNESS = 4.0f;
        Color COLOR_ENEMY = Color.RED;
        Color COLOR_FRIENDLY = Color.BLUE;

        interface Barrel {
            int WIDTH = 120;
            int HEIGHT = 20;
            int ROTATION_PIVOT_X = 10;
            int ROTATION_PIVOT_Y = HEIGHT / 2;
            float OUTLINE_THICKNESS = 2.0f;
        }

    }

    interface PowerUp_Paint {
        Color COLOR = Color.GREEN;
    }

    interface Map_Paint {
        float OUTLINE_THICKNESS = 30.0f;
        float WIDTH = GameMap.WIDTH + OUTLINE_THICKNESS;
        float HEIGHT = GameMap.HEIGHT + OUTLINE_THICKNESS;
    }
}