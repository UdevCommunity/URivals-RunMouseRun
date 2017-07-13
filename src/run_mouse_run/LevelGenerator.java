package run_mouse_run;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class LevelGenerator
{
    private GameManager gameManager;
    private final int MAP_WIDTH_MIN = 10;
    private final int MAP_WIDTH_MAX = 50;

    private final int MAP_HEIGHT_MIN = 10;
    private final int MAP_HEIGHT_MAX = 50;

    public static int MAP_WIDTH = 0;
    public static int MAP_HEIGHT = 0;

    public static Position MOUSES_INITIAL_POS;
    public static Position CATS_INITIAL_POS;
    private final static int INITIAL_MINIMUM_DISTANCE = 5; // minimum eloignés de 15 cases

    private Map map;

    private final int WALL_PROBABILITY_THRESHOLD = 35;
    private final int POWERUP_VISION_PROBABILITY_THRESHOLD = WALL_PROBABILITY_THRESHOLD + 1;    // 3%
    private final int POWERUP_SPEED_PROBABILITY_THRESHOLD = POWERUP_VISION_PROBABILITY_THRESHOLD + 1; // 1%
    private final int INVISIBLE_ZONE_PROBABILITY_THRESHOLD = POWERUP_SPEED_PROBABILITY_THRESHOLD + 1; // 1%
    private final int MINE_PROBABILITY_THRESHOLD = INVISIBLE_ZONE_PROBABILITY_THRESHOLD + 1;  // 1%
    private final int EMPTY_PATH_PROBABILITY_THRESHOLD = 100;

    // IMPORTANT : MAZE[POS_Y][POS_X]

    private PathFinder pathFinder;

    public LevelGenerator(GameManager gameManager)
    {
        this.gameManager = gameManager;
        pathFinder = new PathFinder();

        // nextInt is normally exclusive of the top value,
        // so we add 1 to make it inclusive
        this.MAP_WIDTH = ThreadLocalRandom.current().nextInt(MAP_WIDTH_MIN, MAP_WIDTH_MAX + 1);
        this.MAP_HEIGHT = ThreadLocalRandom.current().nextInt(MAP_HEIGHT_MIN, MAP_HEIGHT_MAX + 1);

        // Generate map
        map = generateRandomMap(MAP_WIDTH, MAP_HEIGHT);

        // Set objects initial position
        setInitialPosition();

    }

    public Map getMap()
    {
        return map;
    }

    public void spawnVisionPowerup()
    {

    }

    public void spawnSpeedPowerup()
    {

    }

    /***
     * Set MOUSE, CATS and Cheese initial positions and checks if correct
     * correct position : Path exists, space consists 70% of empty Tiles
     * ... else regenerate a new map
     */
    private void setInitialPosition()
    {
        /* Set Mouse initial pos*/
        do
        {
            MOUSES_INITIAL_POS = getEmptyPos();
            CATS_INITIAL_POS = getEmptyPos();
        }
        while( !isPathOkay(MOUSES_INITIAL_POS, CATS_INITIAL_POS) // repeat until path exists
                );  // And mouses and cats are far enough

        /*Spawn cheese*/
        for (int i = 0; i < gameManager.CAT_NUMBER+1; i++)
        {
            Position cheesePos;
            do{
                cheesePos = getEmptyPos();
            } while (!isPathOkay(MOUSES_INITIAL_POS, cheesePos) ||
            map.getTile(cheesePos.getPosX(), cheesePos.getPosY()) == Tile.CHEESE);

            // path exists, we add the cheese to the map
            map.setTile(cheesePos.getPosX(), cheesePos.getPosY(), Tile.CHEESE);
        }

        int cellCount = getEmptyCellCount(MOUSES_INITIAL_POS, new ArrayList<Position>());

        if(cellCount < MAP_HEIGHT*MAP_WIDTH*WALL_PROBABILITY_THRESHOLD/100*2/3) // 2/3 /// TODO : test values
        {
            // Generate map
            map = generateRandomMap(MAP_WIDTH, MAP_HEIGHT);
            // Set objects initial position
            setInitialPosition();
        }
        else
        {
            System.out.println("OK");
        }
    }

    /**
     * Checks if path exists, and distance is far enough ( >= INITIAL_MINIMUM_DISTANCE )
     */
    private boolean isPathOkay(Position src, Position dst)
    {
        ArrayList<Position> path = pathFinder.getShortestPath(map, src, dst);

        if(path.isEmpty() || path.size() < INITIAL_MINIMUM_DISTANCE)
            return false;
        else
            return true;
    }

    /**
     * compute path using run_mouse_run.PathFinder
     * @return true if path exists
     */
    public boolean existPath(Position source, Position destination)
    {
        if(Position.comparePosition(source, destination))
            return true;

        if(pathFinder.getShortestPath(map, source, destination).isEmpty())
            return false;
        else
            return true;
    }

    /**
    * Generates a map random using Probability constants
     * @param mapHeight
     * @param mapWidth
     * @return map (Map) , the generated map ( doesn't modify this class' map )
    */
    private Map generateRandomMap(int mapWidth, int mapHeight)
    {
        Map randomMap = new Map("Level Map", MAP_WIDTH, MAP_HEIGHT, Tile.EMPTY);

        for(int i = 0; i < MAP_HEIGHT; i++)
        {
            for(int j = 0; j < MAP_WIDTH; j++)
            {
                int randomNum = ThreadLocalRandom.current().nextInt(0, 100 + 1);

                if(randomNum < WALL_PROBABILITY_THRESHOLD)
                {
                    randomMap.setTile(j, i, Tile.WALL);
                }
                else if (randomNum < POWERUP_VISION_PROBABILITY_THRESHOLD)
                {
                    randomMap.setTile(j, i, Tile.POWERUP_VISION);
                }
                else if (randomNum < POWERUP_SPEED_PROBABILITY_THRESHOLD)
                {
                    randomMap.setTile(j, i, Tile.POWERUP_SPEED);
                }
                else if(randomNum < INVISIBLE_ZONE_PROBABILITY_THRESHOLD)
                {
                    randomMap.setTile(j, i, Tile.INVISIBLE_ZONE);
                }
                else if(randomNum < MINE_PROBABILITY_THRESHOLD)
                {
                    randomMap.setTile(j, i, Tile.MINE);
                }
                else
                {
                    randomMap.setTile(j, i, Tile.EMPTY);
                }
            }
        }

        return randomMap;
    }

    /**
     * Counts the number of reachable cells from a given start point
     * @param start any position in given space to count
     * @param visited an empty ArrayList to keep track of visited nodes when visiting
     * @return the number of tiles in that space
     */
    public int getEmptyCellCount(Position start, ArrayList<Position> visited)
    {
        boolean isOutOfBound = (start.getPosX() >= MAP_WIDTH || start.getPosX() < 0
                                || start.getPosY() >= MAP_HEIGHT || start.getPosY() < 0);

        boolean isWall = isOutOfBound || (map.getTile(start.getPosX(), start.getPosY()) == Tile.WALL);

        boolean isVisited = false;
        for(Position p : visited)
            if(Position.comparePosition(p,start))
                isVisited = true;

        if(isOutOfBound || isWall || isVisited)
        {
            return 0;
        }
        else
        {
            visited.add(start);
            return 1 +
                    getEmptyCellCount(new Position(start.getPosX()+1, start.getPosY()), visited)
                    + getEmptyCellCount(new Position(start.getPosX()-1, start.getPosY()), visited)
                    + getEmptyCellCount(new Position(start.getPosX(), start.getPosY()+1), visited)
                    + getEmptyCellCount(new Position(start.getPosX(), start.getPosY()-1), visited);
        }


    }

    /**
     * keep generating random x, y coordinates until getting an empty position
     * @return an Empty run_mouse_run.Position
     */
    public Position getEmptyPos()
    {
        while (true)
        {
            // get random position
            int x = ThreadLocalRandom.current().nextInt(0, MAP_WIDTH);
            int y = ThreadLocalRandom.current().nextInt(0, MAP_HEIGHT);

            if (map.getTile(x, y) == Tile.EMPTY)
            {
                return new Position(x, y);
            }
        }
    }

}