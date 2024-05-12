package project.gamelogic.objects;

import project.gamelogic.objects.basic.StaticObject;

import java.awt.geom.Point2D;

public class PowerUp extends StaticObject implements GameObjectsConstants.POWER_UP {

    public PowerUp(Point2D.Float center) {
        super(center, RADIUS, COLOR);
    }
    @Override
    public void collide(StaticObject object) {

    }

    @Override
    public void update(double deltaTime) {

    }
}
