package Project.GameLogic.Objects;

import Project.GameLogic.Objects.Default.GameObjectsConstants;
import Project.GameLogic.Objects.Default.RotatingObject;
import Project.GameLogic.Objects.Default.StaticObject;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.geom.Point2D;

public class Player extends RotatingObject implements GameObjectsConstants.Player {
    @Getter
    private int ID;
    @Getter @Setter
    private float hitPoints;
    @Getter @Setter
    private float strength;
    public Player(Point2D.Float center, Color color) {
        super(center, RADIUS, color, 0.0, SPEED, 0.0);
        ID = 1;
        hitPoints = HIT_POINTS;
        strength = STRENGTH;
    }

    @Override
    public void collide(StaticObject object) {

    }

}