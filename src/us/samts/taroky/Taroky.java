package us.samts.taroky;

//Name - Samuel Mach

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;

public class Taroky extends JFrame implements ActionListener {
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int WIDTH = (int) screenSize.getWidth();
    private static final int HEIGHT = (int) screenSize.getHeight();
    final BufferedImage bi1;
    private long DELAY = 10;

    public Taroky()	throws InterruptedException {
        super("Graphics Runner");

        GraphicsConfiguration gc = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();

        setAutoRequestFocus(true);
        setVisible(true);
        createBufferStrategy(2);
        setIgnoreRepaint(true);
        setSize(WIDTH,HEIGHT);
        PaintStuff ps = new PaintStuff();
        getContentPane().add(ps);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JLabel jl = new JLabel();
        final BufferStrategy bufferStrategy = getBufferStrategy();
        Graphics window = null;
        if (bufferStrategy == null) {
            System.out.println("BufferStrategy is null");
            throw new Error("BufferStrategy is null");
        }
        bi1 = new BufferedImage( WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB );

        Graphics g;//Create graphics buffer

        long beforeTime, timeDiff, sleep;
        beforeTime = System.currentTimeMillis();
        while ( true ) {
            g = bi1.getGraphics();
            window = bufferStrategy.getDrawGraphics();
            try {
                ps.paint(g);//Paint onto the Graphics buffer
                //window.drawImage(bi1, 0, 0, null);//Draw the image to the screen
                jl.setIcon(new ImageIcon(bi1));
                getContentPane().removeAll();
                getContentPane().add(jl);
            } finally {
                g.dispose();
            }

            //bufferStrategy.show();

            //Wait code, for animation
            timeDiff = System.currentTimeMillis() - beforeTime;
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

    public static void main(String[] args) throws InterruptedException
    {
        //Taroky taroky = new Taroky();
        Table tab = new Table();
        tab.startGame();
    }
    public static int width() {
        return WIDTH;
    }
    public static int height() {
        return HEIGHT;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}