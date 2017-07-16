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


        System.out.print("Begin Decision\n");
        System.out.print("Pos: "+ getPosition().getPosX() + " " + getPosition().getPosY() + "\n");

        ArrayList<Position> cheesesPosition = viewedMap.getSpecialTilesPosition(Tile.CHEESE);

        if(!destinationPath.isEmpty())
        {
            System.out.print("Path Not Empty\n");
            if(viewedMap.getTile(destinationPath.get(0).getPosX(), destinationPath.get(0).getPosY()) == Tile.WALL)
                destinationPath.clear();

            return;
        }

        System.out.print("Path Empty\n");

        if (!cheesesPosition.isEmpty())
        {
            System.out.print("CheeseList Not Empy\n");
            destinationPath = computePath(viewedMap, cheesesPosition.get(0));
            System.out.print("Initial Position: "+ LevelGenerator.MOUSES_INITIAL_POS.getPosX() + " " + LevelGenerator.MOUSES_INITIAL_POS.getPosY() +"\n");// getPosition().getPosX() + " " + getPosition().getPosY() +"\n" );
            System.out.print("Path:\n");
            for (Position pos: destinationPath)
            {
                System.out.print(pos.getPosX() + " " + pos.getPosY() + "\n");
            }
        }
        else
        {
            System.out.print("Didn't Found Cheese\n");
            ArrayList<Position> borders = viewedMap.getBorders();

            do {
                Position pos = borders.get(ThreadLocalRandom.current().nextInt(0, borders.size()));

                Tile viewedTile = viewedMap.getTile(pos.getPosX(), pos.getPosY());

                System.out.print("Border: " + pos.getPosX()+ " " + pos.getPosY()+ "\n");
                if(viewedTile == Tile.NOT_DISCOVERED || viewedTile == Tile.WALL)
                    continue;

                System.out.print("\nFound it !!" + pos.getPosX() + " " + pos.getPosY() + "\n");

                destinationPath = computePath(viewedMap, new Position(pos.getPosX(), pos.getPosY()));
                for (Position p: destinationPath)
                {
                    System.out.print(p.getPosX() + " " + p.getPosY() + "\n");
                }

                if (!destinationPath.isEmpty())
                    break;
            }while (true);
        }

    }
}
