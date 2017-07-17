package run_mouse_run;


import java.util.ArrayList;

public abstract class Cat extends CharacterController
{
    public Cat(String name, Position initialPosition)
    {
        super(name, initialPosition, new ArrayList<Tile>(){{add(Tile.INVISIBLE_ZONE); add(Tile.MINE);}});
    }

    final void die(String deathCause)
    {
        super.die("Cat", deathCause);
    }

}
