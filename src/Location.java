/**
 * // -------------------------------------------------------------------------
 * /** A location class that stores an x and y coordinate.
 *
 * @author Tony
 * @version Apr 14, 2014
 */
public class Location
{
    private int x, y;


    /**
     * Constructor for the location.
     *
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     */
    public Location(int x, int y)
    {
        this.x = x;
        this.y = y;
    }


    /**
     * Returns the x coordinate
     *
     * @return the x coordinate
     */
    public int getX()
    {
        return x;
    }


    /**
     * Returns the y coordinate
     *
     * @return the y coordinate
     */
    public int getY()
    {
        return y;
    }


    /**
     * Changes the x coordinate to the input
     *
     * @param x
     *            the new x coordinate
     */
    public void setX(int x)
    {
        this.x = x;
    }


    /**
     * Changes the y coordinate to the input
     *
     * @param y
     *            the new y coordinate
     */
    public void setY(int y)
    {
        this.y = y;
    }
}
