package project.gamelogic.objects.basic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.awt.geom.Point2D;

@NoArgsConstructor
abstract public class DynamicObject extends StaticObject {
    @Getter @Setter
    protected double moveAngle;
    @Getter
    protected float speed;
    @Getter @Setter
    protected boolean isMoving;

    public DynamicObject(Point2D.Float center, float radius, double moveAngle, float speed) {
        super(center, radius);
        this.moveAngle = moveAngle;
        this.speed = speed;
        this.isMoving = false;
    }

    @Override
    public void update(double deltaTime) {
        move(deltaTime);
    }

    protected void move(double deltaTime) {
        if(isMoving) {
            float deltaX = (float) (speed * Math.cos(moveAngle) * deltaTime);
            float deltaY = (float) (speed * Math.sin(moveAngle) * deltaTime);

            center = new Point2D.Float(center.x + deltaX, center.y + deltaY);
        }
    }
}
