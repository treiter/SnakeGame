import javax.swing.JOptionPane;
import java.awt.PopupMenu;
import java.awt.Frame;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JMenuBar;
import java.awt.BorderLayout;
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
    private BufferStrategy strategy;                                // used for
    // double buffering
    private boolean        isRunning         = true;

    private int            framesBetweenFood = 30;
    private int            frameCount        = framesBetweenFood;

    private Color          snakeColor        = Color.GREEN.darker();
    private Color          backGroundColor   = Color.white;

    // 1 = up, 2 = right, 3 = down, 4 = left
    private int            lastKeyPressed    = 1;                   // start
// out
// moving up
    private int            lastKeyPressed2   = 3;                   // start
// out
// moving down

    // set up the underlying matrix
    int                    squareSize        = 20;
    int[][]                logicMatrix       =
                                                 new int[WINDOWX / squareSize][WINDOWY
                                                     / squareSize];
    // notes about matrix, 0 = nothing. 1 = food, 2 = snake part

    private Snake          snake;                                   // the
// snake
    private Snake          snake2;                                  // player
// 2's snake

    private int            gameMode          = 1;                   // 1 is
// normal, 2 is extreme, 3 is 2-player.

    private JFrame         frame;


    /**
     * Set up the canvas, panel, and the frame.
     */
    public Game()
    {
        frame = new JFrame("Snake yay");
        // set resolution
        JPanel panel = (JPanel)frame.getContentPane();
        panel.setPreferredSize(new Dimension(WINDOWX, WINDOWY));
        panel.setLayout(null);

        // TODO: get rid of tiny border on panel/frame

        // set up canvas
        this.setBounds(0, 20, WINDOWX, WINDOWY - 20);
        panel.add(this);

        // set up the menu
        PopUpMenu menu = new PopUpMenu(this);
        JMenuBar m = menu.createMenuBar();
        m.setBounds(0, 0, WINDOWX, 20);
        panel.add(m, BorderLayout.LINE_START);

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
        spawnFood();
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
        spawnFood();
        this.requestFocus();
        isRunning = true;
    }


    private void doDraw()
    {
        Graphics2D graphics = (Graphics2D)strategy.getDrawGraphics();

        // blank out canvas
        graphics.setColor(backGroundColor);
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
                    graphics.setColor(snakeColor);
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
        if (gameMode == 3)
            status |= snake2.move(lastKeyPressed2);
        if (status == 1)
        {
            spawnFood();
            return 1;
        }
        else if (status == 5)
        {
            isRunning = false;
            Object[] options = { "Play Again", "Okay", "Quit" };
            int n =
                JOptionPane.showOptionDialog(
                    frame,
                    "Game Over",
                    "",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);
            switch (n)
            {
                case 0:
                    restartGame();
                    break;
                case 1:
                    // do nothing
                    break;
                case 2:
                    System.exit(0);
                    break;
            }
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
        long desiredTime = gameMode == 1 || gameMode == 3 ? 200 : 30;
        long deltaTime = 0;
        int sleepTime = 0;
        this.requestFocus();
        while (true)
        {
            if (isRunning)
            {
                desiredTime = gameMode == 1 || gameMode == 3 ? 200 : 30;
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
                    if (lastKeyPressed != 3)
                        lastKeyPressed = 1;
                    break;
                case KeyEvent.VK_RIGHT:
                    if (lastKeyPressed != 4)
                        lastKeyPressed = 2;
                    break;
                case KeyEvent.VK_DOWN:
                    if (lastKeyPressed != 1)
                        lastKeyPressed = 3;
                    break;
                case KeyEvent.VK_LEFT:
                    if (lastKeyPressed != 2)
                        lastKeyPressed = 4;
                    break;
                case KeyEvent.VK_W:
                    if (lastKeyPressed2 != 3)
                        lastKeyPressed2 = 1;
                    break;
                case KeyEvent.VK_D:
                    if (lastKeyPressed2 != 4)
                        lastKeyPressed2 = 2;
                    break;
                case KeyEvent.VK_S:
                    if (lastKeyPressed2 != 1)
                        lastKeyPressed2 = 3;
                    break;
                case KeyEvent.VK_A:
                    if (lastKeyPressed2 != 2)
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


    class PopUpMenu
    {
        private Game game;


        public PopUpMenu(Game game)
        {
            super();
            this.game = game;
        }


        public JMenuBar createMenuBar()
        {
            JMenuBar menuBar = new JMenuBar();
            JMenu j = new JMenu("Game");
            JMenuItem i = new JMenuItem("Restart");
            i.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    restartGame();
                }
            });
            j.add(i);
            i = new JMenuItem("Pause");
            i.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (((JMenuItem)e.getSource()).getText().equals("Pause"))
                    {
                        isRunning = false;
                        ((JMenuItem)e.getSource()).setText("Resume");
                    }
                    else
                    {
                        ((JMenuItem)e.getSource()).setText("Pause");
                        isRunning = true;
                        game.requestFocus();
                        new Thread(game).start();
                        //run();

                    }
                }
            });
            j.add(i);
            i = new JMenuItem("Quit");
            i.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    frame.dispose();
                }
            });
            j.add(i);
            menuBar.add(j);
            menuBar.add(createMenu("Background Color"));
            menuBar.add(createMenu("Snake Color"));
            j = new JMenu("Mode");
            i = new JMenuItem("Normal");
            i.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    gameMode = 1;
                    restartGame();
                }
            });
            j.add(i);
            i = new JMenuItem("Insane");
            i.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    gameMode = 2;
                    restartGame();
                }
            });
            j.add(i);
            i = new JMenuItem("2 Player");
            i.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    gameMode = 3;
                    restartGame();
                }
            });
            j.add(i);
            menuBar.add(j);

            menuBar.setBorder(BorderFactory.createMatteBorder(
                0,
                0,
                0,
                0,
                Color.BLACK));

            return menuBar;
        }


        public JMenu createMenu(String title)
        {
            JMenu m = new HorizontalMenu(title);
            JMenuItem i = new JMenuItem("Black");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.black;
                    }
                    else
                    {
                        snakeColor = Color.black;
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("Blue");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.blue;
                    }
                    else
                    {
                        snakeColor = Color.blue;
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("Cyan");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.cyan;
                    }
                    else
                    {
                        snakeColor = Color.cyan;
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("Dark Gray");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.darkGray;
                    }
                    else
                    {
                        snakeColor = Color.darkGray;
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("Gray");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.gray;
                    }
                    else
                    {
                        snakeColor = Color.gray;
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("Green");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.green.darker();
                    }
                    else
                    {
                        snakeColor = Color.green.darker();
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("Light Gray");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.lightGray;
                    }
                    else
                    {
                        snakeColor = Color.lightGray;
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("Magenta");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.magenta;
                    }
                    else
                    {
                        snakeColor = Color.magenta;
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("Orange");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.orange;
                    }
                    else
                    {
                        snakeColor = Color.orange;
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("Pink");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.pink;
                    }
                    else
                    {
                        snakeColor = Color.pink;
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("Red");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.red;
                    }
                    else
                    {
                        snakeColor = Color.red;
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("White");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.white;
                    }
                    else
                    {
                        snakeColor = Color.white;
                    }
                }

            });
            m.add(i);
            i = new JMenuItem("Yellow");
            i.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem source = (JMenuItem)e.getSource();
                    String s =
                        ((JMenu)((JPopupMenu)source.getParent()).getInvoker())
                            .getText();
                    if (s.equals("Background Color"))
                    {
                        backGroundColor = Color.yellow;
                    }
                    else
                    {
                        snakeColor = Color.yellow;
                    }
                }

            });
            m.add(i);

            return m;
        }


        class HorizontalMenu
            extends JMenu
        {
            HorizontalMenu(String label)
            {
                super(label);
                JPopupMenu pm = getPopupMenu();
                pm.setLayout(new BoxLayout(pm, BoxLayout.LINE_AXIS));
            }


            public Dimension getMinimumSize()
            {
                return getPreferredSize();
            }


            public Dimension getMaximumSize()
            {
                return getPreferredSize();
            }


            public void setPopupMenuVisible(boolean b)
            {
                boolean isVisible = isPopupMenuVisible();
                if (b != isVisible)
                {
                    if ((b) && isShowing())
                    {
                        int x = 0;
                        int y = 0;
                        Container parent = getParent();
                        if (parent instanceof JPopupMenu)
                        {
                            x = 0;
                            y = getHeight();
                        }
                        else
                        {
                            x = getWidth();
                            y = 0;
                        }
                        getPopupMenu().show(this, x, y);
                    }
                    else
                    {
                        getPopupMenu().setVisible(false);
                    }
                }
            }
        }

    }
}
