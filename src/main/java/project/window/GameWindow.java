package project.window;

import project.client.Client;
import project.input.GameInputListener;
import project.gamelogic.Game;
import project.gamelogic.objects.Bullet;
import project.gamelogic.objects.Player;
import project.gamelogic.objects.PowerUp;
import project.server.LobbyData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class GameWindow extends JPanel implements PaintingConstants {
    private final Client client;
    private JFrame frame;

    private int mainPlayerID;
    private Game game;
    private LobbyData lobby;

    public GameWindow(Client client, GameInputListener gameInputListener) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Top Down Map View");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(this);
            frame.pack();
            frame.setVisible(true);
            frame.setResizable(false);
        });
        this.client = client;

        setPreferredSize(new Dimension(View.WIDTH, View.HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(gameInputListener);
        addMouseListener(gameInputListener);
        addMouseMotionListener(gameInputListener);
    }

    public void closeWindow() {
        if (frame != null) {
            frame.dispose();
        }
    }

    private void drawPlayer(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();

        for (Player player : game.getPlayers()) {
            if (player.getID() == mainPlayerID) continue; // skip main player in drawing

            Point2D.Float center = player.getCenter();
            int playerX = (int)center.getX() - (int)player.getRadius();
            int playerY = (int)center.getY() - (int)player.getRadius();
            int playerDiameter = (int)player.getRadius() * 2;

            int barrelX = (int)center.getX() - Player_Paint.Barrel.ROTATION_PIVOT_X;
            int barrelY = (int)center.getY() - Player_Paint.Barrel.ROTATION_PIVOT_Y;

            // Paint barrel
            g2.rotate(player.getFacingAngle(), (int)center.getX(), (int)center.getY());
            g2.setStroke(new BasicStroke(Player_Paint.Barrel.OUTLINE_THICKNESS));
            g2.setColor(Color.GRAY);
            g2.fillRect(barrelX, barrelY, Player_Paint.Barrel.WIDTH, Player_Paint.Barrel.HEIGHT);
            g2.setColor(Color.BLACK);
            g2.drawRect(barrelX, barrelY, Player_Paint.Barrel.WIDTH, Player_Paint.Barrel.HEIGHT);
            g2.rotate(-player.getFacingAngle(), (int)center.getX(), (int)center.getY());

            // Paint player
            g2.setStroke(new BasicStroke(Player_Paint.OUTLINE_THICKNESS));
            g2.setColor(Player_Paint.COLOR_ENEMY);
            g2.fillOval(playerX, playerY, playerDiameter, playerDiameter);
            g2.setColor(Color.BLACK);
            g2.drawOval(playerX, playerY, playerDiameter, playerDiameter);
        }

        g2.setStroke(oldStroke);
    }

    public void drawMainPlayer(Graphics2D g2, Player mainPlayer) {
        Stroke oldStroke = g2.getStroke();

        int viewCenterX = View.WIDTH / 2;
        int viewCenterY = View.HEIGHT / 2;

        int playerX = viewCenterX - (int)mainPlayer.getRadius();
        int playerY = viewCenterY -  (int)mainPlayer.getRadius();
        int playerDiameter =  (int)mainPlayer.getRadius() * 2;

        int barrelX = viewCenterX - Player_Paint.Barrel.ROTATION_PIVOT_X;
        int barrelY = viewCenterY - Player_Paint.Barrel.ROTATION_PIVOT_Y;

        // Paint barrel
        g2.rotate(mainPlayer.getFacingAngle(), viewCenterX, viewCenterY);
        g2.setStroke(new BasicStroke(Player_Paint.Barrel.OUTLINE_THICKNESS));
        g2.setColor(Color.GRAY);
        g2.fillRect(barrelX, barrelY, Player_Paint.Barrel.WIDTH, Player_Paint.Barrel.HEIGHT);
        g2.setColor(Color.BLACK);
        g2.drawRect(barrelX, barrelY, Player_Paint.Barrel.WIDTH, Player_Paint.Barrel.HEIGHT);
        g2.rotate(-mainPlayer.getFacingAngle(), viewCenterX, viewCenterY);

        // Paint player
        g2.setStroke(new BasicStroke(Player_Paint.OUTLINE_THICKNESS));
        g2.setColor(Player_Paint.COLOR_FRIENDLY);
        g2.fillOval(playerX, playerY, playerDiameter, playerDiameter);
        g2.setColor(Color.BLACK);
        g2.drawOval(playerX, playerY, playerDiameter, playerDiameter);

        g2.setStroke(oldStroke);
    }
    private void drawBullets(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();

        Point2D.Float bulletCenter;
        int bulletX;
        int bulletY;
        int bulletDiameter;

        for (Bullet bullet: game.getBullets()) {
            bulletCenter = bullet.getCenter();
            bulletX = (int)bulletCenter.getX() - (int)bullet.getRadius();
            bulletY = (int)bulletCenter.getY() - (int)bullet.getRadius();
            bulletDiameter = (int)bullet.getRadius() * 2;

            // Bullet color dependent on creator of a bullet
            g2.setColor(bullet.getCreator().getID() == mainPlayerID ? Player_Paint.COLOR_FRIENDLY : Player_Paint.COLOR_ENEMY);
            g2.fillOval(bulletX, bulletY, bulletDiameter, bulletDiameter);
            g2.setColor(Color.BLACK);
            g2.drawOval(bulletX, bulletY, bulletDiameter, bulletDiameter);
        }
        g2.setStroke(oldStroke);
    }

    private void drawPowerUp(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(Player_Paint.OUTLINE_THICKNESS));

        Point2D.Float PowerUpCenter;
        int PowerUpX;
        int PowerUpY;
        int PowerUpDiameter;

        for (PowerUp powerUp: game.getPowerUps()) {
            PowerUpCenter = powerUp.getCenter();
            PowerUpX = (int)PowerUpCenter.getX() - (int)powerUp.getRadius();
            PowerUpY = (int)PowerUpCenter.getY() - (int)powerUp.getRadius();
            PowerUpDiameter = (int)powerUp.getRadius() * 2;

            g2.setColor(PowerUp_Paint.COLOR);
            g2.fillOval(PowerUpX, PowerUpY, PowerUpDiameter, PowerUpDiameter);
            g2.setColor(Color.BLACK);
            g2.drawOval(PowerUpX, PowerUpY, PowerUpDiameter, PowerUpDiameter);
        }
        g2.setStroke(oldStroke);
    }

    private void drawMap(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();

        int viewCenterX = 0;
        int viewCenterY = 0;
        int mapX = viewCenterX - (int)Map_Paint.OUTLINE_THICKNESS / 2;
        int mapY = viewCenterY - (int)Map_Paint.OUTLINE_THICKNESS / 2;
        int width = (int)Map_Paint.WIDTH;
        int height = (int)Map_Paint.HEIGHT;

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(Map_Paint.OUTLINE_THICKNESS));
        g2.drawRect(mapX, mapY, width, height);

        g2.setStroke(oldStroke);
    }

    private void drawScoreboard(Graphics2D g2, Player mainPlayer) {
        Stroke oldStroke = g2.getStroke();
        LinkedHashMap<Player, Integer> scoreTable = game.getScoreTable();

        Font font = new Font("Arial", Font.BOLD, Scoreboard_Paint.FONT_SIZE);
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics(font);

        // Text parameters
        int widestTextWidth = 0;
        int scoreboardX = View.WIDTH;
        int lineHeight = metrics.getHeight();
        int textY = Scoreboard_Paint.Y_OFFSET + metrics.getAscent();
        int topY = textY;

        // Print player's strength
        String text = "Strength: " + mainPlayer.getStrength();
        int textWidth = metrics.stringWidth(text);
        int textX = (View.WIDTH - textWidth) / 2 + Scoreboard_Paint.X_OFFSET;
        g2.drawString(text, textX, textY);
        textY += 2 * lineHeight;
        if (textWidth > widestTextWidth) widestTextWidth = textWidth;
        if (textX < scoreboardX) scoreboardX = textX;

        if (!game.isGameOver()) {
            text = "Scoreboard:";
        }
        else {
            text = "Game Over!";
        }
        textWidth = metrics.stringWidth(text);
        textX = (View.WIDTH - textWidth) / 2 + Scoreboard_Paint.X_OFFSET;
        g2.drawString(text, textX, textY);
        textY += lineHeight;
        if (textWidth > widestTextWidth) widestTextWidth = textWidth;
        if (textX < scoreboardX) scoreboardX = textX;

        // Print players points
        g2.setColor(Color.RED);
        for (Player player : scoreTable.keySet()) {
            if (player.getID() == mainPlayer.getID()) {
                text = player.getID() + " (You): " + scoreTable.get(player);
            }
            else {
                text = player.getID() + ": " + scoreTable.get(player);
            }
            if (game.isGameOver() && scoreTable.get(player) >= Game.getPointsToWin()) {
                text += " WINNER";
            }
            textWidth = metrics.stringWidth(text);
            textX = (View.WIDTH - textWidth) / 2 + Scoreboard_Paint.X_OFFSET;
            g2.drawString(text, textX, textY);
            textY += lineHeight;

            if (textWidth > widestTextWidth) widestTextWidth = textWidth;
            if (textX < scoreboardX) scoreboardX = textX;
        }

        // Print outline
        int totalTextHeight = textY - topY;
        g2.setColor(Color.BLACK);
        g2.drawRect(scoreboardX - Scoreboard_Paint.OUTLINE_BONUS_WIDTH / 2, topY - metrics.getAscent(), widestTextWidth + Scoreboard_Paint.OUTLINE_BONUS_WIDTH, totalTextHeight);

        g2.setStroke(oldStroke);
    }

    private void drawLobby(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();

        List<String> lobbyText = new ArrayList<>();
        if (client.getPlayerID() > 0) {
            lobbyText.add("Połączono z lobby");
            lobbyText.add("Ilość graczy: " + lobby.getReadyPlayers());
            lobbyText.add("Wymagana ilość graczy: " + lobby.getRequiredMinPlayers());
            if (lobby.getReadyPlayers() >= lobby.getRequiredMinPlayers()) {
                lobbyText.add("Gra rozpocznie się za " + lobby.getLobbyStartTimer() + "s");
            }
        }
        else {
            lobbyText.add("Dołączanie do lobby...");
        }

        Font font = new Font("Arial", Font.BOLD, Lobby_Paint.FONT_SIZE);
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics(font);
        int lineHeight = metrics.getHeight();
        int totalTextHeight = lobbyText.size() * lineHeight;
        int textY = (View.HEIGHT - totalTextHeight) / 2 + metrics.getAscent();

        g2.setColor(Color.RED);
        for (String text : lobbyText) {
            int textWidth = metrics.stringWidth(text);
            int textX = (View.WIDTH - textWidth) / 2;
            g2.drawString(text, textX, textY);
            textY += lineHeight;
        }

        g2.setStroke(oldStroke);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        game = client.getGame();
        lobby = client.getLobbyData();

        if (lobby.isGameStarted()) {
            mainPlayerID = client.getPlayerID();
            Player mainPlayer = game.getPlayerByID(mainPlayerID);
            if (mainPlayer == null) return;

            // Translate every object based on player position so player is in the center of a frame
            Graphics2D g2 = (Graphics2D) g;
            int translateX = (int)mainPlayer.getCenter().getX() - View.WIDTH / 2;
            int translateY = (int)mainPlayer.getCenter().getY() - View.HEIGHT / 2;
            g2.translate(-translateX, -translateY);

            drawMap(g2);
            drawPlayer(g2);
            drawBullets(g2);
            drawPowerUp(g2);

            g2.translate(translateX, translateY);

            drawScoreboard(g2, mainPlayer);
            drawMainPlayer(g2, mainPlayer);
        }
        else {
            Graphics2D g2 = (Graphics2D) g;
            drawLobby(g2);
        }
    }
}
