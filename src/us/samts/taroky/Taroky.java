package us.samts.taroky;

//Name - Samuel Mach

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class Taroky extends JFrame implements WindowListener, MouseListener, MouseMotionListener, ComponentListener {
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static int WIDTH = (int) screenSize.getWidth();
    private static int HEIGHT = (int) screenSize.getHeight();
    private int mouseX;
    private int mouseY;
    private boolean mouseDown;
    private boolean windowOpen;

    public static void main(String[] args) throws InterruptedException
    {

        //Taroky taroky = new Taroky();
        Table tab = new ConsoleTable();
        tab.startGame();
    }

    public Taroky() {
        super("Taroky");
        windowOpen = true;
        mouseDown = false;
        setSize(WIDTH, HEIGHT);

        addComponentListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);

        GraphicsConfiguration gc = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();

        setAutoRequestFocus(true);
        setVisible(true);
        createBufferStrategy(3);
        setIgnoreRepaint(true);
        setSize(WIDTH,HEIGHT);

        ConsoleTable t = new ConsoleTable();
        PaintStuff ps = new PaintStuff(t);
        getContentPane().add(ps);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JLabel jl = new JLabel();
        final BufferStrategy bufferStrategy = getBufferStrategy();
        if (bufferStrategy == null) {
            System.out.println("BufferStrategy is null");
            throw new Error("BufferStrategy is null");
        }
        BufferedImage bi1 = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

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
                        t.startGame();
                    }
                } else {
                    ps.paint(g);
                }
                jl.setIcon(new ImageIcon(bi1));
                getContentPane().removeAll();
                getContentPane().add(jl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                g.dispose();
            }

            //Wait code, for animation
            timeDiff = System.currentTimeMillis() - beforeTime;
            long DELAY = 10;
            sleep = DELAY - timeDiff;
            if (sleep < 0) {
                sleep = 2;
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.printf("Thread interrupted: %s%n", e.getMessage());
            }
            beforeTime = System.currentTimeMillis();
        }

    }

    public static int width() {
        return WIDTH;
    }
    public static int height() {
        return HEIGHT;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        windowOpen = true;
    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        windowOpen = false;
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

    @Override
    public void mouseClicked(MouseEvent e) {
        //mouseDown = true;

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
        setSize(WIDTH, HEIGHT);
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
}