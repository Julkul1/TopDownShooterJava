package project.gamelogic.objects;

import project.gamelogic.objects.basic.RotatingObject;
import project.gamelogic.objects.basic.StaticObject;
import lombok.Getter;
import lombok.Setter;
import project.gamelogic.objects.basic.Status;

import java.awt.*;
import java.awt.geom.Point2D;
import java.net.*;
import java.io.*;

public class Player extends RotatingObject implements GameObjectsConstants.Player {
    @Getter
    private final int ID;
    @Getter @Setter
    private float hitPoints;
    private static int globalID = 0;
    private int shootingDelay = 1;
    private boolean didShoot = false;



    public int getShootingDelay(){
        return shootingDelay;
    }

    public void setShootingDelay(int shootingDelay){
        this.shootingDelay = shootingDelay;
    }

    public boolean isDidShoot(){
        return didShoot;
    }

    public void setDidShoot(boolean didShoot){
        this.didShoot = didShoot;
    }

    @Getter @Setter
    private float strength;
    public Player(Point2D.Float center, Color color, int ID) {
        super(center, RADIUS, color, 0.0, SPEED, 0.0);
        this.ID = ID;
        hitPoints = HIT_POINTS;
        strength = STRENGTH;


    }

    public static int getNextID() {
        globalID++;
        return globalID;
    }

    public static void resetGlobalID() {
        globalID = 0;
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
            PowerUp powerUp = (PowerUp)object;
            this.setStrength(this.getStrength()+powerUp.getAddStrength());
        }
    }

}
