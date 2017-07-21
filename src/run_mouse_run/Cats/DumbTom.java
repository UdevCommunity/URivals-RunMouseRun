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

        ArrayList<Position> mousesPosition = getViewedMap().getSpecialTilesPosition(Tile.MOUSE);

        if (!mousesPosition.isEmpty())
        {
            computeDestinationPath(getViewedMap(), mousesPosition.get(0));
        }

        if(!getDestinationPath().isEmpty())
        {
            if(getViewedMap().getTile(getDestinationPath().get(0).getPosX(), getDestinationPath().get(0).getPosY()) == Tile.WALL
                    || !canCrossByDiagonal(getPosition(), getDestinationPath().get(0)))
                getDestinationPath().clear();

            return;
        }


        else
        {
            searchAtRandomNotDiscovredTile();
        }

    }

    void searchAtRandomNotDiscovredTile()
    {
        ArrayList<Position> notDiscovredTiles = getViewedMap().getSpecialTilesPosition(Tile.NOT_DISCOVERED);

        do {
            int posIndex = ThreadLocalRandom.current().nextInt(0, notDiscovredTiles.size());
            Position pos = notDiscovredTiles.remove(posIndex);

            computeDestinationPath(getViewedMap(), new Position(pos.getPosX(), pos.getPosY()));

            if (!getDestinationPath().isEmpty())
                break;
        }while (true);
    }

    void searchAtBorders()
    {
        ArrayList<Position> borders = getViewedMap().getDiscoveredBorders();

        do {
            int posIndex = ThreadLocalRandom.current().nextInt(0, borders.size());
            Position pos = borders.remove(posIndex);

            Tile viewedTile = getViewedMap().getTile(pos.getPosX(), pos.getPosY());

            if(viewedTile == Tile.NOT_DISCOVERED || viewedTile == Tile.WALL)
                continue;

            computeDestinationPath(getViewedMap(), new Position(pos.getPosX(), pos.getPosY()));

            if (!getDestinationPath().isEmpty())
                break;
        }while (true);
    }
}