package project.window;

public interface PaintingConstants {
    interface View {
        int WIDTH = 1280;
        int HEIGHT = 720;
    }

    interface Player {
        int RADIUS = 40;
        float OUTLINE_THICKNESS = 4.0f;
        interface Barrel {
            int WIDTH = 120;
            int HEIGHT = 20;
            int ROTATION_PIVOT_X = 10;
            int ROTATION_PIVOT_Y = HEIGHT / 2;
            float OUTLINE_THICKNESS = 2.0f;
        }

    }

    interface Map {
        float OUTLINE_THICKNESS = 30.0f;
    }
}