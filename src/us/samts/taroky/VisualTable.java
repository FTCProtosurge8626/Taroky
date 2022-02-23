package us.samts.taroky;

//Name - Samuel Mach

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class VisualTable extends Table implements WindowListener, MouseListener, MouseMotionListener, ComponentListener, KeyListener {
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static int WIDTH = (int) (screenSize.getWidth()/4);
    private static int HEIGHT = (int) (screenSize.getHeight()/4);
    private int mouseX;
    private int mouseY;
    private boolean mouseDown;
    private boolean windowOpen;
    private BufferedImage bi1;
    private final JLabel jl = new JLabel();
    Taroky taroky;
    PaintStuff ps;

    public VisualTable(Taroky taroky) {
        super(0);
        this.taroky = taroky;
        getPlayers()[0] = new Human("Sam", this);
        getPlayers()[1] = new Robot("Thomas");
        getPlayers()[2] = new Robot("Henry");
        getPlayers()[3] = new Robot("Jill");

        windowOpen = true;
        mouseDown = false;
        ps = new PaintStuff(this);
    }

    public void animate() {
        Graphics g;//Create graphics buffer

        long beforeTime, timeDiff, sleep;
        beforeTime = System.currentTimeMillis();

        boolean inMenu = true;

        while (windowOpen) {
            bi1 = new BufferedImage( WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB );
            g = bi1.getGraphics();
            try {
                if (inMenu) {
                    if (ps.drawMenu(g, mouseX, mouseY) && mouseDown) {
                        inMenu = false;
                        //startGame();
                    }
                } else {
                    ps.paint(g);
                }
                jl.setIcon(new ImageIcon(bi1));
                taroky.update();
            } finally {
                g.dispose();
            }

            //Wait code, for animation
            timeDiff = System.currentTimeMillis() - beforeTime;
            long DELAY = 10;
            sleep = DELAY - timeDiff;
            if (sleep < 0) {
                sleep = 2;//Sleep will be 2-10 ms long
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.printf("Thread interrupted: %s%n", e.getMessage());
            }
            beforeTime = System.currentTimeMillis();
        }
    }

    public BufferedImage getBI() {return bi1;}
    public JLabel getJL() {return jl;}
    public void setBI(BufferedImage set) {bi1 = set;}

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        final Point pos = e.getPoint();
        mouseX = pos.x;
        mouseY = pos.y;

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        final Point pos = e.getPoint();
        mouseX = pos.x;
        mouseY = pos.y;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        WIDTH = e.getComponent().getWidth();
        HEIGHT = e.getComponent().getHeight();
        taroky.setWH(WIDTH, HEIGHT);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void message(String message) {

    }

    @Override
    public String getInputString(String message) {
        return null;
    }

    @Override
    public boolean getInputBoolean(String message) {
        return false;
    }

    @Override
    public int getInputInt(String message) {
        return 0;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}