package gruniozerca;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import java.io.*;
import sun.audio.*;
import sun.plugin2.liveconnect.JSExceptions;

public class Gruniozerca implements MouseListener, KeyListener
{
    public static Gruniozerca gruniozerca;
    public final int WIDTH = 800, HEIGHT = 639;
    public Renderer renderer;
    public Rectangle bird;
    public int ticks, ticks2, score = 0;
    public ArrayList<Rectangle> columns;
    public Random rand;
    public boolean gameOver, started;
    public int w = 800;
    public int h = 600;
    public int s = 600;
    public float marchewy = 0;
    public float gruniox = 0.5f;
    public boolean pause = false;
    public boolean run = false;
    public boolean prawy = false;
    public boolean lewy = false;
    public boolean czyGrunio = true;
    public boolean czyTrzesienie = false;

    public Image background;
    public Image startBackground;
    public Image pauseBackground;
    public Image exit;
    //public Timer timer = new Timer(20, this);
    public Timer zegar;
    private boolean direction = true;
    private boolean czySpacja = false;

    public Gruniozerca()
    {
        JFrame jframe = new JFrame();

        renderer = new Renderer();
        rand = new Random();

        jframe.add(renderer);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addMouseListener(this);
        jframe.addKeyListener(this);
        jframe.setResizable(true);
        jframe.setTitle("Gruniożerca");
        jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jframe.setUndecorated(true);
        jframe.setVisible(true);
        jframe.addComponentListener(new ResizeListener());

        background = new ImageIcon("img/background.jpg").getImage();
        startBackground = new ImageIcon("img/start.jpg").getImage();
        pauseBackground = new ImageIcon("img/pause.jpg").getImage();
        exit = new ImageIcon("img/exit.png").getImage();

        //bird = new Rectangle(WIDTH/2 -10, HEIGHT/2 -10, 20,20);
        //columns = new ArrayList<Rectangle>();

        //addColumn(true);
        //addColumn(true);
        //addColumn(true);
        //addColumn(true);
        //File Clap = new File("aud/zycie.WAV");
        /*try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Clap));
            clip.start();

            Thread.sleep(clip.getMicrosecondLength()/1000);
        }catch(Exception e){
            System.out.print("Cos poszlo nie tak\n");
        } */
;


        //timer.start();
        zegar = new Timer();
        zegar.scheduleAtFixedRate(new Zadanie(),0,10);
    }


    class ResizeListener implements ComponentListener {

        public void componentHidden(ComponentEvent e) {}
        public void componentMoved(ComponentEvent e) {}
        public void componentShown(ComponentEvent e) {}

        public void componentResized(ComponentEvent e) {
            Dimension newSize = e.getComponent().getBounds().getSize();         //pobiera wymiary okna (odejmuje od wysości 39 bo jest pasek tytułowy)
            h = newSize.height;
            w = newSize.width;
            s = Math.min(h,w);
            //System.out.print("Wysokosc: " + h + " Szerokosc: " + w + "\n");
        }
    }

    class Zadanie extends TimerTask{

