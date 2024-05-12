package project.gamelogic.objects;

import project.gamelogic.objects.basic.DynamicObject;
import project.gamelogic.objects.basic.StaticObject;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Point2D;

public class Bullet extends DynamicObject implements GameObjectsConstants.Bullet {
    @Getter
    private final float damage;

    public Bullet(Point2D.Float center, Color color, double moveAngle, float damage) {
        super(center, RADIUS, color, moveAngle, SPEED);
        this.damage = damage;
    }

    @Override
    public void collide(StaticObject object) {

    }
}
