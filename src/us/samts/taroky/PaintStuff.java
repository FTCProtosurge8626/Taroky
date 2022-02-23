package us.samts.taroky;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public class PaintStuff extends Canvas {
    private int location;
    VisualTable t;

    public PaintStuff(VisualTable visualTable) {
        location = 5;
        t = visualTable;
    }
    public void paint(Graphics g) {
        //t is assumed to be instantiated

        drawBackground(g);//Clears the screen
        drawTable(g);

        //Paint is the main runner method. It will be called every 10 ms after the Play button is clicked
        //There are 5 stacks of cards on the table, plus player hands and in-play cards
        //Each stack should have a number on it equal to the number of cards. 4 stacks are discard piles (winnings), the last is the talon
        //Any stack with 0 cards should not be drawn at all
        //Player hands should be drawn in full. Only the Human player (players[0]) should have cards face-up
        //All others (players[1,2,3]) should be drawn with cardBack
        //Coins will be drawn in the corners, blue = 10, red = 5, white = 1. Each stack will have a number equal to the number of chips that player has
        //Animations for paying chips and playing/discarding/collecting/drawing/dealing cards will likely be added, not sure when though \_(`-`)_/

        //Animation test
        location++;
        g.setColor(Color.BLACK);
        g.setFont(new Font("TAHOMA",Font.BOLD,12));
        g.drawString("Samuel Mach - Taroky Time!", 50, location%Taroky.height());
        //g.drawImage(Card.getCardBack(),20,30,50, 100, null);
        //drawImage(g, Card.getCardBack(), 100, 200, 45);
    }
    public void writeString(String toWrite, int player) {
        if (player > 3 || player < 0) {throw new Error("Index OOB exception: bad player");}


    }
    public boolean drawMenu(Graphics g, int mouseX, int mouseY) {
        //Draws the menu. Buttons should be centered.
        drawBackground(g);
        int[] x = {Taroky.width()/2+50, Taroky.width()/2-50, Taroky.width()/2-50, Taroky.width()/2+50};
        int[] y = {Taroky.height()/2-20, Taroky.height()/2-20, Taroky.height()/2+20, Taroky.height()/2+20};
        //Play button

        if (mouseX > x[1] && mouseX < x[0] && mouseY > y[0] && mouseY < y[2]) {
            g.setColor(new Color(125, 125, 125));
            g.fillPolygon(x, y, 4);
            g.setFont(new Font("TAHOMA", Font.BOLD, 20));
            g.setColor(Color.DARK_GRAY);
            g.drawString("Play", Taroky.width() / 2 - 20, Taroky.height() / 2 + 10);
            return true;
        } else {
            g.setColor(new Color(125, 125, 125));
            g.drawPolygon(x, y, 4);
            g.setFont(new Font("TAHOMA", Font.BOLD, 20));
            g.drawString("Play", Taroky.width() / 2 - 20, Taroky.height() / 2 + 10);
            return false;
        }
    }
    public void drawTable(Graphics g) {
        //100px on each side
        g.setColor(new Color(103, 62, 0));
        int[] x = {100,100,Taroky.width()-100,Taroky.width()-100};
        int[] y = {100,Taroky.height()-100,Taroky.height()-100,100};
        g.fillPolygon(x,y,4);

        for (int i=0; i< t.getPlayers()[0].getHand().size();i++) {
            //Paints your hand, face up
            g.drawImage(t.getPlayers()[0].getHand().get(0).getImg(),i*50,Taroky.height()-50,null);
        }
    }
    public void drawBackground(Graphics g) {
        g.setColor(new Color(20,20,255));//Background
        int[] x = {0,Taroky.width(),Taroky.width(),0};
        int[] y = {0,0,Taroky.height(),Taroky.height()};
        g.fillPolygon(x,y,4);
    }
    public void drawImage(Graphics g, BufferedImage b, int x, int y, int degreesRotation) {
        double rotationRequired = Math.toRadians (degreesRotation);
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, x, y);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        // Drawing the rotated image at the required drawing locations
        g.drawImage(op.filter(b, null), x, y, 100, 50, null);
    }
}
