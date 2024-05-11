import javax.swing.*;
import java.awt.*;

public class GameWindow extends JPanel implements PaintingConstants {
    private final PlayerObject player;

    public GameWindow(GameInputListener gameInputListener, PlayerObject player) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Top Down Map View");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(this);
            frame.pack();
            frame.setVisible(true);
        });
        this.player = player;

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
        g2.rotate(player.getAngle(), viewCenterX, viewCenterY);
        g2.setStroke(new BasicStroke(Player.Barrel.OUTLINE_THICKNESS));
        g2.setColor(Color.GRAY);
        g2.fillRect(barrelX, barrelY, Player.Barrel.WIDTH, Player.Barrel.HEIGHT);
        g2.setColor(Color.BLACK);
        g2.drawRect(barrelX, barrelY, Player.Barrel.WIDTH, Player.Barrel.HEIGHT);
        g2.rotate(-player.getAngle(), viewCenterX, viewCenterY);

        // Paint player
        g2.setStroke(new BasicStroke(Player.OUTLINE_THICKNESS));
        g2.setColor(Color.YELLOW);
        g2.fillOval(playerX, playerY, playerDiameter, playerDiameter);
        g2.setColor(Color.BLACK);
        g2.drawOval(playerX, playerY, playerDiameter, playerDiameter);

        g2.setStroke(oldStroke);
    }

    private void drawMap(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();

        int viewCenterX = View.WIDTH / 2;
        int viewCenterY = View.HEIGHT / 2;
        int mapX = viewCenterX - (int)Map.OUTLINE_THICKNESS  / 2 - (int)player.getPlayerX();
        int mapY = viewCenterY - (int)Map.OUTLINE_THICKNESS / 2 - (int)player.getPlayerY();

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(Map.OUTLINE_THICKNESS));
        g2.drawRect(mapX, mapY, 2000, 2000);

        g2.setStroke(oldStroke);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        Graphics2D g2 = (Graphics2D) g;
        drawMap(g2);
        drawPlayer(g2);
    }

}
