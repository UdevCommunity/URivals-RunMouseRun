package run_mouse_run.Mouses;

import run_mouse_run.*;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class DumbJerry extends Mouse
{
    public DumbJerry(String name, Position initialPosition)
    {
        super(name, initialPosition);
    }

    @Override
    protected void computeDecision()
    {
        super.computeDecision();

        ArrayList<Position> cheesesPosition = getViewedMap().getSpecialTilesPosition(Tile.CHEESE);

        if (!cheesesPosition.isEmpty())
        {
            setDestinationPath(computeDestinationPath(getViewedMap(), cheesesPosition.get(0)));
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

            setDestinationPath(computeDestinationPath(getViewedMap(), new Position(pos.getPosX(), pos.getPosY())));

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

            setDestinationPath(computeDestinationPath(getViewedMap(), new Position(pos.getPosX(), pos.getPosY())));

            if (!getDestinationPath().isEmpty())
                break;
        }while (true);
    }
}
