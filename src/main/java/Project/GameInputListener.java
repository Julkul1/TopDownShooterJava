package Project;

import java.awt.event.*;

public class GameInputListener implements KeyListener, MouseListener, MouseMotionListener {
    private final InputState inputState;
    public GameInputListener(InputState inputState) {
        this.inputState = inputState;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                inputState.setUpKeyPressed(true);
                break;
            case KeyEvent.VK_DOWN:
                inputState.setDownKeyPressed(true);
                break;
            case KeyEvent.VK_LEFT:
                inputState.setLeftKeyPressed(true);
                break;
            case KeyEvent.VK_RIGHT:
                inputState.setRightKeyPressed(true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                inputState.setUpKeyPressed(false);
                break;
            case KeyEvent.VK_DOWN:
                inputState.setDownKeyPressed(false);
                break;
            case KeyEvent.VK_LEFT:
                inputState.setLeftKeyPressed(false);
                break;
            case KeyEvent.VK_RIGHT:
                inputState.setRightKeyPressed(false);
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        inputState.setCursorX(e.getX());
        inputState.setCursorY(e.getY());
    }
}
