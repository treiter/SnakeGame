import java.util.LinkedList;


public class Snake
{
    private LinkedList<Location> snakeList = new LinkedList<Location>();
    private int[][] backgroundMatrix;
    public Snake(int[][] backgroundMatrix) {
        this.backgroundMatrix = backgroundMatrix; //should create a shallow copy
        //TODO: make sure this works
        int x = backgroundMatrix.length/2;
        int y = backgroundMatrix[x].length/2;
        backgroundMatrix[x][y] = 2;
        snakeList.addFirst(new Location(x, y));
    }
    public void move(int direction) {
        Location head = snakeList.getFirst();
        Location newHead = new Location(head.getX(), head.getY());
        switch(direction) {
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
        if(newHead.getX() >= backgroundMatrix.length || newHead.getX() < 0)
            System.exit(1);
        if(newHead.getY() >= backgroundMatrix[0].length || newHead.getY() < 0)
            System.exit(1);
        if(backgroundMatrix[newHead.getX()][newHead.getY()] == 2)
            System.exit(1);
        if(backgroundMatrix[newHead.getX()][newHead.getY()] == 0) {
            Location tail = snakeList.removeLast();
            backgroundMatrix[tail.getX()][tail.getY()] = 0;
        }
        else {
            System.out.println(snakeList.size() + 1);
        }
        snakeList.addFirst(newHead);
        backgroundMatrix[newHead.getX()][newHead.getY()] = 2;

    }
}
