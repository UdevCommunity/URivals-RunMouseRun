package run_mouse_run.Mouses;

import run_mouse_run.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

        /*System.out.print("\n\n");
        for (int i = 0; i < viewedMap.getWidth(); i++)
        {
            for (int j = 0; j < viewedMap.getHeight(); j++)
                System.out.print(viewedMap.getTile(i, j));
            System.out.print("\n");
        }
        */


        ArrayList<Position> cheesesPosition = viewedMap.getSpecialTilesPosition(Tile.CHEESE);

        if(!destinationPath.isEmpty())
            return;

        if (!cheesesPosition.isEmpty())
        {
            destinationPath = computePath(viewedMap, cheesesPosition.get(0));
            System.out.print("Initial Position: "+ LevelGenerator.MOUSES_INITIAL_POS.getPosX() + " " + LevelGenerator.MOUSES_INITIAL_POS.getPosY() +"\n");// getPosition().getPosX() + " " + getPosition().getPosY() +"\n" );
            System.out.print("Path:\n");
            for (int i = 0; i < destinationPath.size(); i++)
            {
                System.out.print(destinationPath.get(i).getPosX() + " " + destinationPath.get(i).getPosY() + "\n");
            }
        }/*
        else
        {
            do
            {
                int i = ThreadLocalRandom.current().nextInt(0, viewedMap.getWidth());
                int j = ThreadLocalRandom.current().nextInt(0, viewedMap.getHeight());

                Tile viewedTile = viewedMap.getTile(i, j);

                if(viewedTile == Tile.NOT_DISCOVERED || viewedTile == Tile.WALL)
                    continue;

                destinationPath = computePath(viewedMap, new Position(i, j));
                System.out.print("\nFound it !!" + i + " " + j + "\n");

                break;
            }while (true);
        }*/

    }
}
