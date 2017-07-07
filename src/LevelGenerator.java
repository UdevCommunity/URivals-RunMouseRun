import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class LevelGenerator
{
    private final int MAP_WIDTH_MIN = 30;
    private final int MAP_WIDTH_MAX = 50;

    private final int MAP_HEIGHT_MIN = 30;
    private final int MAP_HEIGHT_MAX = 50;

    public static int MAP_WIDTH = 0;
    public static int MAP_HEIGHT = 0;

    public static Position MOUSES_INITIAL_POS;
    public static Position CATS_INITIAL_POS;
    private final static int INITIAL_MINIMUM_DISTANCE = 15; // minimum eloignés de 15 cases

    private Tile[][] map;

    private final int WALL_PROBABILITY_THRESHOLD = 35;
    private final int POWERUP_VISION_PROBABILITY_THRESHOLD = WALL_PROBABILITY_THRESHOLD + 3;    // 3%
    private final int POWERUP_SPEED_PROBABILITY_THRESHOLD = POWERUP_VISION_PROBABILITY_THRESHOLD + 3; // 3%
    private final int INVISIBLE_ZONE_PROBABILITY_THRESHOLD = POWERUP_SPEED_PROBABILITY_THRESHOLD + 3; // 6%
    private final int MINE_PROBABILITY_THRESHOLD = INVISIBLE_ZONE_PROBABILITY_THRESHOLD + 3;  // 6%
    private final int EMPTY_PATH_PROBABILITY_THRESHOLD = 100;

    // IMPORTANT : MAZE[POS_Y][POS_X]

    private PathFinder pathFinder;

    public LevelGenerator()
    {
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

    public Tile[][] getMap()
    {
        return map;
    }

    public void spawnPowerup()
    {

    }

    /***
     * Set MOUSE, CATS and Cheese initial positions and checks if correct
     */
    private void setInitialPosition()
    {
        /* Set Mouse initial pos*/
        do
        {
            MOUSES_INITIAL_POS = getEmptyPos();
            CATS_INITIAL_POS = getEmptyPos();
        }
        while( !existPath(MOUSES_INITIAL_POS, CATS_INITIAL_POS) // repeat until path exists
                );  // And mouses and cats are far enough

        /*Spawn cheese*/
        for (int i = 0; i < 3; i++) /// TODO : Use cat count here
        {
            Position cheesePos;
            do{
                cheesePos = getEmptyPos();
            } while (!isPathOkay(MOUSES_INITIAL_POS, cheesePos));
            // path exists, we add the cheese to the map
            map[cheesePos.getPosY()][cheesePos.getPosX()] = Tile.CHEESE;
        }
    }

    /**
     * Checks if path exists, and distance is far enough ( >= INITIAL_MINIMUM_DISTANCE )
     */
    private boolean isPathOkay(Position Mouse, Position Cat)
    {
        ArrayList<Position> path = pathFinder.getShortestPath(map, Mouse, Cat);

        if(path.isEmpty() || path.size() < INITIAL_MINIMUM_DISTANCE)
            return false;
        else
            return true;
    }

    /**
     * compute path using PathFinder
     * @return true if path exists
     */
    public boolean existPath(Position source, Position destination)
    {
        if(source.equals(destination))
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
     * @return map (Tile[][]) , the generated map ( doesn't modify this class' map )
    */
    private Tile[][] generateRandomMap(int mapWidth, int mapHeight)
    {
        Tile[][] randomMap = new Tile[MAP_HEIGHT][MAP_WIDTH];
        for(int i = 0; i < MAP_HEIGHT; i++)
        {
            for(int j = 0; j < MAP_WIDTH; j++)
            {
                int randomNum = ThreadLocalRandom.current().nextInt(0, 100 + 1);

                if(randomNum < WALL_PROBABILITY_THRESHOLD)
                {
                    randomMap[i][j] = Tile.WALL;
                }
                else if (randomNum < POWERUP_VISION_PROBABILITY_THRESHOLD)
                {
                    randomMap[i][j] = Tile.EMPTY;  // what's vision ?
                }
                else if (randomNum < POWERUP_SPEED_PROBABILITY_THRESHOLD)
                {
                    randomMap[i][j] = Tile.EMPTY;  // Speed
                }
                else if(randomNum < INVISIBLE_ZONE_PROBABILITY_THRESHOLD)
                {
                    randomMap[i][j] = Tile.EMPTY;  // invisible
                }
                else if(randomNum < MINE_PROBABILITY_THRESHOLD)
                {
                    randomMap[i][j] = Tile.MINE;
                }
                else
                {
                    randomMap[i][j] = Tile.EMPTY;
                }
            }
        }

        return randomMap;
    }

    /**
     * keep generating random x, y coordinates until getting an empty position
     * @return an Empty Position
     */
    public Position getEmptyPos()
    {
        while (true)
        {
            // get random position
            int x = ThreadLocalRandom.current().nextInt(0, MAP_WIDTH);
            int y = ThreadLocalRandom.current().nextInt(0, MAP_HEIGHT);

            if (map[y][x] == Tile.EMPTY)
            {
                return new Position(x, y);
            }
        }
    }

    public void switchTile(int x, int y)
    {
        map[y][x] = map[y][x].next();
    }



}