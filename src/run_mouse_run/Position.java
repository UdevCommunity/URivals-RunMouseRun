package run_mouse_run;

public class Position
{
    private int posX;
    private int posY;

    public Position(int posX, int posY)
    {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX()
    {
        return posX;
    }

    public int getPosY()
    {
        return posY;
    }

    public void setPosX(int posX)
    {
        this.posX = posX;
    }

    public void setPosY(int posY)
    {
        this.posY = posY;
    }

    public static boolean comparePosition(Position positionOne, Position positionTwo)
    {
        try
        {
            return positionOne.getPosX() == positionTwo.getPosX() && positionOne.getPosY() == positionTwo.getPosY();
        }catch (NullPointerException e)
        {
            return false;
        }
    }

    public Position copy()
    {
        return new Position(posX, posY);
    }

    public static boolean areDiagonal(Position positionOne, Position positionTwo)
    {
        return (positionOne.getPosX() - positionTwo.getPosX() != 0) && (positionOne.getPosY() - positionTwo.getPosY() != 0);
    }
}
