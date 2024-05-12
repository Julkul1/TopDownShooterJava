package project.gamelogic.objects.basic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.awt.geom.Point2D;

@NoArgsConstructor
abstract public class RotatingObject extends DynamicObject {
    @Getter @Setter
    protected double facingAngle;
    public RotatingObject(Point2D.Float center, double radius, Color color, double moveAngle, float speed, double facingAngle) {
        super(center, radius, color, moveAngle, speed);
        this.facingAngle = facingAngle;
    }


}
