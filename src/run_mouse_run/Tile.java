package run_mouse_run;


public enum Tile
{
    NOT_DISCOVERED, EMPTY, WALL, CHEESE, POWERUP_VISION, POWERUP_SPEED, INVISIBLE_ZONE, MINE, CAT, MOUSE;

    private static Tile[] values = values();

    public Tile next()
    {
        return values[(this.ordinal()+1) % values.length];
    }
}

