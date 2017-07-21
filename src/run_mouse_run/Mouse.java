package run_mouse_run;


import java.util.ArrayList;

public abstract class Mouse extends CharacterController
{
    public Mouse(String name)
    {
        super(name, new ArrayList<Tile>(){{add(Tile.MINE);}});
    }

    final void respawn()
    {
        super.respawn("Mouse");
    }
}
