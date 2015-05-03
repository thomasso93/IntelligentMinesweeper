package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends JFrame implements ActionListener {

    private int deley = 0;
    private GamePanel gamePanel = new GamePanel();
    private MyJButton startButton = new MyJButton(new ImageIcon("res/start.png"), new ImageIcon("res/start_hover.png"), new ImageIcon("res/start_press.png"));
    private MyJButton quitButton = new MyJButton(new ImageIcon("res/wyjdz.png"), new ImageIcon("res/wyjdz_hover.png"), new ImageIcon("res/wyjdz_press.png"));
    private MyJButton pauseButton = new MyJButton(new ImageIcon("res/pauza.png"), new ImageIcon("res/pauza_hover.png"), new ImageIcon("res/pauza_press.png"));
    private boolean running = false;
    private boolean paused = false;
    private int fps = 60;

    public Game() {
        super("Minesweeper");
        JPanel p = new JPanel();
        p.add(startButton);
        p.add(pauseButton);
        p.add(quitButton);
        this.add(p, BorderLayout.WEST);
        this.add(gamePanel, BorderLayout.EAST);
        setVisible(true);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        startButton.addActionListener(this);
        quitButton.addActionListener(this);
        pauseButton.addActionListener(this);
    }

    public static void main(String[] args) {
        new Game();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
       if (s == startButton) {
            running = !running;

            if (running) {
                runGameLoop();
            }


        } else if (s == pauseButton) {
            paused = !paused;
            if (paused) {
                pauseButton.setIcon(new ImageIcon("res/wznow.png"));
                pauseButton.setPressedIcon(new ImageIcon("res/wznow_press.png"));
                pauseButton.setRolloverIcon(new ImageIcon("res/wznow_hover.png"));
            } else {
                pauseButton.setIcon(new ImageIcon("res/pauza.png"));
                pauseButton.setPressedIcon(new ImageIcon("res/pauza_press.png"));
                pauseButton.setRolloverIcon(new ImageIcon("res/pauza_hover.png"));
                
            }
        } else if (s == quitButton) {
            System.exit(0);
        }
    }

    public void runGameLoop() {
        Thread loop = new Thread() {
            @Override
            public void run() {
                gameLoop();
            }
        };
        loop.start();
    }

    //Only run this in another Thread!
    private void gameLoop() {
        //This value would probably be stored elsewhere.
        final double GAME_HERTZ = 30.0;
        //Calculate how many ns each frame should take for our target game hertz.
        final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
        //At the very most we will update the game this many times before a new render.
        //If you're worried about visual hitches more than perfect timing, set this to 1.
        final int MAX_UPDATES_BEFORE_RENDER = 5;
        //We will need the last update time.
        double lastUpdateTime = System.nanoTime();
        //Store the last time we rendered.
        double lastRenderTime = System.nanoTime();

        //If we are able to get as high as this FPS, don't render again.
        final double TARGET_FPS = 60;
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

        //Simple way of finding FPS.
        int lastSecondTime = (int) (lastUpdateTime / 1000000000);

        while (running) {
            double now = System.nanoTime();
            int updateCount = 0;

            if (!paused) {
                if (gamePanel.gameOver) {
                    gamePanel.update();
                    paused = true;
                    pauseButton.setEnabled(false);
                }
                //Do as many game updates as we need to, potentially playing catchup.
                while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
                    gamePanel.update();
                    lastUpdateTime += TIME_BETWEEN_UPDATES;
                    updateCount++;
                }

                //If for some reason an update takes forever, we don't want to do an insane number of catchups.
                //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
                if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
                    lastUpdateTime = now - TIME_BETWEEN_UPDATES;
                }

                //Render. To do so, we need to calculate interpolation for a smooth render.
                float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES));
                gamePanel.drawGame(interpolation);
                lastRenderTime = now;

                //Update the frames we got.
                int thisSecond = (int) (lastUpdateTime / 1000000000);
                if (thisSecond > lastSecondTime) {
                    gamePanel.SetFPS(gamePanel.GetFrameCount());
                    gamePanel.SetFrameCount(0);
                    lastSecondTime = thisSecond;
                }

                //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
                while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
                    Thread.yield();

                    //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
                    //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
                    //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                    }
                    now = System.nanoTime();
                }
            }
        }
    }
}