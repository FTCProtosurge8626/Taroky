package us.samts.taroky;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.Component.*;
import java.util.*;

public class PaintStuff extends Canvas {
    private int location;
    public PaintStuff() {
        location = 5;
    }
    public void paint(Graphics g) {
        location++;
        g.setColor(new Color(20,20,255));
        int[] x = {0,Taroky.width(),Taroky.width(),0};
        int[] y = {0,0,Taroky.height(),Taroky.height()};
        g.fillPolygon(x,y,4);
        g.setColor(Color.BLACK);
        g.setFont(new Font("TAHOMA",Font.BOLD,12));
        g.drawString("Samuel Mach - Taroky Time!", 50, location);
        g.drawImage(Card.getCardBack(),0,0,null);
    }
}
