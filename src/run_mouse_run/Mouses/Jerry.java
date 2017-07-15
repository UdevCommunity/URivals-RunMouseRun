package run_mouse_run.Mouses;

import run_mouse_run.GameManager;
import run_mouse_run.Mouse;
import run_mouse_run.Position;
import run_mouse_run.Tile;

import java.util.ArrayList;

public class Jerry extends Mouse
{
    public Jerry(String name, Position initialPosition)
    {
        super(name, initialPosition);
    }

    @Override
    protected void computeDecision()
    {
        super.computeDecision();

        ArrayList<Position> cheesesPosition = viewedMap.getCheesesPosition();

        if(!destinationPath.isEmpty())
            return;

        if (!cheesesPosition.isEmpty())
        {
            destinationPath = computePath(viewedMap, cheesesPosition.get(0));
        }
        else
        {
            for (int i = 0; i < viewedMap.getWidth(); i++)
                for (int j = 0; j < viewedMap.getHeight(); j++)
                {
                    Tile viewedTile = viewedMap.getTile(i, j);

                    if(viewedTile == Tile.NOT_DISCOVERED || viewedTile == Tile.WALL)
                        continue;

                    destinationPath = computePath(viewedMap, new Position(i, j));
                    return;
                }
        }

    }
}
