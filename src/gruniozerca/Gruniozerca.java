package gruniozerca;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.File;
import java.io.*;
import java.util.Timer;


public class Gruniozerca implements MouseListener, KeyListener
{
    static Gruniozerca gruniozerca;
    private Renderer renderer;
    private int ticks, score = 0;
    private int ticks2 = -1;
    private boolean gameOver, started;
    private int w = 800;
    private int h = 600;
    private int s = 600;
    private float grunioSpeed = 0.008f;     //grunioSpeed dodawane do gruniox w czasie każdego tiknięcia zegara mówi o szybkości przemieszczania Grunia
    private float carroty = 0;
    private float gruniox = 0.5f;           //początkowe położenie Grunia
    private boolean pause = false;
    private boolean run = false;
    private boolean right = false;
    private boolean left = false;
    private boolean isGrunioBlack = true;
    private boolean isQuake = false;
    private boolean isRoundEnd = false;
    private boolean isDead = false;
    private boolean isRepeat = false;
    private int level = 1;                  //Początkowy poziom
    private int lives = 3;

    private Image startBackground;
    private Image pauseBackground;
    private Image exit;
    private Image next;
    private Image heartRed;
    private Image heartGrey;
    private Image hospital;
    private Image logo;
    private Image holiday;
    private Image repeat;
    private boolean direction = true;
    private boolean czySpacja = false;

    private Cursor blankCursor;
    private JFrame jframe;

    private randomLevel newLevel;
    private float[][] CarrotsCoordinates;
    private boolean[] isColor;
    private boolean[] isAte;
    private int numberOfCarrots;
    private Queue<Float> historyGrunioX;        //kolejki potrzebne do odtwarzania rozgrywki
    private Queue<Float> historyCarrotY;
    private Queue<Boolean> historyDirection;
    private Queue<Boolean> historyRun;
    private Queue<Boolean> historyColor;

