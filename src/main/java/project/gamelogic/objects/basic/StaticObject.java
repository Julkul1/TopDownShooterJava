package project.gamelogic.objects.basic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.Serializable;

@NoArgsConstructor
abstract public class StaticObject implements Serializable {
    @Getter @Setter
    protected double radius;
    @Getter @Setter
    protected Color color;
    @Getter @Setter
    protected Point2D.Float center;

    public StaticObject(Point2D.Float center, double radius, Color color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    public abstract void collide(StaticObject object);
    public abstract void update(double deltaTime);

    public boolean doesCollide(StaticObject object) {
        return center.distance(object.center) < radius + object.radius;
    }
    static boolean doesCollide(StaticObject objectA, StaticObject objectB) {
        return objectA.center.distance(objectB.center) < objectA.radius + objectB.radius;
    }


}
