package run_mouse_run.Cats;

import run_mouse_run.Cat;
import run_mouse_run.Position;
import run_mouse_run.Tile;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class DumbTom extends Cat
{
    public DumbTom(String name, Position initialPosition)
    {
        super(name, initialPosition);
    }

    @Override
    protected void computeDecision()
    {
        super.computeDecision();

        ArrayList<Position> mousesPosition = viewedMap.getSpecialTilesPosition(Tile.MOUSE);

        if (!mousesPosition.isEmpty())
        {
            destinationPath = computePath(viewedMap, mousesPosition.get(0));
        }

        if(!destinationPath.isEmpty())
        {
            if(viewedMap.getTile(destinationPath.get(0).getPosX(), destinationPath.get(0).getPosY()) == Tile.WALL
                    || !canCrossByDiagonalWall(destinationPath.get(0)))
                destinationPath.clear();

            return;
        }


        else
        {
            ArrayList<Position> borders = viewedMap.getBorders();

            do {
                Position pos = borders.get(ThreadLocalRandom.current().nextInt(0, borders.size()));

                Tile viewedTile = viewedMap.getTile(pos.getPosX(), pos.getPosY());

                if(viewedTile == Tile.NOT_DISCOVERED || viewedTile == Tile.WALL)
                    continue;

                destinationPath = computePath(viewedMap, new Position(pos.getPosX(), pos.getPosY()));

                if (!destinationPath.isEmpty())
                    break;
            }while (true);
        }

    }
}