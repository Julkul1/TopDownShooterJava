package project.gamelogic.objects;

import project.gamelogic.objects.basic.DynamicObject;
import project.gamelogic.objects.basic.StaticObject;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Point2D;

public class Bullet extends DynamicObject implements GameObjectsConstants.Bullet {
    @Getter
    private final float damage;
    @Getter
    private final Player creator;

    public Bullet(Player creator, Point2D.Float center, double moveAngle, float damage) {
        super(center, RADIUS, moveAngle, SPEED);
        this.damage = damage;
        this.creator = creator;
        this.isMoving = true;
    }

    @Override
    public void collide(StaticObject object) {
        if (object instanceof Player && creator != object) {
            Player player = (Player)object;
            player.collide(this);
        }
    }
}
