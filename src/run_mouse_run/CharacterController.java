package run_mouse_run;

import java.util.ArrayList;

public abstract class CharacterController
{
    private GameManager gameManager;
    private String name;
    private Position position;
    private ArrayList<Position> destinationPath;
    private int moveSpeed = 1;
    private final int CONSEQUENT_MOVE_DELAY = CustomTimer.GAME_SPEED/2; // In milliseconds, the delay between two moves
    private final int VIEW_DISTANCE = 5;
    private int powerupTourLeft = 0;
    private Tile[][] map;
    private Tile[][] viewedMap;
    private boolean isVisible = true;

    public CharacterController(GameManager gameManager, String name, Position INITIAL_POSITION)
    {
        this.gameManager = gameManager;
        this.name = name;
        this.position = INITIAL_POSITION;
        map = new Tile[LevelGenerator.MAP_WIDTH][LevelGenerator.MAP_HEIGHT];
    }

    final public String getName()
    {
        return name;
    }

    final public void die()
    {
        // Reset to initial pos
    }

    final public void applyVisionPowerUp()
    {

    }

    final public void applyMoveSpeedPowerup()
    {

    }


    final private void reducePowerupTour()
    {
        if (powerupTourLeft > 0)
            powerupTourLeft--;

        if (powerupTourLeft == 0)
            moveSpeed = 1;
    }

    final private Position move(int moveSpeed) throws Exception
    {
        if (((destinationPath.get(0).getPosX() - position.getPosX()) > 1) ||
                ((destinationPath.get(0).getPosY() - position.getPosY()) > 1))
            throw new Exception("Hchem berka matghouch");

        for (int i = 0; i < moveSpeed; i++)
        {
            if (destinationPath.isEmpty())
                return position;

            position = destinationPath.remove(0);
        }

        reducePowerupTour();
        return position;
    }

    final protected ArrayList<Position> computePath(Position source, Position destination)
    {
        return null;
    }

    final public Tile[][] discoverMap() // TODO: Charma to rewrite
    {
        Tile[][] viewMap = new Tile[VIEW_DISTANCE + 1][VIEW_DISTANCE + 1];
        cleanMap(viewMap, VIEW_DISTANCE + 1, VIEW_DISTANCE + 1);

        Position startPoint = new Position(position.getPosX(), position.getPosY());

        startPoint.setPosX((startPoint.getPosX() - VIEW_DISTANCE >=0)?startPoint.getPosX() - VIEW_DISTANCE:0);
        startPoint.setPosY((startPoint.getPosX() - VIEW_DISTANCE >=0)?startPoint.getPosY() - VIEW_DISTANCE:0);

        for (int i = 0; i < VIEW_DISTANCE && startPoint.getPosX() + i < LevelGenerator.MAP_WIDTH; i++)
            for (int j = 0; j < VIEW_DISTANCE && startPoint.getPosY() + j < LevelGenerator.MAP_HEIGHT; j++)
            {
                //viewMap[i][j] = this.map[startPoint.getPosX() + i][startPoint.getPosY() +j] = run_mouse_run.LevelGenerator.map[startPoint.getPosX() + i][startPoint.getPosY() + j];

                /*for (int k = 0; k < run_mouse_run.GameManager.cats.size(); k++)
                    if (run_mouse_run.Position.comparePosition(new run_mouse_run.Position(i, j), run_mouse_run.GameManager.cats.get(k).getPosition()))
                        //viewMap[i][j] = run_mouse_run.Tile.CAT;

                for (int k = 0; k < run_mouse_run.GameManager.mouses.size(); k++)
                    if (run_mouse_run.Position.comparePosition(new run_mouse_run.Position(i, j), run_mouse_run.GameManager.mouses.get(k).getPosition()))
                        //viewMap[i][j] = run_mouse_run.Tile.MOUSE;*/
            }

        return viewMap;
    }

    final public Position getPosition()
    {
        return position;
    }

    final private void cleanMap(Tile[][] map, int mapWidth, int mapHeight)
    {
        for (int i = 0; i < mapWidth; i++)
            for (int j = 0; j < mapHeight; j++)
                map[i][j] = Tile.NOT_DISCOVERED;
    }


    protected void computeDecision()
    {
        // Si auccun objectif n'est trouvé, allé vers une zone innexploré de la map
    }

    final public boolean isVisible()
    {
        return isVisible;
    }


    final private void update()
    {
        viewedMap = discoverMap();
        computeDecision();
    }
}


