package project;

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
            case KeyEvent.VK_W:
                inputState.setUpKeyPressed(true);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                inputState.setDownKeyPressed(true);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                inputState.setLeftKeyPressed(true);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                inputState.setRightKeyPressed(true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                inputState.setUpKeyPressed(false);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                inputState.setDownKeyPressed(false);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                inputState.setLeftKeyPressed(false);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                inputState.setRightKeyPressed(false);
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        switch(mouseEvent.getButton()) {
            case MouseEvent.BUTTON1:
                inputState.setLeftMouseClick(true);
                break;
        }
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
