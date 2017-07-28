package run_mouse_run;


import java.util.ArrayList;

public abstract class Cat extends CharacterController
{
    public Cat(String name)
    {
        super(name, new ArrayList<Tile>(){{add(Tile.CHEESE); add(Tile.INVISIBLE_ZONE); add(Tile.MINE);}});
    }

    final void respawn()
    {
        super.respawn("Cat");
    }

}