    private Gruniozerca()
    {
        final int WIDTH = 800, HEIGHT = 639;
        jframe = new JFrame();

        renderer = new Renderer();

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

        try{
            String gongFile = "aud/theme.wav";      // audio które gra w tle (zapętlone)
            File musicPath = new File(gongFile);
            if(musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }catch (FileNotFoundException ex)
        {
            System.out.println("Audio Error");
        }catch (IOException e) {

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        startBackground = new ImageIcon("img/start.jpg").getImage();
        pauseBackground = new ImageIcon("img/pause.jpg").getImage();
        exit = new ImageIcon("img/exit.png").getImage();
        next = new ImageIcon("img/next.png").getImage();
        heartRed = new ImageIcon("img/heartRed.png").getImage();
        heartGrey = new ImageIcon("img/heartGrey.png").getImage();
        hospital = new ImageIcon("img/hospital.jpg").getImage();
        logo = new ImageIcon("img/logo.png").getImage();
        holiday = new ImageIcon("img/holiday.jpg").getImage();
        repeat = new ImageIcon("img/replay.jpg").getImage();

        historyGrunioX = new LinkedList<>();    // kolejka do zapisywania pozycji x Grunia
        historyColor = new LinkedList<>();      // kolejka -- kolorów Grunia
        historyDirection = new LinkedList<>();  // kolejka -- kierunku Grunia
        historyRun = new LinkedList<>();        // kolejka -- ruchu Grunia
        historyCarrotY = new LinkedList<>();    // kolejka -- marchewek Y

        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");    // stworzenie niewidzialnego kursora myszy
        jframe.getContentPane().setCursor(blankCursor);

        newLevel = new randomLevel(level, grunioSpeed, 0.2f);         //losuje nowy poziom o nazwie "newLevel"

        CarrotsCoordinates = newLevel.getCarrotsCoordinates();                //tak w ogóle to te trzy linijki powynny być wywoływane na początku nowego poziomu
        isColor = newLevel.getCarrotColor();

        numberOfCarrots = newLevel.getCarrotsNumber();
        isAte = new boolean[numberOfCarrots];                      // tablica zjedzonych lub opuszczonych marchewek ????
        for(int i = 0; i < numberOfCarrots; i++) isAte[i] = false; // na poczatku zadna nie jest zjedzona wiec wszystkie na false

        int timerPeriod = 20;                    //okres timera (w milisekundach)
        Timer clock = new Timer();               //nowy timer
        clock.scheduleAtFixedRate(new Exercise(),0,timerPeriod);
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
        }
    }

    class Exercise extends TimerTask{

        public void run() {
            if (started && !pause && !isRoundEnd && !isDead) {
                ticks++;
                historyGrunioX.add(gruniox);        // dopisywanie do kolejek danych
                historyDirection.add(direction);    // potrzebnych do mozliwosci
                historyRun.add(run);                // odtworzenia poprzedniego etapu gry
                historyColor.add(isGrunioBlack);


                // dzwiek chrupania marchewki
                if ((ticks - ticks2) == 1) {
                    Random generator = new Random();
                    char audioFileNumber = (char)(49+Math.round(generator.nextDouble()*2)); //losowanie char "1", "2" lub "3"
                    try{
                        String gongFile = "aud/marchew" + audioFileNumber + ".wav";
                        InputStream in = new FileInputStream(gongFile);
                        AudioStream audioStream = new AudioStream(in);
                        AudioPlayer.player.start(audioStream);
                    }catch (FileNotFoundException ex)
                    {
                        System.out.println("Audio Error");
                    }catch (IOException e) {

                    }
                }
                if (started && carroty < 1) {
                    carroty += 0.003f;
                } else if (started) {
                    carroty = 0;
                }
                if (run) {
                    if (direction && gruniox <= 1) {
                        gruniox += grunioSpeed;
                    } else if (gruniox >= 0) {
                        gruniox -= grunioSpeed;
                    }
                }

            }
            renderer.repaint();
        }
    }

    // ============== STWORZENIE SZABLONU MARCHEWKI + RYSOWANIE ===========================

    private void paintCarrot(Graphics g, float x, float y, boolean isColor)
    {
        int x1 = Math.round(x*w - s*0.04f/2);
        int y1 = Math.round(y*h - s*0.11f);
        Color color1 = new Color(227, 164, 0);
        Color color2 = new Color(255, 231, 140, 255);
        if(isColor){
            color1 = new Color(255, 59, 0);
            color2 = new Color(255, 185, 137, 255);
        }
        g.setColor(color1);
        int tabX [ ] = { x1 , x1 + Math.round(s*0.04f) , x1 + Math.round(s*0.03f) , x1 + Math.round(s*0.01f) , x1};
        int tabY [ ] = { y1 + Math.round(s*0.05f) , y1 + Math.round(s*0.05f) , y1 + Math.round(s*0.1f) , y1 + Math.round(s*0.1f) , y1 + Math.round(s*0.05f) };
        int n = tabX.length;
        g.fillPolygon( tabX, tabY, n-1 );
        g.fillOval(  x1 , y1 + Math.round(s*0.03f), Math.round(s*0.04f), Math.round(s*0.04f) );
        g.fillOval(  x1 + Math.round(s*0.01f) , y1 + Math.round(s*0.09f), Math.round(s*0.02f), Math.round(s*0.02f) );
        g.setColor(color2);
        int tabX1 [ ] = { x1 + Math.round(s*0.02f) , x1 + Math.round(s*0.03f) , x1 + Math.round(s*0.02f) , x1 + Math.round(s*0.02f) };
        int tabY1 [ ] = { y1 + Math.round(s*0.055f) , y1 + Math.round(s*0.055f) , y1 + Math.round(s*0.095f) , y1 + Math.round(s*0.055f) };
        int n1 = tabX1.length;
        g.fillPolygon( tabX1, tabY1, n1-1 );
        g.fillOval(  x1 + Math.round(s*0.02f) , y1 + Math.round(s*0.05f), Math.round(s*0.01f), Math.round(s*0.01f) );
        g.setColor(Color.green);
        int tabX2 [ ] = { x1 + Math.round(s*0.02f) , x1 + Math.round(s*0.005f) , x1 + Math.round(s*0.015f) , x1 + Math.round(s*0.02f) , x1 + Math.round(s*0.025f) , x1 + Math.round(s*0.035f) , x1 + Math.round(s*0.02f) };
        int tabY2 [ ] = { y1 + Math.round(s*0.03f) , y1 + Math.round(s*0.005f) , y1 + Math.round(s*0.01f) , y1, y1 + Math.round(s*0.01f) , y1 + Math.round(s*0.005f) , y1 + Math.round(s*0.03f)  };
        int n2 = tabX2.length;
        g.fillPolygon( tabX2, tabY2, n2-1 );
    }

    // ================== WYKRYWANIE KOLIZJI DWOCH KLAS RECTANGLE (GRUNIA + MARCHEWEK) ===============================

    private boolean collision(float carrotX, float carrotY, boolean isColor) {
        Rectangle r1 = new Rectangle(Math.round(gruniox * w - s * 0.07f), Math.round(h * 0.74f), Math.round(s * 0.14f), Math.round(s * 0.08f));
        Rectangle r2 = new Rectangle(Math.round(w * carrotX - s * 0.04f / 2), Math.round(h * carrotY - s * 0.11f), Math.round(s * 0.04f), Math.round(s * 0.11f));
        if (r1.intersects(r2) && isColor == isGrunioBlack) {
            if(!isRepeat) score++;
            isQuake = true;
            ticks2 = ticks;
            return true;
        }else if(r1.intersects(r2) && isColor != isGrunioBlack) {
            try{
                String gongFile = "aud/lose.wav";
                InputStream in = new FileInputStream(gongFile);
                AudioStream audioStream = new AudioStream(in);
                AudioPlayer.player.start(audioStream);
            }catch (FileNotFoundException ex)
            {
                System.out.println("Audio Error");
            }catch (IOException e) {

            }
            lives--;
            if(lives <= 0) isDead = true;
            return true;
        }else{
            return false;
        }
    }

    // ==================== TRANSLACJA OBRAZU POWODUJACA TRZESIENIE =======================

    private void quake(Graphics g){
        if ((ticks-ticks2) % 15 < 3){
            g.translate(0, Math.round(-h * 0.01f));
        }else if ((ticks-ticks2) % 15 < 6){
            g.translate(0, Math.round(h * 0.01f));
        }else if((ticks-ticks2) % 15 < 10) {
            g.translate(0, Math.round(-h * 0.01f));
        }else if((ticks-ticks2) % 15 < 14) {
            g.translate(0, Math.round(h * 0.01f));
        }else if((ticks - ticks2) > 15){
            isQuake = false;
            ticks2 = 0;
        }
    }

    private void paintGrunio(Graphics g, float x, float y)
    {
        int x1 = Math.round(x*w - s * 0.07f);
        int y1 = Math.round(y*h - s * 0.08f);
        Image grunio;
        if (run) {
            if (ticks % 50 < 25) {
                if (isGrunioBlack) {
                    grunio = Toolkit.getDefaultToolkit().getImage("img/grunio_run1.png");    //Grunio
                }else{
                    grunio = Toolkit.getDefaultToolkit().getImage("img/dida_run1.png");    //Grunio
                }
            } else {
                if(isGrunioBlack){
                    grunio = Toolkit.getDefaultToolkit().getImage("img/grunio_run2.png");    //Grunio
                }else{
                    grunio = Toolkit.getDefaultToolkit().getImage("img/dida_run2.png");    //Grunio
                }
            }
        }else{
            if(isGrunioBlack){
                grunio = Toolkit.getDefaultToolkit().getImage("img/grunio_freeze.png");    //Grunio
            }else{
                grunio = Toolkit.getDefaultToolkit().getImage("img/dida_freeze.png");    //Grunio
            }
        }
        if(direction) {
            g.drawImage(grunio, x1, y1, Math.round(s * 0.14f), Math.round(s * 0.08f), null);
        }else{
            g.drawImage(grunio, x1 + Math.round(s * 0.14f), y1, -Math.round(s * 0.14f), Math.round(s * 0.08f), null);
        }
    }

    // =============== RYSOWANIE ŻYC (SERC) =======================

    private void paintLives(Graphics g)
    {
        if (lives >= 3){
            g.drawImage(heartRed, Math.round((w * 0.7f)),  Math.round(h*0.05f), Math.round(s * 0.1f), Math.round(h * 0.1f), null);
            g.drawImage(heartRed, Math.round((w * 0.8f)),  Math.round(h*0.05f), Math.round(s * 0.1f), Math.round(h * 0.1f), null);
            g.drawImage(heartRed, Math.round((w * 0.9f)),  Math.round(h*0.05f), Math.round(s * 0.1f), Math.round(h * 0.1f), null);
        }else if (lives == 2){
            g.drawImage(heartRed, Math.round((w * 0.7f)),  Math.round(h*0.05f), Math.round(s * 0.1f), Math.round(h * 0.1f), null);
            g.drawImage(heartRed, Math.round((w * 0.8f)),  Math.round(h*0.05f), Math.round(s * 0.1f), Math.round(h * 0.1f), null);
            g.drawImage(heartGrey, Math.round((w * 0.9f)),  Math.round(h*0.05f), Math.round(s * 0.1f), Math.round(h * 0.1f), null);
        }else if (lives == 1){
            g.drawImage(heartRed, Math.round((w * 0.7f)),  Math.round(h*0.05f), Math.round(s * 0.1f), Math.round(h * 0.1f), null);
            g.drawImage(heartGrey, Math.round((w * 0.8f)),  Math.round(h*0.05f), Math.round(s * 0.1f), Math.round(h * 0.1f), null);
            g.drawImage(heartGrey, Math.round((w * 0.9f)),  Math.round(h*0.05f), Math.round(s * 0.1f), Math.round(h * 0.1f), null);
        }
    }

    // ============== MALOWANIE OBRAZU =======================
    void repaint(Graphics g)
    {
        if (pause) {                    //Rysowane w czasie trwania pauzy
            jframe.getContentPane().setCursor(Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
            g.drawImage(pauseBackground, 0, 0, w, h, null);
            g.drawImage(exit, Math.round((w-s * 0.7f)/2),  Math.round(h*0.75f), Math.round(s * 0.7f), Math.round(h * 0.2f), null);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, Math.round(s * 0.14f)));
            g.drawString("PAUZA", Math.round((w-s * 0.5f)/2), Math.round(s * 0.14f));
        }else if (!started){            //Rysowane przed rozpoczęciem gry
            g.drawImage(startBackground, 0, 0, w, h, null);
            g.drawImage(logo, Math.round((w-s * 0.95f)/2),  Math.round(h*0.05f), Math.round(s * 0.95f), Math.round(h * 0.2f), null);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, 100));
            g.drawString("Kliknij aby rozpocząć!", Math.round((w-s * 1f)/2), Math.round(s * 0.9f));
        }else if (isDead){              //Eysowane po zakończeniu gry
            isRoundEnd = false;
            jframe.getContentPane().setCursor(Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
            g.drawImage(hospital, 0, 0, w, h, null);
            g.drawImage(exit, Math.round((w-s * 0.7f)/2),  Math.round(h*0.75f), Math.round(s * 0.7f), Math.round(h * 0.2f), null);
            g.drawImage(repeat, Math.round(w * 0.05f),  Math.round(h*0.05f), Math.round(s * 0.2f), Math.round(s * 0.2f), null);
            g.setColor(Color.black);
            g.setFont(new Font("Arial", 1, Math.round(s * 0.14f)));
            g.drawString("KONIEC", Math.round((w-s * 0.5f)/2), Math.round(s * 0.14f));
            g.drawString(String.format("WYNIK: %s", (score)), Math.round((w-s * 0.7f)/2), Math.round(s * 0.7f));
        }else if (isRoundEnd){          //Rysowane po zakończeniu poziomu
            jframe.getContentPane().setCursor(Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
            g.drawImage(holiday, 0, 0, w, h, null);
            g.drawImage(next, Math.round((w-s * 0.7f)/2),  Math.round(h*0.75f), Math.round(s * 0.7f), Math.round(h * 0.2f), null);
            g.drawImage(repeat, Math.round(w * 0.05f),  Math.round(h*0.05f), Math.round(s * 0.2f), Math.round(s * 0.2f), null);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, Math.round(s * 0.14f)));
            g.drawString("KONIEC", Math.round((w-s * 0.5f)/2), Math.round(s * 0.14f));
            g.drawString(String.format("POZIOMU %s", (level)), Math.round((w-s * 0.7f)/2), Math.round(s * 0.28f));
        }
        else{
            if(isRepeat){
                gruniox = historyGrunioX.remove();          // jesli jest wlaczony "odtworz ponownie" to przypisujemy Gruniowi
                direction = historyDirection.remove();      // dane z kolejki (jednoczesnie usuwajac już użyte) zeby powstalo
                run = historyRun.remove();                  // automatyczne odtworzenie przebiegu gry (etapu)
                isGrunioBlack = historyColor.remove();
            }
            jframe.getContentPane().setCursor(blankCursor); // podczas gry nie bedzie byc widoczny kursor myszy

            // dla odpowiedniego poziomu gry zmieniamy tło
            int backgroundNumber = level%6;
            Image background = new ImageIcon("img/background" + backgroundNumber + ".jpg").getImage();
            if(ticks < 10)
                background = new ImageIcon("img/background" + backgroundNumber + ".jpg").getImage();
            g.drawImage(background , 0, 0, w, h, null);
            if (isQuake) quake(g);
            g.drawImage(background, 0, 0, w, h, null);

            paintLives(g);
            float carrotSpeed = newLevel.getCarrotsSpeed();

            for (int i = 0; i < numberOfCarrots; i++){
               /* float newCarrotY;
                historyCarrotY.add((float) ticks * carrotSpeed - CarrotsCoordinates[1][i]);
                if(isRepeat)
                    newCarrotY = historyCarrotY.remove();
                else
                    newCarrotY = (float) ticks * carrotSpeed - CarrotsCoordinates[1][i];
                */
                if (((float) ticks * carrotSpeed - CarrotsCoordinates[1][i]) > 0 && !isAte[i])
                {
                    paintCarrot(g, CarrotsCoordinates[0][i], (float) ticks * carrotSpeed - CarrotsCoordinates[1][i], isColor[i]);
                    if(collision(CarrotsCoordinates[0][i], (float) ticks * carrotSpeed - CarrotsCoordinates[1][i], isColor[i]))
                    {                           // jesli nastapila kolizja Grunio + marchewki + odpowiedni kolor
                        isAte[i] = true;        // ustawiamy tę marchewke jako zjedzoną
                    }

                    if(((float) ticks * carrotSpeed - CarrotsCoordinates[1][i]) >= 1 && !isAte[i]){
                        try{
                            String gongFile = "aud/lose.wav";
                            InputStream in = new FileInputStream(gongFile);
                            AudioStream audioStream = new AudioStream(in);
                            AudioPlayer.player.start(audioStream);
                        }catch (FileNotFoundException ex)
                        {
                            System.out.println("Audio Error");
                        }catch (IOException e) {

                        }
                        isAte[i] = true;
                        lives--;
                        if(lives <= 0) isDead = true;
                    }

                }
                if(((float) ticks * carrotSpeed - newLevel.carrotY[numberOfCarrots-1]) > 1)
                {
                    isRoundEnd = true;
                }

            }

            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, Math.round(s * 0.14f)));

            if (gameOver) {
                g.drawString("Game over!", Math.round((w * 0.5f)/2), Math.round(s * 0.14f));
            }
            if (!gameOver && started && !isRepeat) {
                g.drawString(String.valueOf(score), Math.round(w * 0.1f), Math.round(s * 0.14f));
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
        if ( e.getX() < Math.round((w-s * 0.7f)/2) + Math.round(s * 0.7f) && e.getX() >  Math.round((w-s * 0.7f)/2) && e.getY() < Math.round(h*0.75f) + Math.round(s * 0.2f) && e.getY() > Math.round(h*0.75f) ){
            if (isRoundEnd){                //Wykonuje się przy kliknięciu przycisku rozpoczęcia kolejnego poziomu
                try{
                    String gongFile = "aud/click.wav";
                    InputStream in = new FileInputStream(gongFile);
                    AudioStream audioStream = new AudioStream(in);
                    AudioPlayer.player.start(audioStream);
                }catch (FileNotFoundException ex)
                {
                    System.out.println("Audio Error");
                }catch (IOException u) {

                }
                historyGrunioX = new LinkedList<>();
                historyColor = new LinkedList<>();
                historyDirection = new LinkedList<>();
                historyRun = new LinkedList<>();

                run = false;
                isDead = false;
                isRoundEnd = false;
                ticks = 0;
                ticks2 = -1;
                isQuake = false;
                lives = 3;
                gruniox = 0.5f;
                isRepeat = false;
                level++;
                newLevel = new randomLevel(level, grunioSpeed, 0.2f);         // losuje nowy poziom o nazwie "newLevel"

                CarrotsCoordinates = newLevel.getCarrotsCoordinates();
                isColor = newLevel.getCarrotColor();

                numberOfCarrots = newLevel.getCarrotsNumber();
                isAte = new boolean[numberOfCarrots];
                for(int j = 0; j < numberOfCarrots; j++) isAte[j] = false;
            }else if (pause || isDead) {
                System.exit(0);
            }
        }
        if ( e.getX() < Math.round(w * 0.05f) + Math.round(s * 0.2f) && e.getX() >  Math.round(w * 0.05f) && e.getY() < Math.round(h*0.05f) + Math.round(s * 0.2f) && e.getY() > Math.round(h*0.05f) ) {
            if(isRoundEnd || isDead) {      //Wykonuje się przy kliknięciu przycisku powtórzenia rozgrywki
                try{
                    String gongFile = "aud/click.wav";
                    InputStream in = new FileInputStream(gongFile);
                    AudioStream audioStream = new AudioStream(in);
                    AudioPlayer.player.start(audioStream);
                }catch (FileNotFoundException ex)
                {
                    System.out.println("Audio Error");
                }catch (IOException u) {

                }
                isDead = false;
                isRoundEnd = false;
                ticks = 0;
                ticks2 = -1;
                isQuake = false;
                lives = 3;
                gruniox = 0.5f;
                for (int j = 0; j < numberOfCarrots; j++) isAte[j] = false;
                isRepeat = true;
            }
        }
        if(!started){                     //Wykonuje się przy wcisnięciu myszki na ekranie powitalnym
            try{
                String gongFile = "aud/click.wav";
                InputStream in = new FileInputStream(gongFile);
                AudioStream audioStream = new AudioStream(in);
                AudioPlayer.player.start(audioStream);
            }catch (FileNotFoundException ex)
            {
                System.out.println("Audio Error");
            }catch (IOException u) {

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
    public void keyPressed(KeyEvent e) {                //Sterowanie gruniem i zmiana kolorów
        if(e.getKeyCode() == KeyEvent.VK_RIGHT && !pause){
            run = true;
            direction = true;
            right = true;
        }else if(e.getKeyCode() == KeyEvent.VK_LEFT && !pause) {
            run = true;
            direction = false;
            left = true;
        }
        if (left && right){
            run = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            if(!czySpacja){
                isGrunioBlack = !isGrunioBlack;
                czySpacja = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {               //Zatrzaski
        if(!isRepeat) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                run = false;
                right = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                run = false;
                left = false;
            }
            if (right && !left) {
                run = true;
                direction = true;
            } else if (left && !right) {
                run = true;
                direction = false;
            }

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                czySpacja = false;

            }
        }

        if(e.getKeyCode() == KeyEvent.VK_ESCAPE && started)
        {
            pause = !pause;
            renderer.repaint();
        }
    }
}