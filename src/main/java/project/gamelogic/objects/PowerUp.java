package project.gamelogic.objects;

import lombok.Getter;
import project.gamelogic.Game;
import project.gamelogic.objects.basic.StaticObject;
import project.gamelogic.objects.basic.Status;

import java.awt.geom.Point2D;

public class PowerUp extends StaticObject implements GameObjectsConstants.POWER_UP {
    private float lifeSpan = MAX_LIFE_SPAN;
    public PowerUp(Point2D.Float center) {
        super(center, RADIUS);
    }

    public static double globalTimer = TIME_TO_SPAWN;
    @Override
    public void collide(StaticObject object) {
        if (object instanceof Player) {
            Player player = (Player)object;
            player.setStrength(player.getStrength() + STRENGTH_POWER);
        }
    }

    @Override
    public void update(double deltaTime) {
        lifeSpan -= deltaTime;
        if (lifeSpan < 0){
            status = Status.DEAD;
        }
    }

    public static void updateGlobal(double deltaTime, Game game) {
        globalTimer -= deltaTime;
        if (globalTimer <= 0) {
            globalTimer = TIME_TO_SPAWN;
            game.createPowerUp();
        }
    }
}