        public void run() {
            if (started && !pause) {
                ticks++;
                if (collision()) {
                    marchewy = 0;
                    score++;
                    czyTrzesienie = true;
                    ticks = ticks2;
                }
                if (started && marchewy < 1) {
                    marchewy += 0.003f;
                } else if (started) {
                    marchewy = 0;
                }
                if (run) {
                    if (direction && gruniox <= 1) {
                        gruniox += 0.004f;
                    } else if (gruniox >= 0) {
                        gruniox -= 0.004f;
                    }
                }

            }
            renderer.repaint();
        }
    }
/*
    public void jump()
    {
        if(gameOver)
        {
            bird = new Rectangle(WIDTH/2 -10, HEIGHT/2 -10, 20,20);
            columns.clear();

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);
            gameOver = false;
        }
        else if (!gameOver)
        {
            if(yMotion > 0)
            {
                yMotion = 0;
            }
            yMotion -= 10;
        }
        if(!started)
        {
            started = true;
        }
    }

 */
/*
    @Override
    public void actionPerformed(ActionEvent e) {
        ticks++;
        if (started && marchewy < 1){
            marchewy += 0.005f;
        } else if (started) {
            marchewy = 0;
            score++;
        }
        if (run){
            if(direction && gruniox <= 1 ){
                gruniox += 0.005f;
            }else if (gruniox >= 0){
                gruniox -= 0.005f;
            }
        }
        renderer.repaint();
    }
*/
/*
    public void addColumn(boolean start)
    {
        int space = 500;
        int width = 100;
        int height = 50 + rand.nextInt(300);

        if (start)
        {
            columns.add(new Rectangle(WIDTH + width + columns.size()*300, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(WIDTH + width + (columns.size()-1)*300, 0, width, HEIGHT - space));
        }
        else
        {
            columns.add(new Rectangle(columns.get(columns.size() -1).x + 600, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() -1).x , 0, width, HEIGHT - space));
        }
}
 */
/*
    public void paintColumn(Graphics g, Rectangle column)
    {
        g.setColor(Color.green.darker());
        g.fillRect(column.x, column.y, column.width, column.height);
    }
*/
    public void paintCarrot(Graphics g, float x, float y)
    {
        int x1 = Math.round(x*w - s*0.04f/2);
        int y1 = Math.round(y*h - s*0.11f);
        g.setColor(Color.orange);
        int tabX [ ] = { x1 + 0 , x1 + Math.round(s*0.04f) , x1 + Math.round(s*0.03f) , x1 + Math.round(s*0.01f) , x1 + 0 };
        int tabY [ ] = { y1 + Math.round(s*0.05f) , y1 + Math.round(s*0.05f) , y1 + Math.round(s*0.1f) , y1 + Math.round(s*0.1f) , y1 + Math.round(s*0.05f) };
        int n = tabX.length;
        g.fillPolygon( tabX, tabY, n-1 );
        g.fillOval(  x1 + 0 , y1 + Math.round(s*0.03f), Math.round(s*0.04f), Math.round(s*0.04f) );
        g.fillOval(  x1 + Math.round(s*0.01f) , y1 + Math.round(s*0.09f), Math.round(s*0.02f), Math.round(s*0.02f) );
        g.setColor(Color.orange.brighter());
        int tabX1 [ ] = { x1 + Math.round(s*0.02f) , x1 + Math.round(s*0.03f) , x1 + Math.round(s*0.02f) , x1 + Math.round(s*0.02f) };
        int tabY1 [ ] = { y1 + Math.round(s*0.055f) , y1 + Math.round(s*0.055f) , y1 + Math.round(s*0.095f) , y1 + Math.round(s*0.055f) };
        int n1 = tabX1.length;
        g.fillPolygon( tabX1, tabY1, n1-1 );
        g.fillOval(  x1 + Math.round(s*0.02f) , y1 + Math.round(s*0.05f), Math.round(s*0.01f), Math.round(s*0.01f) );
        g.setColor(Color.green);
        int tabX2 [ ] = { x1 + Math.round(s*0.02f) , x1 + Math.round(s*0.005f) , x1 + Math.round(s*0.015f) , x1 + Math.round(s*0.02f) , x1 + Math.round(s*0.025f) , x1 + Math.round(s*0.035f) , x1 + Math.round(s*0.02f) };
        int tabY2 [ ] = { y1 + Math.round(s*0.03f) , y1 + Math.round(s*0.005f) , y1 + Math.round(s*0.01f) , y1 + 0 , y1 + Math.round(s*0.01f) , y1 + Math.round(s*0.005f) , y1 + Math.round(s*0.03f)  };
        int n2 = tabX2.length;
        g.fillPolygon( tabX2, tabY2, n2-1 );
    }

    public boolean collision()
    {
        Rectangle r1 = new Rectangle(Math.round(gruniox*w - s * 0.07f), Math.round(h * 0.74f), Math.round(s * 0.14f), Math.round(s * 0.08f));
        Rectangle r2 = new Rectangle(Math.round(w * 0.5f - s*0.04f/2), Math.round(h * marchewy - s*0.11f), Math.round(s*0.04f), Math.round(s*0.11f));
        if(r1.intersects(r2)){
            return true;
        }else{
            return false;
        }
    }

    public void trzesienie(Graphics g){
        if (ticks-ticks2 % 30 < 5){
            g.translate(0, Math.round(-h * 0.01f));
        }else if (ticks-ticks2 % 30 < 12){
            g.translate(0, Math.round(h * 0.01f));
        }else if(ticks-ticks2 % 30 < 20) {
            g.translate(0, Math.round(-h * 0.01f));
        }else if(ticks-ticks2 % 30 < 29) {
            g.translate(0, Math.round(h * 0.01f));
        }else{
            czyTrzesienie = false;
        }
    }

