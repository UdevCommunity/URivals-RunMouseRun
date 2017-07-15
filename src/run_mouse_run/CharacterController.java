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
    protected ArrayList<Position> destinationPath;
    private int moveSpeed;
    private int viewDistance;
    private long movePowerupTourLeft = 0;
    private long visionPowerupTourLeft = 0;
    protected Map map;
    protected Map viewedMap;

    private final int CONSEQUENT_MOVE_DELAY = CustomTimer.GAME_SPEED/2; // In milliseconds, the delay between two moves
    private final int UPDATE_FREQUENCE = CustomTimer.GAME_SPEED; // In milliseconds
    private Timer timer;
    private TimerTask task;

    public CharacterController(String name, Position initialPosition)
    {
        this.name = name;
        this.position = initialPosition;
        destinationPath = new ArrayList<>();
        moveSpeed = INITIAL_MOVE_SPEED;
        viewDistance = INITIAL_VIEW_DISTANCE;
        map = new Map(String.format("%s Map", name), LevelGenerator.MAP_WIDTH, LevelGenerator.MAP_HEIGHT, Tile.NOT_DISCOVERED);

        task = createUpdateTask();
        timer = new Timer();
    }

    final public TimerTask createUpdateTask()
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

    final public void startTimer()
    {
        timer.scheduleAtFixedRate(task, 0, UPDATE_FREQUENCE);
    }

    final public void stopTimer()
    {
        timer.cancel();
    }

    final public void resumeTimer()
    {
        task = createUpdateTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, UPDATE_FREQUENCE);
    }

    final public void die()
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

    final public void applyVisionPowerUp()
    {
        viewDistance = INITIAL_VIEW_DISTANCE + 1;
        visionPowerupTourLeft = 10;

        if(GameManager.gameManager.getLevelGenerator().getMap().getTile(position.getPosX(), position.getPosY()) != Tile.POWERUP_VISION)
            GameManager.gameManager.stopGame("Cat Lose", name);
    }

    final public void applyMoveSpeedPowerup()
    {
        moveSpeed = INITIAL_MOVE_SPEED + 1;
        movePowerupTourLeft = 10;

        if(GameManager.gameManager.getLevelGenerator().getMap().getTile(position.getPosX(), position.getPosY()) != Tile.POWERUP_SPEED)
            GameManager.gameManager.stopGame("Cat Lose", name);
    }

    final private void reducePowerupsTour()
    {
        moveSpeed = (--movePowerupTourLeft > 0)? moveSpeed: INITIAL_MOVE_SPEED;
        viewDistance = (--visionPowerupTourLeft > 0)? viewDistance: INITIAL_VIEW_DISTANCE;
    }

    final private void discoverMap()
    {
        viewedMap = GameManager.gameManager.getLevelGenerator().getViewedMap(position, viewDistance);

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

    final protected ArrayList<Position> computePath(Map map, Position destination)
    {
        return GameManager.gameManager.getLevelGenerator().getPathFinder().getShortestPath(map, position, destination);
    }

    final private void move()
    {
        if (!destinationPath.isEmpty() && (((destinationPath.get(0).getPosX() - position.getPosX()) > 1) ||
                ((destinationPath.get(0).getPosY() - position.getPosY()) > 1)))
            GameManager.gameManager.stopGame("Cat Lose", name);

        for (int i = 0; i < moveSpeed; i++)
        {
            if (destinationPath.isEmpty())
                return;

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

    final public Position getPosition()
    {
        return position;
    }

    final public ArrayList<Position> getDestinationPath()
    {
        return destinationPath;
    }
}


