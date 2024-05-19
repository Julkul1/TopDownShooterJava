package project.window;

import project.GameInputListener;
import project.gamelogic.Game;
import project.gamelogic.GameMap;
import project.gamelogic.objects.Bullet;
import project.gamelogic.objects.Player;
import project.gamelogic.objects.PowerUp;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;

public class GameWindow extends JPanel implements PaintingConstants {
    private final Game game;

    public GameWindow(Game game, GameInputListener gameInputListener) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Top Down Map View");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(this);
            frame.pack();
            frame.setVisible(true);
            frame.setResizable(false);
        });
        this.game = game;

        setPreferredSize(new Dimension(View.WIDTH, View.HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(gameInputListener);
        addMouseListener(gameInputListener);
        addMouseMotionListener(gameInputListener);
    }

    private void drawPlayer(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();

        int viewCenterX = View.WIDTH / 2;
        int viewCenterY = View.HEIGHT / 2;

        int playerX = viewCenterX - Player.RADIUS;
        int playerY = viewCenterY - Player.RADIUS;
        int playerDiameter = Player.RADIUS * 2;

        int barrelX = viewCenterX - Player.Barrel.ROTATION_PIVOT_X;
        int barrelY = viewCenterY - Player.Barrel.ROTATION_PIVOT_Y;

        // Paint barrel
        g2.rotate(game.getMainPlayer().getFacingAngle(), viewCenterX, viewCenterY);
        g2.setStroke(new BasicStroke(Player.Barrel.OUTLINE_THICKNESS));
        g2.setColor(Color.GRAY);
        g2.fillRect(barrelX, barrelY, Player.Barrel.WIDTH, Player.Barrel.HEIGHT);
        g2.setColor(Color.BLACK);
        g2.drawRect(barrelX, barrelY, Player.Barrel.WIDTH, Player.Barrel.HEIGHT);
        g2.rotate(-game.getMainPlayer().getFacingAngle(), viewCenterX, viewCenterY);

        // Paint player
        /*g2.setStroke(new BasicStroke(Player.OUTLINE_THICKNESS));
        g2.setColor(game.getMainPlayer().getColor());
        g2.fillOval(playerX, playerY, playerDiameter, playerDiameter);
        g2.setColor(Color.BLACK);
        g2.drawOval(playerX, playerY, playerDiameter, playerDiameter);*/

        //Paint players
        List playerList = game.getPlayers();
        project.gamelogic.objects.Player mainPlayer = game.getMainPlayer();
        float mainPlayerX = mainPlayer.getCenter().x;
        float mainPlayerY = mainPlayer.getCenter().y;
        for(int i = 0; i < playerList.size(); i++){



            g2.setStroke(new BasicStroke(Player.OUTLINE_THICKNESS));
            project.gamelogic.objects.Player currentPlayer = (project.gamelogic.objects.Player)playerList.get(i);

            float otherPlayerX = currentPlayer.getCenter().x;
            float otherPlayerY = currentPlayer.getCenter().y;
            g2.setColor(currentPlayer.getColor());
            g2.fillOval((int)(otherPlayerX-mainPlayerX)+playerX, (int)(otherPlayerY-mainPlayerY)+playerY, playerDiameter, playerDiameter);
            g2.setColor(Color.BLACK);
            g2.drawOval((int)(otherPlayerX-mainPlayerX)+playerX, (int)(otherPlayerY-mainPlayerY)+playerY, playerDiameter, playerDiameter);
        }

        g2.setStroke(oldStroke);
    }

    private void drawBullets(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();
        int translateX = (int)game.getMainPlayer().getCenter().getX() - View.WIDTH / 2;
        int translateY = (int)game.getMainPlayer().getCenter().getY() - View.HEIGHT / 2;
        g2.translate(-translateX, -translateY);
        g2.setStroke(new BasicStroke(Player.OUTLINE_THICKNESS));

        Point2D.Float bulletCenter;
        int bulletX;
        int bulletY;
        int bulletDiameter;

        for (Bullet bullet: game.getBullets()) {
            bulletCenter = bullet.getCenter();
            bulletX = (int)bulletCenter.getX() - (int)bullet.getRadius();
            bulletY = (int)bulletCenter.getY() - (int)bullet.getRadius();
            bulletDiameter = (int)bullet.getRadius() * 2;

            g2.setColor(bullet.getColor());
            g2.fillOval(bulletX, bulletY, bulletDiameter, bulletDiameter);
            g2.setColor(Color.BLACK);
            g2.drawOval(bulletX, bulletY, bulletDiameter, bulletDiameter);
        }
        g2.translate(translateX, translateY);
        g2.setStroke(oldStroke);
    }

    private void drawPowerUp(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();
        int translateX = (int)game.getMainPlayer().getCenter().getX() - View.WIDTH / 2;
        int translateY = (int)game.getMainPlayer().getCenter().getY() - View.HEIGHT / 2;
        g2.translate(-translateX, -translateY);
        g2.setStroke(new BasicStroke(Player.OUTLINE_THICKNESS));

        Point2D.Float PowerUpCenter;
        int PowerUpX;
        int PowerUpY;
        int PowerUpDiameter;

        for (PowerUp powerUp: game.getPowerUps()) {
            PowerUpCenter = powerUp.getCenter();
            PowerUpX = (int)PowerUpCenter.getX() - (int)powerUp.getRadius();
            PowerUpY = (int)PowerUpCenter.getY() - (int)powerUp.getRadius();
            PowerUpDiameter = (int)powerUp.getRadius() * 2;

            g2.setColor(powerUp.getColor());
            g2.fillOval(PowerUpX, PowerUpY, PowerUpDiameter, PowerUpDiameter);
            g2.setColor(Color.BLACK);
            g2.drawOval(PowerUpX, PowerUpY, PowerUpDiameter, PowerUpDiameter);
        }
        g2.translate(translateX, translateY);
        g2.setStroke(oldStroke);
    }

    private void drawMap(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();

        int viewCenterX = View.WIDTH / 2;
        int viewCenterY = View.HEIGHT / 2;
        int translateX = (int)game.getMainPlayer().getCenter().getX();
        int translateY = (int)game.getMainPlayer().getCenter().getY();
        int mapX = viewCenterX - (int)Map.OUTLINE_THICKNESS / 2;
        int mapY = viewCenterY - (int)Map.OUTLINE_THICKNESS / 2;
        int width = GameMap.WIDTH + (int)Map.OUTLINE_THICKNESS;
        int height = GameMap.HEIGHT + (int)Map.OUTLINE_THICKNESS;

        g2.translate(-translateX, -translateY);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(Map.OUTLINE_THICKNESS));
        g2.drawRect(mapX, mapY, width, height);
        g2.translate(translateX, translateY);

        g2.setStroke(oldStroke);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        drawMap(g2);
        drawPlayer(g2);
        drawBullets(g2);
        drawPowerUp(g2);
    }

}
