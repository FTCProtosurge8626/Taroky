package us.samts.taroky;

//Name - Samuel Mach

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;

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
    private ArrayList<String> messages;
    private ArrayList<Long> messageTimer;
    private int step;
    private long tempWaitTime;

    public VisualTable(Taroky taroky) {
        super(0);
        this.taroky = taroky;
        //Currently no way for Human to join. A new method of inputs must be created
        getPlayers()[0] = new Robot("Sam");
        getPlayers()[1] = new Robot("Thomas");
        getPlayers()[2] = new Robot("Henry");
        getPlayers()[3] = new Robot("Jill");
        messages = new ArrayList<>();
        messageTimer = new ArrayList<>();
        step = 0;

        windowOpen = true;
        mouseDown = false;
        ps = new PaintStuff(this);
    }

    public VisualTable(Taroky taroky, double[][][] seed) {
        super(500);
        this.taroky = taroky;
        //Currently no way for Human to join. A new method of inputs must be created
        getPlayers()[0] = new AI("Charlie",0,this,seed);
        getPlayers()[1] = new AI("Thomas",1,this,seed);
        getPlayers()[2] = new AI("Henry",2,this,seed);
        getPlayers()[3] = new AI("Jill",3,this,seed);
        messages = new ArrayList<>();
        messageTimer = new ArrayList<>();
        step = 0;

        windowOpen = true;
        mouseDown = false;
        ps = new PaintStuff(this);
    }

    public void animate() {
        Graphics g;//Create graphics buffer

        long beforeTime, timeDiff, sleep;
        beforeTime = System.currentTimeMillis();

        boolean preverCheck = false;

        while (windowOpen) {
            bi1 = new BufferedImage( WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB );
            g = bi1.getGraphics();
            try {
                switch (step) {
                    case 0:
                        if (ps.drawMenu(g, mouseX, mouseY) && mouseDown) {
                            step++;
                            resetTable();
                            ps.paint(g);
                        }
                        break;
                    case 1:
                        roundHandler(true);
                        ps.paint(g);
                        step++;
                        break;
                    case 2:
                        preverCheck = preverCheck();
                        ps.paint(g);
                        step++;
                        break;
                    default:
                        ps.paint(g);
                }
                for (int i=messageTimer.size()-1;i>=0;i--)
                    if (messageTimer.get(i)<=0) {
                        messageTimer.remove(i);
                        messages.remove(i);
                    }
                ps.drawMessages(g,messages);
                jl.setIcon(new ImageIcon(bi1));
                taroky.update();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                g.dispose();
            }

            //Wait code, for animation
            timeDiff = System.currentTimeMillis() - beforeTime;
            for (int i=0;i<messageTimer.size();i++)
                messageTimer.set(i,messageTimer.get(i)-timeDiff);//Lower time remaining for the messages
            long DELAY = 10;
            DELAY += tempWaitTime;
            tempWaitTime = 0;
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

    public void waitFor(long waitTime) {
        tempWaitTime += waitTime;
    }

    @Override
    public void startGame() throws InterruptedException {
        //Called to start a game
        resetTable();
        roundHandler(true);
        hand(preverCheck());
    }


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
        messages.add(message);
        messageTimer.add(5000L);
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