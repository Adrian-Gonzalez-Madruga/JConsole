package jconsole;

import DLibX.DConsole;
import java.awt.Color;

public class Main {
    
    public static void main(String[] args) {
        int x = 0;
        int y = 0;
        
        int num = 7;
        int[] xp = {0, 0, -num, num};
        int[] yp = {-num, num, 0, 0};
        
        Color[] c = {Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW};
        
        DConsole dc = new DConsole(800,800);
        JConsole j = new JConsole(0);
        
        while(true) {
            dc.setPaint(new Color(0,0,0,10));
            dc.fillRect(0,0,1000,1000);
            dc.setPaint(Color.MAGENTA);
            
            for(int q = 0; q < 4; q++) { // test button
                if(j.isButtonPressed(q)) {
                    dc.setPaint(c[q]);
                }
            }
            
            dc.fillEllipse(x, y, 35, 35);
            
            x += (j.analogHorizontal(0) - 50) / 4; //test analog
            y += (j.analogVertical(0) - 50) / 4;
            
            for(int q = 0; q < 4; q++) { // test DPAD
                if(j.isDpadPressed(q)) {
                    x += xp[q];
                    y += yp[q];
                }
            }
            
            dc.redraw();
            dc.pause(20);
            
        }
        
    }
    
    
}

