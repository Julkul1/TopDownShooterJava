package project.gamelogic;

import project.gamelogic.objects.Player;
import project.gamelogic.objects.basic.StaticObject;
import project.gamelogic.objects.basic.Status;

import java.awt.geom.Point2D;

public class GameMap {
    public static int WIDTH = 2000;
    public static int HEIGHT = 2000;

    public static void collide(StaticObject object) {
        float radius = object.getRadius();
        Point2D.Float center = object.getCenter();

        if (doesCollide(object)) {
            // Move player back to boundaries of a map
            if (object instanceof Player) {
                float x, y;
                if (center.getX() + radius > WIDTH) {
                    x = WIDTH - radius;
                } else if (center.getX() - radius < 0) {
                    x = radius;
                } else {
                    x = center.x;
                }

                if (center.getY() + radius > HEIGHT) {
                    y = HEIGHT - radius;
                } else if (center.getY() - radius < 0) {
                    y = radius;
                } else {
                    y = center.y;
                }

                object.setCenter(new Point2D.Float(x, y));
            }
            else {
                object.setStatus(Status.DEAD);
            }
        }
    }

    public static boolean doesCollide(StaticObject object) {
        float radius = object.getRadius();
        Point2D.Float center = object.getCenter();

        return center.getX() + radius > WIDTH ||
                center.getX() - radius  < 0 ||
                center.getY() + radius > HEIGHT ||
                center.getY() - radius < 0;
    }
}
