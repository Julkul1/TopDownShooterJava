package project.gamelogic;

import lombok.Setter;
import project.gamelogic.objects.*;
import project.input.InputState;
import project.gamelogic.objects.basic.StaticObject;
import project.gamelogic.objects.basic.Status;
import project.window.PaintingConstants;
import lombok.Getter;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Game implements Serializable {
    private static final int pointsToWin = 100;
    private static final int pointsPerKill = 5;

    @Getter
    private List<Player> players =  new LinkedList<>();
    @Getter
    private List<Bullet> bullets = new LinkedList<>();
    @Getter
    private List<PowerUp> powerUps = new LinkedList<>();
    private Map<Player, Integer> scoreTable = new HashMap<>();
    @Getter @Setter
    private boolean gameStarted = false;
    @Getter
    private final boolean isServer;

    public Game(boolean isServer) {
        this.isServer = isServer;
    }

    void loadGame(Game gameToLoad){
        this.players = gameToLoad.players;
        this.bullets = gameToLoad.bullets;
        this.powerUps = gameToLoad.powerUps;
        this.scoreTable = gameToLoad.scoreTable;
    }

    public Point2D.Float getRandomLocation(boolean doesCollide, float radius, float bonusRadius, Class<? extends StaticObject>... classes) {
        Random rand = new Random();
        int x = 0, y = 0;

        if (doesCollide) {
            List<StaticObject> collisionObjects = new ArrayList<>();
            if (classes.length == 0) return null;
            // Get with what the random location cannot collie with
            for (Class<? extends StaticObject> clazz : classes) {
                if (Player.class.isAssignableFrom(clazz)) {
                    collisionObjects.addAll(players);
                }
                else if (Bullet.class.isAssignableFrom(clazz)) {
                    collisionObjects.addAll(bullets);
                }
                else if (PowerUp.class.isAssignableFrom(clazz)) {
                    collisionObjects.addAll(powerUps);
                }
                else {
                    return null;
                }
            }

            Point2D.Float point = new Point2D.Float();

            do {
                point.x = rand.nextInt(GameMap.HEIGHT);
                point.y = rand.nextInt(GameMap.WIDTH);
            }while(collisionObjects.stream().anyMatch(obj -> obj.doesCollide(point, radius, bonusRadius)));
            x = (int)point.x;
            y = (int)point.y;

        }
        else {
            y = rand.nextInt(GameMap.HEIGHT);
            x = rand.nextInt(GameMap.WIDTH);
        }
        return new Point2D.Float(x, y);
    }

    public void createPowerUp(){
        Point2D.Float point = getRandomLocation(false, PowerUp.RADIUS, 0);
        powerUps.add(new PowerUp(point));
    }

    public void newPlayerLocation(Player player) {
        Point2D.Float newPosition = this.getRandomLocation(true, player.getRadius(), 60f, Player.class, Bullet.class);
        player.setCenter(newPosition);
    }

    public void update(double deltaTime) {
        // Create power ups on the map
        if (isServer) {
            PowerUp.updateGlobal(deltaTime, this);
        }
        Command command = new Command();

        for (Player player : players) {
            command.setValue("");
            player.update(deltaTime, command);
            if (command.getValue().equals("Add bullet")) {
                addBullet(player);
            }
            collide(player);
        }

        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
            collide(bullet);
        }

        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            powerUp.update(deltaTime);
            if (powerUp.getStatus() == Status.DEAD) {
                iterator.remove();
            }
        }

        bullets.removeIf(bullet -> bullet.getStatus() == Status.DEAD);
        powerUps.removeIf(powerUp -> powerUp.getStatus() == Status.DEAD);
        players.stream().filter(p -> p.getStatus() == Status.DEAD).forEach(player -> {
            player.setHitPoints(GameObjectsConstants.Player.HIT_POINTS);
            player.setStatus(Status.ALIVE);
            newPlayerLocation(player);
        });

    }

    // Function checks if object is dead so it aka don't exist, so it skips it in further processing
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

    public void updatePlayerControl(InputState inputState, int playerID) {
        Player player = getPlayerByID(playerID);
        if (player == null) return;
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

        player.setShooting(inputState.isLeftMouseClick());

        float centerX = (float)PaintingConstants.View.WIDTH / 2;
        float centerY = (float)PaintingConstants.View.HEIGHT / 2;
        double facingAngle = Math.atan2(inputState.getCursorY() - centerY, inputState.getCursorX() - centerX);
        double moveAngle = Math.atan2(y - 0.0, x - 0.0);

        player.setMoveAngle(moveAngle);
        player.setFacingAngle(facingAngle);
        player.setMoving(isMoving);
    }

    public void addPlayer(Player newPlayer) {
        newPlayerLocation(newPlayer);
        players.add(newPlayer);
        scoreTable.put(newPlayer, 0);
    }

    public void addBullet(Player creator) {
        // Create bullet in front of barrel of a player
        float x = creator.getCenter().x + PaintingConstants.Player_Paint.Barrel.WIDTH * (float)Math.cos(creator.getFacingAngle());
        float y = creator.getCenter().y + PaintingConstants.Player_Paint.Barrel.WIDTH * (float)Math.sin(creator.getFacingAngle());

        bullets.add(new Bullet(creator, new Point2D.Float(x, y), creator.getFacingAngle(), creator.getStrength()));
    }

    public Player getPlayerByID(int playerID) {
        Optional<Player> optionalPlayer = players.stream().filter(p -> p.getID() == playerID).findFirst();
        // Didn't find player with a correct ID return null else player object
        return optionalPlayer.orElse(null);
    }
}
