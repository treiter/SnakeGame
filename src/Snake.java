import java.util.LinkedList;

/**
 * // -------------------------------------------------------------------------
 * /** Snake class for the game. Uses a linked list to store locations, cause a
 * snake is list-like. If the snake doesn't eat food, add a new head, and remove
 * the tail. If it does eat food, don't remove the tail (so length increases).
 *
 * @author Tony
 * @version Apr 14, 2014
 */
public class Snake
{
    private LinkedList<Location> snakeList = new LinkedList<Location>();
    private int[][]              backgroundMatrix;


    /**
     * Creates a new snake object, with a pointer matrix to the logicMatrix from
     * game. Because matrix1 = matrix2 creates a shallow copy, this will make
     * that updating the matrix in either class (game or snake) will update it
     * in both.
     *
     * @param backgroundMatrix
     *            a integer matrix containing the locations of everything. 0 is
     *            blank, 1 is food, 2 is a snake part
     */
    public Snake(int[][] backgroundMatrix)
    {
        this.backgroundMatrix = backgroundMatrix; // should create a shallow
                                                  // copy
        int x = backgroundMatrix.length / 2;
        int y = backgroundMatrix[x].length / 2;
        backgroundMatrix[x][y] = 2;
        snakeList.addFirst(new Location(x, y));
    }


    /**
     * To be called every time the snake moves. Moves the head forward in the
     * correct direction, and if it eats food, increases the length by one.
     * Checks to see if the snake is out of bounds, or if it has hit itself.
     *
     * @param direction
     *            the direction to move in (1=up, 2=right, 3=down, 4=left).
     * @return a status indicator. 0 means the snake just moved with no
     *         complications, 1 means the snake ate food, and 2 means the snake
     *         died //TODO: make it return 2 instead of just exiting
     */
    public int move(int direction)
    {
        Location head = snakeList.getFirst();
        Location newHead = new Location(head.getX(), head.getY());
        switch (direction)
        {
            case 1:
                newHead.setY(newHead.getY() - 1);
                break;
            case 2:
                newHead.setX(newHead.getX() + 1);
                break;
            case 3:
                newHead.setY(newHead.getY() + 1);
                break;
            case 4:
                newHead.setX(newHead.getX() - 1);
                break;
            default:
                break;
        }
        int returnStatus = 0;
        // check to see if it died
        if (newHead.getX() >= backgroundMatrix.length || newHead.getX() < 0)
            System.exit(1);
        if (newHead.getY() >= backgroundMatrix[0].length || newHead.getY() < 0)
            System.exit(1);
        if (backgroundMatrix[newHead.getX()][newHead.getY()] == 2)
            System.exit(1);

        // if it didn't eat food, remove the tail
        if (backgroundMatrix[newHead.getX()][newHead.getY()] == 0)
        {
            Location tail = snakeList.removeLast();
            backgroundMatrix[tail.getX()][tail.getY()] = 0;
        }
        else
        {
            System.out.println(snakeList.size() + 1);
            returnStatus = 1;
        }
        // add the new head, update where it is, and return the correct status
        snakeList.addFirst(newHead);
        backgroundMatrix[newHead.getX()][newHead.getY()] = 2;
        return returnStatus;
    }
}
