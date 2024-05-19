package project.gamelogic.objects.basic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

@NoArgsConstructor
abstract public class StaticObject implements Serializable {
    @Getter @Setter
    protected float radius;
    @Getter @Setter
    protected Point2D.Float center;
    @Getter @Setter
    protected Status status;

    public StaticObject(Point2D.Float center, float radius) {
        this.center = center;
        this.radius = radius;
        status = Status.ALIVE;
    }

    public abstract void collide(StaticObject object);
    public abstract void update(double deltaTime);

    public boolean doesCollide(StaticObject object) {
        return center.distance(object.center) < radius + object.radius;
    }
    public boolean doesCollide(StaticObject object, float bonusDistance) {
        return center.distance(object.center) < radius + object.radius + bonusDistance;
    }
    static boolean doesCollide(StaticObject objectA, StaticObject objectB) {
        return objectA.center.distance(objectB.center) < objectA.radius + objectB.radius;
    }

    public boolean doesCollide(Point2D.Float center, float radius, float bonusDistance) {
        return center.distance(center) < this.radius + radius + bonusDistance;
    }

}
