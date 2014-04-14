import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.image.BufferStrategy;
import java.awt.Canvas;
import javax.swing.JFrame;

/**
 * // -------------------------------------------------------------------------
/**
 *  Snake.
 *
 *  @author L2T5
 *  @version 04.07.2014
 */
public class Game
    extends Canvas implements Runnable
{
    private final int        WINDOWX         = 800, WINDOWY = 600;
    private BufferStrategy   strategy;                             // used for
                                                        // double buffering
    private boolean          isRunning       = true;

    private int framesBetweenFood = 30;
    private int frameCount = framesBetweenFood;

    //1 = up, 2 = right, 3 = down, 4 = left
    private int              lastKeyPressed  = 1;     // start out moving up

    // set up the underlying matrix
    int                      squareSize      = 20;
    int[][]                  logicMatrix     =
                                                 new int[WINDOWX / squareSize][WINDOWY
                                                     / squareSize];
    // notes about matrix, 0 = nothing. 1 = food, 2 = snake part


    private Snake snake;//the snake

    /**
     * Set up the canvas, panel, and the frame.
     */
    public Game()
    {
        JFrame frame = new JFrame("Snake yay");
        // set resolution
        JPanel panel = (JPanel)frame.getContentPane();
        panel.setPreferredSize(new Dimension(WINDOWX, WINDOWY));
        panel.setLayout(null);

        //TODO: get rid of tiny border on panel/frame

        // set up canvas
        this.setBounds(0, 0, WINDOWX, WINDOWY);
        panel.add(this);

        // we'll repaint manually, so don't let canvas do it automatically
        this.setIgnoreRepaint(true);

        // set up frame/set visible
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // instantiate key listener
        this.addKeyListener(new KeyInputHandler());
        this.requestFocus();// otherwise keys may input to frame not canvas

        // create double buffer strategy
        this.createBufferStrategy(2);
        strategy = this.getBufferStrategy();

        snake = new Snake(logicMatrix);
    }


    /**
     * Starts a new game. Should clear any existing data, and reset from
     * nothing.
     */
    private void restartGame()
    {
        // TODO: clear any existing data (snake-list, position, spawned blocks)
        lastKeyPressed = 1;
        logicMatrix = new int[WINDOWX/squareSize][WINDOWY/squareSize];
        snake = new Snake(logicMatrix);
    }


    private void doDraw()
    {
        Graphics2D graphics = (Graphics2D)strategy.getDrawGraphics();

        // blank out canvas
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, WINDOWX, WINDOWY);

        // draw grid
        graphics.setColor(Color.BLACK);
        for (int x = 0; x < WINDOWX; x += squareSize)
        {
            graphics.drawLine(x, 0, x, WINDOWY);
        }
        for (int y = 0; y < WINDOWY; y += squareSize)
        {
            graphics.drawLine(0, y, WINDOWX, y);
        }

        for(int i = 0; i < logicMatrix.length; i++) {
            for(int j = 0; j < logicMatrix[i].length; j++) {
                if(logicMatrix[i][j] == 1) {
                    graphics.setColor(Color.BLUE);
                    graphics.fillOval(i*squareSize, j*squareSize, squareSize, squareSize);
                }
                else if(logicMatrix[i][j] == 2) {
                    graphics.setColor(Color.GREEN.darker());
                    graphics.fillOval(i*squareSize, j*squareSize, squareSize, squareSize);
                }
            }
        }

        // flip the buffer
        graphics.dispose();
        strategy.show();
    }


    private void updateLogic()
    {

        if(frameCount <= 0) {
            spawnFood();
            frameCount = framesBetweenFood;
        }
        snake.move(lastKeyPressed);
        // TODO: snake logic, collisions, victory/loss conditions
    }

    /**
     * Game loop. Maintains speed, calls logic and drawing updates.
     */
    public void run()
    {
        long lastLoopTime = System.currentTimeMillis();
        long desiredTime = 100;// move every half second to start.
        long deltaTime = 0;
        int sleepTime = 0;
        while (isRunning)
        {
            // time that last loop took
            deltaTime = System.currentTimeMillis() - lastLoopTime;
            // only move once every desiredTime amount of milliseconds
            sleepTime = (int)(desiredTime - deltaTime);
            if (sleepTime > 0)
            {
                try
                {
                    Thread.sleep(sleepTime);
                }
                catch (Exception e)
                {
                    // this probably should never fail...
                }
            }

            frameCount--;
            updateLogic();
            doDraw();
            //System.out.println(frameCount);
            lastLoopTime = System.currentTimeMillis();

        }
    }

    private void spawnFood() {
        int i = (int)(Math.random()*logicMatrix.length);
        int j = (int)(Math.random()*logicMatrix[i].length);
        while(logicMatrix[i][j] != 0) {
            i = (int)(Math.random()*logicMatrix.length);
            j = (int)(Math.random()*logicMatrix[i].length);
        }
        logicMatrix[i][j] = 1;
    }


    /**
     * Handles keyboard input. Should only maintain the last key pressed.
     */
    private class KeyInputHandler
        implements KeyListener
    {

        /**
         * The only method we care about. We want the last key pressed, since
         * the snake always moves in the direction of the last key pressed even
         * if it's let go.
         */
        @Override
        public void keyPressed(KeyEvent e)
        {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    lastKeyPressed = 1;
                    break;
                case KeyEvent.VK_RIGHT:
                    lastKeyPressed = 2;
                    break;
                case KeyEvent.VK_DOWN:
                    lastKeyPressed = 3;
                    break;
                case KeyEvent.VK_LEFT:
                    lastKeyPressed = 4;
                    break;
                default:
                    // do nothing (ie, if g is pressed, leave last key pressed)
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e)
        {
            //we don't care
        }

        @Override
        public void keyTyped(KeyEvent e)
        {
            //we don't care
        }
    }

    /**
     * Start the game.
     *
     * @param args The arguments passed to the execution call.
     */
    public static void main(String[] args) {
        Game game = new Game();
        new Thread(game).start();
    }
}
