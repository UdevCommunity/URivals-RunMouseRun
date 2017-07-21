package run_mouse_run;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class CharacterController
{
    private final int INITIAL_MOVE_SPEED = 1;
    private final int INITIAL_VIEW_DISTANCE = 5;
    private final int STUN_EFFECT_DELAY = 3000;
    private final int CONSEQUENT_MOVE_DELAY = CustomTimer.GAME_SPEED/2;
    private final int UPDATE_FREQUENCE = CustomTimer.GAME_SPEED;

    private String name;
    private Position position;
    private ArrayList<Position> destinationPath;
    private ArrayList<Tile> invisibleTiles;
    private int moveSpeed;
    private int viewDistance;
    private long movePowerupTourLeft = 0;
    private long visionPowerupTourLeft = 0;
    private int targetReachedCount = 0;
    private Map map;
    private Map viewedMap;
    private PathFinder pathFinder;
    private boolean seeBehindWalls = false;
    private boolean isAlive = true;

    private Timer timer;

    CharacterController(String name, ArrayList<Tile> invisibleTiles)
    {
        this.name = name;

        destinationPath = new ArrayList<>();

        moveSpeed = INITIAL_MOVE_SPEED;
        viewDistance = INITIAL_VIEW_DISTANCE;

        this.invisibleTiles = invisibleTiles;
    }

    private TimerTask createUpdateTask()
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
        if(!isAlive)
            return;

        TimerTask task = createUpdateTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, UPDATE_FREQUENCE);
    }

    final void stopTimer()
    {
        timer.cancel();
    }

    void respawn(String characterType)
    {
        stopTimer();
        System.out.println("Respawn: "+ name +" " + GameManager.gameManager.getLevelGenerator().getMap().getTile(position.getPosX(), position.getPosY()));
        position = GameManager.gameManager.getLevelGenerator().getValidRespawnPosition(characterType);
        destinationPath.clear();
        moveSpeed = INITIAL_MOVE_SPEED;
        viewDistance = INITIAL_VIEW_DISTANCE;
        movePowerupTourLeft = 0;
        visionPowerupTourLeft = 0;
        startTimer();
    }

    final void die()
    {
        GameManager.gameManager.getLevelGenerator().getMap().setTile(position.getPosX(), position.getPosY(), Tile.EMPTY);
        isAlive = false;
        destinationPath.clear();
        stopTimer();
    }

    final void increaseTargetReachedCount()
    {
        targetReachedCount += 1;
        System.out.println(name + " Catch mouse " + targetReachedCount);
    }

    final void applyVisionPowerUp()
    {
        viewDistance = INITIAL_VIEW_DISTANCE + 1;
        visionPowerupTourLeft = 10;
        seeBehindWalls = true;
    }

    final void applyMoveSpeedPowerup()
    {
        moveSpeed = INITIAL_MOVE_SPEED + 1;
        movePowerupTourLeft = 10;
    }

    private void reducePowerupsTour()
    {
        moveSpeed = (--movePowerupTourLeft > 0)? moveSpeed: INITIAL_MOVE_SPEED;

        if(--visionPowerupTourLeft <= 0)
        {
            viewDistance = INITIAL_VIEW_DISTANCE;
            seeBehindWalls = false;
        }
    }

    private void discoverMap()
    {
        viewedMap = GameManager.gameManager.getLevelGenerator().getViewedMap(viewedMap, position, viewDistance, seeBehindWalls, invisibleTiles);

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

    private void move()
    {
        if (!destinationPath.isEmpty() && (((destinationPath.get(0).getPosX() - position.getPosX()) > 1) ||
                ((destinationPath.get(0).getPosY() - position.getPosY()) > 1)))
            GameManager.gameManager.stopGame(name + " tried to cheat in move !!");

        for (int i = 0; i < moveSpeed; i++)
        {
            if (i > 0)
                try {Thread.sleep(CONSEQUENT_MOVE_DELAY);} catch (InterruptedException e) {e.printStackTrace();}

            if (destinationPath.isEmpty() || !canCrossByDiagonal(position, destinationPath.get(0)))
                return;

            if (GameManager.gameManager.getLevelGenerator().getMap().getTile(destinationPath.get(0).getPosX(), destinationPath.get(0).getPosY()) == Tile.WALL)
            {
                try {Thread.sleep(STUN_EFFECT_DELAY);} catch (InterruptedException e) {e.printStackTrace();}
                return;
            }

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

    final protected ArrayList<Position> computeDestinationPath(Map map, Position destination)
    {
        setDestinationPath(pathFinder.getShortestPath(map, position, destination));
        return destinationPath;
    }

    final protected ArrayList<Position> computePath(Map map, Position source, Position destination)
    {
        return pathFinder.getShortestPath(map, source, destination);
    }

    final protected boolean canCrossByDiagonal(Position position, Position next)
    {
        return GameManager.gameManager.getLevelGenerator().canCrossByDiagonal(position, next);
    }

    final private void setDestinationPath(ArrayList<Position> destinationPath)
    {
        this.destinationPath = destinationPath;
    }

    final public ArrayList<Position> getDestinationPath()
    {
        return destinationPath;
    }

    final public String getName()
    {
        return name;
    }

    final public Position getPosition()
    {
        return position;
    }

    final public Map getViewedMap()
    {
        return viewedMap;
    }

    final public Map getMap() { return map; }

    final int getTargetReachedCount()
    {
        return targetReachedCount;
    }

    final void setPosition(Position position)
    {
        this.position = position;
    }

    final void setMap(Map map)
    {
        this.map = map;
    }

    final void setViewedMap(Map viewedMap)
    {
        this.viewedMap = viewedMap;
    }

    final void setPathFinder(PathFinder pathFinder)
    {
        this.pathFinder = pathFinder;
    }

    final void setSeeBehindWalls(boolean seeBehindWalls)
    {
        this.seeBehindWalls = seeBehindWalls;
    }

    final boolean isAlive()
    {
        return isAlive;
    }
}


