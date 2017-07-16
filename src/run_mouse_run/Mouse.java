package run_mouse_run;


import java.util.ArrayList;

public abstract class Mouse extends CharacterController
{
    public Mouse(String name, Position initialPosition)
    {
        super(name, initialPosition, new ArrayList<Tile>(){{add(Tile.MINE);}});
    }

    final void die()
    {
        super.die("Mouse");
    }
}
