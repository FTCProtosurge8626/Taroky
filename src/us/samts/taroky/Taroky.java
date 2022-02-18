package us.samts.taroky;

//Name - Samuel Mach

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.Component.*;

public class Taroky extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    final BufferedImage bi1;
    private long DELAY = 10;

    public Taroky()	throws InterruptedException {
        super("Graphics Runner");

        GraphicsConfiguration gc = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();


        setVisible(true);
        createBufferStrategy(2);
        setIgnoreRepaint(true);
        setSize(WIDTH,HEIGHT);
        Table table = new Table();
        PaintStuff ps = new PaintStuff();
        getContentPane().add(ps);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JLabel jl = new JLabel();
        final BufferStrategy bufferStrategy = getBufferStrategy();

        if (bufferStrategy == null) {
            System.out.println("BufferStrategy is null");
        }

        bi1 = new BufferedImage( WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB );

        final Thread t = new Thread( new Runnable() {
            public void run() {
                long beforeTime, timeDiff, sleep;
                beforeTime = System.currentTimeMillis();
                while ( true ) {
                    Graphics g = bi1.getGraphics();//Create graphics buffer
                    assert bufferStrategy != null;
                    Graphics window = bufferStrategy.getDrawGraphics();

                    try {
                        ps.paint(g);
                        window.drawImage(bi1, 0, 0, null);//Draw the image to the screen
                    } finally {
                        g.dispose();
                        window.dispose();
                    }

                    bufferStrategy.show();

                    //Wait code, for animation
                    timeDiff = System.currentTimeMillis() - beforeTime;
                    sleep = DELAY - timeDiff;
                    if (sleep < 0) {
                        sleep = 2;
                    }
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        String msg = String.format("Thread interrupted: %s", e.getMessage());
                    }
                    beforeTime = System.currentTimeMillis();
                }
            }
        });

        table.startGame();
        t.start();
        jl.setIcon( new ImageIcon( bi1 ) );
        getContentPane().add( jl );
    }

    public static void main(String[] args) throws InterruptedException
    {
        boolean graphics = true;
        if (graphics) {
            Taroky taroky = new Taroky();
        } else {
            Table table = new Table();
            table.startGame();
        }
    }
    public static int width() {
        return WIDTH;
    }
    public static int height() {
        return HEIGHT;
    }
}