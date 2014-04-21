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
 * /** Snake.
 *
 * @author L2T5
 * @version 04.07.2014
 */
public class Game
    extends Canvas
    implements Runnable
{
    private final int      WINDOWX           = 800, WINDOWY = 600;
    private BufferStrategy strategy;                               // used for
    // double buffering
    private boolean        isRunning         = true;

    private int            framesBetweenFood = 30;
    private int            frameCount        = framesBetweenFood;

    // 1 = up, 2 = right, 3 = down, 4 = left
    private int            lastKeyPressed    = 1;                  // start out
// moving up
    private int            lastKeyPressed2   = 3;                  // start out
// moving down

    // set up the underlying matrix
    int                    squareSize        = 20;
    int[][]                logicMatrix       =
                                                 new int[WINDOWX / squareSize][WINDOWY
                                                     / squareSize];
    // notes about matrix, 0 = nothing. 1 = food, 2 = snake part

    private Snake          snake;                                  // the snake
    private Snake          snake2;                                 // player
// 2's snake

    private int            gameMode          = 3;                  // 1 is
// normal, 2 is extreme, 3 is 2-player.


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

        // TODO: get rid of tiny border on panel/frame

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

        if (gameMode == 1 || gameMode == 2)
        {
            snake = new Snake(logicMatrix);
        }
        else if (gameMode == 3)
        {
            snake =
                new Snake(
                    logicMatrix,
                    logicMatrix.length * 3 / 4,
                    logicMatrix[0].length / 2);
            snake2 =
                new Snake(
                    logicMatrix,
                    logicMatrix.length / 4,
                    logicMatrix[0].length / 2);
        }
    }


    /**
     * Starts a new game. Should clear any existing data, and reset from
     * nothing.
     */
    private void restartGame()
    {
        // TODO: clear any existing data (snake-list, position, spawned blocks)
        lastKeyPressed = 1;
        logicMatrix = new int[WINDOWX / squareSize][WINDOWY / squareSize];
        snake = new Snake(logicMatrix);
    }


    private void doDraw()
    {
        Graphics2D graphics = (Graphics2D)strategy.getDrawGraphics();

        // blank out canvas
        graphics.setColor(Color.WHITE);
        if (gameMode == 2)
        {
            int r = (int)(Math.random() * 256);
            int g = (int)(Math.random() * 256);
            int b = (int)(Math.random() * 256);
            graphics.setColor(new Color(r, g, b));
        }
        graphics.fillRect(0, 0, WINDOWX, WINDOWY);

        // draw grid
        if (gameMode == 1)
        {
            graphics.setColor(Color.BLACK);
            for (int x = 0; x < WINDOWX; x += squareSize)
            {
                graphics.drawLine(x, 0, x, WINDOWY);
            }
            for (int y = 0; y < WINDOWY; y += squareSize)
            {
                graphics.drawLine(0, y, WINDOWX, y);
            }
        }
        for (int i = 0; i < logicMatrix.length; i++)
        {
            for (int j = 0; j < logicMatrix[i].length; j++)
            {
                if (logicMatrix[i][j] == 1)
                {
                    graphics.setColor(Color.BLUE);
                    graphics.fillOval(
                        i * squareSize,
                        j * squareSize,
                        squareSize,
                        squareSize);
                }
                else if (logicMatrix[i][j] == 2)
                {
                    graphics.setColor(Color.GREEN.darker());
                    graphics.fillOval(
                        i * squareSize,
                        j * squareSize,
                        squareSize,
                        squareSize);
                }
            }
        }

        // flip the buffer
        graphics.dispose();
        strategy.show();
    }


    private int updateLogic()
    {

        /*
         * if(frameCount <= 0) { spawnFood(); frameCount = framesBetweenFood; }
         */
        int status = snake.move(lastKeyPressed);
        if(gameMode == 3)
            status |= snake2.move(lastKeyPressed2);
        if (status == 1)
        {
            spawnFood();
            return 1;
        }
        return status;
        // TODO: snake logic, collisions, victory/loss conditions
    }


    /**
     * Game loop. Maintains speed, calls logic and drawing updates.
     */
    public void run()
    {
        long lastLoopTime = System.currentTimeMillis();
        // desired time between moves depends on game mode
        long desiredTime = gameMode==1||gameMode==3 ? 200 : 30;
        long deltaTime = 0;
        int sleepTime = 0;
        boolean firstTime = true;
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
            if (firstTime)
            {
                spawnFood();
                firstTime = false;
            }

            // speed up when eating
            if (updateLogic() == 1 && gameMode == 1 && desiredTime > 40)
            {
                desiredTime -= 1;
            }
            doDraw();
            // System.out.println(frameCount);
            lastLoopTime = System.currentTimeMillis();

        }
    }


    private void spawnFood()
    {
        int i = (int)(Math.random() * logicMatrix.length);
        int j = (int)(Math.random() * logicMatrix[i].length);
        while (logicMatrix[i][j] != 0)
        {
            i = (int)(Math.random() * logicMatrix.length);
            j = (int)(Math.random() * logicMatrix[i].length);
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
            switch (e.getKeyCode())
            {
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
                case KeyEvent.VK_W:
                    lastKeyPressed2 = 1;
                    break;
                case KeyEvent.VK_D:
                    lastKeyPressed2 = 2;
                    break;
                case KeyEvent.VK_S:
                    lastKeyPressed2 = 3;
                    break;
                case KeyEvent.VK_A:
                    lastKeyPressed2 = 4;
                    break;
                default:
                    // do nothing (ie, if g is pressed, leave last key pressed)
                    break;
            }
        }


        @Override
        public void keyReleased(KeyEvent e)
        {
            // we don't care
        }


        @Override
        public void keyTyped(KeyEvent e)
        {
            // we don't care
        }
    }


    /**
     * Start the game.
     *
     * @param args
     *            The arguments passed to the execution call.
     */
    public static void main(String[] args)
    {
        Game game = new Game();
        new Thread(game).start();
    }
}
