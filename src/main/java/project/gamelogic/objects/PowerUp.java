package project.gamelogic.objects;

import lombok.Getter;
import project.gamelogic.objects.basic.StaticObject;
import project.gamelogic.objects.basic.Status;

import java.awt.geom.Point2D;

public class PowerUp extends StaticObject implements GameObjectsConstants.POWER_UP {
    private Double lifeSpan = 1200.0;

    @Getter
    private boolean remove = false;

    @Getter
    private float addStrength = 5.0f;
    public PowerUp(Point2D.Float center) {
        super(center, RADIUS, COLOR);
    }
    @Override
    public void collide(StaticObject object) {
    }

    @Override
    public void update(double deltaTime) {
        lifeSpan -= deltaTime;
        if (lifeSpan < 0){
            remove = true;
        }
    }
}
