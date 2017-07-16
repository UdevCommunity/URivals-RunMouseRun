package run_mouse_run;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class CharacterController
{
    private final int INITIAL_MOVE_SPEED = 1;
    private final int INITIAL_VIEW_DISTANCE = 5;

    private String name;
    private Position position;
    private ArrayList<Position> destinationPath;
    private ArrayList<Tile> invisibleTiles;
    private int moveSpeed;
    private int viewDistance;
    private long movePowerupTourLeft = 0;
    private long visionPowerupTourLeft = 0;
    private Map map;
    private Map viewedMap;
    private PathFinder pathFinder;
    private boolean seeBehindWalls = false;

    private final int CONSEQUENT_MOVE_DELAY = CustomTimer.GAME_SPEED/2; // In milliseconds, the delay between two moves
    private final int UPDATE_FREQUENCE = CustomTimer.GAME_SPEED; // In milliseconds
    private Timer timer;
    private TimerTask task;

    public CharacterController(String name, Position initialPosition, ArrayList<Tile> invisibleTiles)
    {
        this.name = name;
        this.position = initialPosition;
        destinationPath = new ArrayList<>();
        this.invisibleTiles = invisibleTiles;
        moveSpeed = INITIAL_MOVE_SPEED;
        viewDistance = INITIAL_VIEW_DISTANCE;

        map = new Map(name, LevelGenerator.MAP_WIDTH, LevelGenerator.MAP_HEIGHT, Tile.NOT_DISCOVERED);
                //GameManager.gameManager.getLevelGenerator().getViewedMap(String.format("%s Map", name), position, viewDistance);
        viewedMap = map.copy();
        pathFinder = new PathFinder();

        task = createUpdateTask();
        timer = new Timer();
    }

    final private TimerTask createUpdateTask()
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                discoverMap();
                computeDecision();
                move();
            }
        };
    }

    final void startTimer()
    {
        timer.scheduleAtFixedRate(task, 0, UPDATE_FREQUENCE);
    }

    final void stopTimer()
    {
        timer.cancel();
    }

    final void resumeTimer()
    {
        task = createUpdateTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, UPDATE_FREQUENCE);
    }

    final void die()
    {
        position = GameManager.gameManager.getLevelGenerator().getValidRespawnPosition();
        destinationPath.clear();
        moveSpeed = INITIAL_MOVE_SPEED;
        viewDistance = INITIAL_VIEW_DISTANCE;
        movePowerupTourLeft = 0;
        visionPowerupTourLeft = 0;
        stopTimer();
        resumeTimer();
    }

    final void applyVisionPowerUp()
    {
        viewDistance = INITIAL_VIEW_DISTANCE + 1;
        visionPowerupTourLeft = 10;
    }

    final void applyMoveSpeedPowerup()
    {
        moveSpeed = INITIAL_MOVE_SPEED + 1;
        movePowerupTourLeft = 10;
    }

    private void reducePowerupsTour()
    {
        moveSpeed = (--movePowerupTourLeft > 0)? moveSpeed: INITIAL_MOVE_SPEED;
        viewDistance = (--visionPowerupTourLeft > 0)? viewDistance: INITIAL_VIEW_DISTANCE;
    }

    private void discoverMap()
    {
        viewedMap = GameManager.gameManager.getLevelGenerator().getViewedMap(viewedMap, position, viewDistance);

        for (int i = 0; i < viewedMap.getWidth(); i++)
            for (int j = 0; j < viewedMap.getHeight(); j++)
            {
                Tile viewedTile = viewedMap.getTile(i, j);

                if(viewedTile == Tile.NOT_DISCOVERED)
                    viewedMap.setTile(i, j, map.getTile(i, j));
                else
                    map.setTile(i, j, (viewedTile != Tile.CAT && viewedTile != Tile.MOUSE)? viewedTile: Tile.EMPTY);
            }
    }

    final protected ArrayList<Position> computeDestinationPath(Map map, Position destination)
    {
        return pathFinder.getShortestPath(map, position, destination);
    }

    final protected ArrayList<Position> computePath(Map map, Position source, Position destination)
    {
        return pathFinder.getShortestPath(map, source, destination);
    }

    final protected boolean canCrossByDiagonal(Position position, Position next)
    {
        return GameManager.gameManager.getLevelGenerator().canCrossByDiagonal(position, next);
    }

    private void move()
    {
        if (!destinationPath.isEmpty() && (((destinationPath.get(0).getPosX() - position.getPosX()) > 1) ||
                ((destinationPath.get(0).getPosY() - position.getPosY()) > 1)))
            GameManager.gameManager.stopGame("Cat Lose", name);

        for (int i = 0; i < moveSpeed; i++)
        {
            if (i > 0)
                try {Thread.sleep(CONSEQUENT_MOVE_DELAY);} catch (InterruptedException e) {e.printStackTrace();}

            if (destinationPath.isEmpty() ||
                    !canCrossByDiagonal(position, destinationPath.get(0)))
                return;

            Tile actualTile = GameManager.gameManager.getLevelGenerator().getMap().getTile(position.getPosX(), position.getPosY());
            if(actualTile == Tile.CAT || actualTile == Tile.MOUSE)
                GameManager.gameManager.getLevelGenerator().getMap().setTile(position.getPosX(), position.getPosY(), Tile.EMPTY);

            position = destinationPath.remove(0);
        }

        reducePowerupsTour();
    }

    protected void computeDecision()
    {

    }

    final public String getName()
    {
        return name;
    }

    final public Position getPosition() {
        return position;
    }

    final public ArrayList<Position> getDestinationPath()
    {
        return destinationPath;
    }

    final protected void setDestinationPath(ArrayList<Position> destinationPath)
    {
        this.destinationPath = destinationPath;
    }

    final public Map getViewedMap() {
        return viewedMap;
    }

    final public Map getMap() { return map; }

    final public void setPosition(Position position) {
        this.position = position;
    }


}


