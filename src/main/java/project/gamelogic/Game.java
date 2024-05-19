package project.gamelogic;

import project.gamelogic.objects.*;
import project.InputState;
import project.gamelogic.objects.basic.StaticObject;
import project.gamelogic.objects.basic.Status;
import project.window.PaintingConstants;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class Game implements Runnable {
    private static final int TARGET_TICK_RATE = 120;
    private static final long TARGET_TIME = 1000000000 / TARGET_TICK_RATE;
    private static final int pointsToWin = 100;
    private static final int pointsPerKill = 5;

    private int timeToCreatePowerUp = 5;

    @Getter
    private List<Player> players =  new LinkedList<>();
    @Getter
    private List<Bullet> bullets = new LinkedList<>();
    @Getter
    private List<PowerUp> powerUps = new LinkedList<>();
    private Map<Player, Integer> scoreTable = new HashMap<>();
    @Getter
    Player mainPlayer;
    private final InputState inputState;
    public Game(InputState inputState) {
        this.inputState = inputState;
    }

    void loadGame(Game gameToLoad){
        this.players = gameToLoad.players;
        this.bullets = gameToLoad.bullets;
        this.powerUps = gameToLoad.powerUps;
        this.scoreTable = gameToLoad.scoreTable;
    }



    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        int ticks = 0;

        mainPlayer = new Player(new Point2D.Float(100,100), Color.CYAN, Player.getNextID());
        players.add(mainPlayer);

        while (!Thread.interrupted()) {

            long now = System.nanoTime();
            delta += (now - lastTime) / (double) TARGET_TIME;
            lastTime = now;

            while (delta >= 1) {
                update(delta);
                delta--;
                ticks++;
            }

            if (System.currentTimeMillis() - timer > 1000) {

                System.out.println("TICK RATE: " + ticks);
                ticks = 0;
                timer += 1000;
                System.out.println(getMainPlayer().getStrength());
                timeToCreatePowerUp--;

                if(timeToCreatePowerUp == 0){
                    powerUps.add(createPowerUp());
                    timeToCreatePowerUp = 5;
                }
                for (int i = 0; i < players.size(); i++) {
                    Player currentPlayer = players.get(i);
                    if(currentPlayer.isDidShoot() == true){
                        int shootingDelay = currentPlayer.getShootingDelay();
                        shootingDelay--;
                        currentPlayer.setShootingDelay(shootingDelay);
                        if(currentPlayer.getShootingDelay() == 0){
                            currentPlayer.setDidShoot(false);
                            currentPlayer.setShootingDelay(1);
                        }
                    }
                }




            }
        }
    }

    public PowerUp createPowerUp(){
        Random rand = new Random();
        int y = rand.nextInt(GameMap.HEIGHT);
        int x = rand.nextInt(GameMap.WIDTH);
        return new PowerUp(new Point2D.Float(x,y));
    }

    public void update(double deltaTime) {
        updatePlayerControl(mainPlayer.getID(), deltaTime);

        mainPlayer.update(deltaTime);
        collide(mainPlayer);

        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
            collide(bullet);
        }

        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            powerUp.update(deltaTime);
            if (powerUp.isRemove()) {
                iterator.remove();
            }
        }

        bullets.removeIf(bullet -> bullet.getStatus() == Status.DEAD);
    }

    private void collide(StaticObject object) {
        if (GameMap.doesCollide(object)) {
            GameMap.collide(object);
        }

        if (object.getStatus() == Status.DEAD){
            return;
        }

        for (Player player : players) {
            if (object != player && object.getStatus() == Status.ALIVE && object.doesCollide(player)) {
                object.collide(player);
            }
            if (object.getStatus() == Status.DEAD){
                return;
            }
        }

        for (Bullet bullet : bullets) {
            if (object != bullet && object.getStatus() == Status.ALIVE && object.doesCollide(bullet)) {
                object.collide(bullet);
            }
            if (object.getStatus() == Status.DEAD){
                return;
            }
        }

        for (PowerUp powerUp : powerUps) {
            if (object instanceof Player && object.getStatus() == Status.ALIVE && object.doesCollide(powerUp)) {
                object.collide(powerUp);
                powerUps.remove(powerUp);
            }
            if (object.getStatus() == Status.DEAD){
                return;
            }
        }

    }

    public void updatePlayerControl(int playerID, double deltaTime) {
        float x = 0, y = 0;
        boolean isMoving = false;
        if (inputState.isUpKeyPressed()) {
            y = -1.0f;
            isMoving = true;
        }
        if (inputState.isDownKeyPressed()) {
            y = 1.0f;
            isMoving = true;
        }
        if (inputState.isLeftKeyPressed()) {
            x = -1.0f;
            isMoving = true;
        }
        if (inputState.isRightKeyPressed()) {
            x = 1.0f;
            isMoving = true;
        }

        if (inputState.isLeftMouseClick()) {
            //inputState.setLeftMouseClick(false);
            if(!mainPlayer.isDidShoot()){
                addBullet(mainPlayer);
                mainPlayer.setDidShoot(true);
            }


        }

        float centerX = (float)PaintingConstants.View.WIDTH / 2;
        float centerY = (float)PaintingConstants.View.HEIGHT / 2;
        double facingAngle = Math.atan2(inputState.getCursorY() - centerY, inputState.getCursorX() - centerX);
        double moveAngle = Math.atan2(y - 0.0, x - 0.0);

        mainPlayer.setMoveAngle(moveAngle);
        mainPlayer.setFacingAngle(facingAngle);
        mainPlayer.setMoving(isMoving);
    }

    public void addPlayer(Player newPlayer) {
        players.add(newPlayer);
        scoreTable.put(newPlayer, 0);
    }

    public void addBullet(Player creator) {
        float x = creator.getCenter().x + PaintingConstants.Player.Barrel.WIDTH * (float)Math.cos(creator.getFacingAngle());
        float y = creator.getCenter().y + PaintingConstants.Player.Barrel.WIDTH * (float)Math.sin(creator.getFacingAngle());

        bullets.add(new Bullet(creator, new Point2D.Float(x, y), creator.getColor(), creator.getFacingAngle(), creator.getStrength()));
    }
}
