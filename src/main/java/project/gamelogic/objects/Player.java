package project.gamelogic.objects;

import project.gamelogic.Game;
import project.gamelogic.objects.basic.RotatingObject;
import project.gamelogic.objects.basic.StaticObject;
import lombok.Getter;
import lombok.Setter;
import project.gamelogic.objects.basic.Status;

import java.awt.*;
import java.awt.geom.Point2D;
import java.net.*;
import java.io.*;
import java.security.PrivilegedAction;

public class Player extends RotatingObject implements GameObjectsConstants.Player {
    private static int globalID = 0;
    @Getter private final int ID;
    @Getter @Setter private float hitPoints;
    @Getter @Setter private float strength;
    @Getter @Setter private boolean isShooting = false;
    private float shootingDelay = 0.0F;
    private final Game game;

    public Player(Point2D.Float center, int ID, Game game) {
        super(center, RADIUS, 0.0, SPEED, 0.0);
        this.ID = ID;
        hitPoints = HIT_POINTS;
        strength = STRENGTH;
        this.game = game;
    }

    public static int getNextID() {
        globalID++;
        return globalID;
    }

    public static void resetGlobalID() {
        globalID = 0;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        shootingDelay -= (float)deltaTime;

        if (isShooting && shootingDelay <= 0) {
            shootingDelay += SHOOT_DELTA;
            game.addBullet(this);
        }

        // if shooting delay is less than 0 set 0
        shootingDelay = Math.max(shootingDelay, 0f);
    }

    @Override
    public void collide(StaticObject object) {
        if(object instanceof Bullet) {
            Bullet bullet = (Bullet)object;
            if (this != bullet.getCreator()) {
                hitPoints -= bullet.getDamage();
                if (hitPoints <= 0) {
                    status = Status.DEAD;
                }
                bullet.setStatus(Status.DEAD);
            }
        }
        if(object instanceof PowerUp){
            PowerUp powerUP = (PowerUp)object;
            powerUP.collide(this);
        }
    }

}