    public void paintGrunio(Graphics g, float x, float y)
    {
        int x1 = Math.round(x*w - s * 0.07f);
        int y1 = Math.round(y*h - s * 0.08f);
        Image grunio;
        if (run) {
            if (ticks % 50 < 25) {
                if (direction) {
                    if (czyGrunio) {
                        grunio = Toolkit.getDefaultToolkit().getImage("img/grunio_run1_r.png");    //Grunio
                    }else{
                        grunio = Toolkit.getDefaultToolkit().getImage("img/dida_run1_r.png");    //Grunio
                    }
                } else {
                    if(czyGrunio) {
                        grunio = Toolkit.getDefaultToolkit().getImage("img/grunio_run1_l.png");    //Grunio
                    }else{
                        grunio = Toolkit.getDefaultToolkit().getImage("img/dida_run1_l.png");    //Grunio
                    }
                }
            } else {
                if (direction) {
                    if(czyGrunio){
                        grunio = Toolkit.getDefaultToolkit().getImage("img/grunio_run2_r.png");    //Grunio
                    }else{
                        grunio = Toolkit.getDefaultToolkit().getImage("img/dida_run2_r.png");    //Grunio
                    }
                } else {
                    if(czyGrunio) {
                        grunio = Toolkit.getDefaultToolkit().getImage("img/grunio_run2_l.png");    //Grunio
                    }else{
                        grunio = Toolkit.getDefaultToolkit().getImage("img/dida_run2_l.png");    //Grunio
                    }
                }
            }
        }else{
            if (direction) {
                if(czyGrunio){
                    grunio = Toolkit.getDefaultToolkit().getImage("img/grunio_freeze_r.png");    //Grunio
                }else{
                    grunio = Toolkit.getDefaultToolkit().getImage("img/dida_freeze_r.png");    //Grunio
                }
            } else {
                if(czyGrunio) {
                    grunio = Toolkit.getDefaultToolkit().getImage("img/grunio_freeze_l.png");    //Grunio
                }else{
                    grunio = Toolkit.getDefaultToolkit().getImage("img/dida_freeze_l.png");    //Grunio
                }
            }
        }
        g.drawImage(grunio, x1, y1, Math.round(s*0.14f), Math.round(s*0.08f), null);

    }

    public void repaint(Graphics g)
    {
        if (pause) {
            g.drawImage(pauseBackground, 0, 0, w, h, null);
            g.drawImage(exit, Math.round((w-s * 0.7f)/2),  Math.round(h*0.75f), Math.round(s * 0.7f), Math.round(h * 0.2f), null);
            //g.setColor(Color.red);
            //g.fillRect( Math.round((w-s * 0.7f)/2),  Math.round(h/2), Math.round(s * 0.7f), Math.round(h * 0.15f));
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, Math.round(s * 0.14f)));
            //g.drawString("WYJŚCIE", Math.round((w-s * 0.7f)/2 + s * 0.03f), Math.round(h/2 + s * 0.14f));
            g.drawString("PAUZA", Math.round((w-s * 0.5f)/2), Math.round(s * 0.14f));
        }else if (!started){
            g.drawImage(startBackground, 0, 0, w, h, null);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, 100));
            g.drawString("Click to start!", 75, HEIGHT / 2 - 50);
        }
        else{
            g.drawImage(background, 0, 0, w, h, null);
            if (czyTrzesienie) trzesienie(g);
            g.drawImage(background, 0, 0, w, h, null);
            float marchewx = 0.5f;
            paintCarrot(g, marchewx, marchewy);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, 100));

            if (gameOver) {
                g.drawString("Game over!", 100, HEIGHT / 2 - 50);
            }
            if (!gameOver && started) {
                g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
            }

            paintGrunio(g, gruniox, 0.82f);
        }

}

    public static void main(String[] args)
    {
        gruniozerca = new Gruniozerca();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (pause){
            if ( e.getX() < Math.round((w-s * 0.7f)/2) + Math.round(s * 0.7f) && e.getX() >  Math.round((w-s * 0.7f)/2) && e.getY() < Math.round(h*0.75f) + Math.round(s * 0.2f) && e.getY() > Math.round(h*0.75f) ){
                System.exit(0);
            }
        }
        gameOver = false;
        started = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT && !pause){
            run = true;
            direction = true;
            prawy = true;
        }else if(e.getKeyCode() == KeyEvent.VK_LEFT && !pause) {
            run = true;
            direction = false;
            lewy = true;
        }
        if (lewy && prawy){
            run = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            if(!czySpacja){
                czyGrunio = !czyGrunio;
                czySpacja = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            run = false;
            prawy = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            run = false;
            lewy = false;
        }
        if(prawy && !lewy){
            run = true;
            direction = true;
        }else if(lewy && !prawy){
            run = true;
            direction = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            //jump();
            czySpacja = false;

        }

        if(e.getKeyCode() == KeyEvent.VK_ESCAPE && started)
        {
            pause = !pause;
            //if (pause) timer.stop();
            //else timer.start();
            renderer.repaint();
        }
    }
}