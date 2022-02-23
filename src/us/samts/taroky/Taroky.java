package us.samts.taroky;

//Name - Samuel Mach

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class Taroky extends JFrame {
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static int WIDTH = (int) (screenSize.getWidth()/4);
    private static int HEIGHT = (int) (screenSize.getHeight()/4);
    private static VisualTable vt;


    public static void main(String[] args) throws InterruptedException {

        new Taroky();
        vt.animate();
        //new ConsoleTable().startGame();
    }

    public Taroky() {
        super("Taroky");
        setSize(WIDTH, HEIGHT);

        vt = new VisualTable(this);

        addComponentListener(vt);
        addMouseMotionListener(vt);
        addMouseListener(vt);
        addKeyListener(vt);

        setAutoRequestFocus(true);
        setVisible(true);
        createBufferStrategy(3);
        setIgnoreRepaint(true);
        setSize(WIDTH,HEIGHT);

        PaintStuff ps = new PaintStuff(vt);
        getContentPane().add(ps);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final BufferStrategy bufferStrategy = getBufferStrategy();
        if (bufferStrategy == null) {
            throw new Error("BufferStrategy is null");
        }

        //This is the JFrame. VisualTable is the Table. Table handles the painting, this handles the window. The window will be updated whenever VT asks for an update.
    }

    public void update() {
        getContentPane().removeAll();
        getContentPane().add(vt.getJL());
    }

    public static int width() {
        return WIDTH;
    }
    public static int height() {
        return HEIGHT;
    }
    public void setWH(int wide, int tall) {WIDTH = wide; HEIGHT = tall;}
}