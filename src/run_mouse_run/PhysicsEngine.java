package run_mouse_run;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PhysicsEngine
{

    private GameManager gameManager;
    private Map levelMap;

    private ArrayList<Cat> cats;
    private ArrayList<Mouse> mouses;

    private final int UPDATE_FREQUENCE = CustomTimer.GAME_SPEED/4;
    private Timer timer;
    private TimerTask task;

    public PhysicsEngine(GameManager gameManager)
    {
        this.gameManager = gameManager;
        this.levelMap = gameManager.getLevelGenerator().getMap();

        this.cats = gameManager.getCats();
        this.mouses = gameManager.getMouses();

        task = new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    checkIsCatsOnSpecialTile();
                    checkIsMouseOnSpecialTile();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };
        timer = new Timer();

    }

    public void runPhysicsEngine()
    {
        timer.scheduleAtFixedRate(task, 0, UPDATE_FREQUENCE);
    }


    private void checkIsCatsOnSpecialTile() throws Exception
    {
        for (Cat cat: cats)
        {
            Tile tileCatIsStandingOn = levelMap.getTile(cat.getPosition().getPosX(), cat.getPosition().getPosY());

            switch (tileCatIsStandingOn)
            {
                case MOUSE:
                    gameManager.stopGame("Cat Win", cat.getName());
                    break;
                case MINE:
                    tileCatIsStandingOn = Tile.EMPTY;
                case WALL:
                    cat.die();
                    break;
                case POWERUP_VISION:
                    cat.applyVisionPowerUp();
                    gameManager.getLevelGenerator().spawnVisionPowerup();
                    break;
                case POWERUP_SPEED:
                    cat.applyMoveSpeedPowerup();
                    gameManager.getLevelGenerator().spawnSpeedPowerup();
                    break;
            }

            if(tileCatIsStandingOn == Tile.POWERUP_SPEED || tileCatIsStandingOn == Tile.POWERUP_VISION
                    || tileCatIsStandingOn == Tile.EMPTY)
                tileCatIsStandingOn = Tile.CAT;
        }
    }

    private void checkIsMouseOnSpecialTile() throws Exception
    {
        for (Mouse mouse: mouses)
        {
            Tile tileMouseIsStandingOn = levelMap.getTile(mouse.getPosition().getPosX(), mouse.getPosition().getPosY());

            switch (tileMouseIsStandingOn)
            {
                case CHEESE:
                    gameManager.stopGame("Mouse Win", mouse.getName());
                    break;
                case CAT:
                    for (Cat winningCat: cats)
                    {
                        if(Position.comparePosition(mouse.getPosition(), winningCat.getPosition()))
                        {
                            gameManager.stopGame("Cat Win", winningCat.getName());
                        }
                    }
                    break;
                case MINE:
                    tileMouseIsStandingOn = Tile.EMPTY;
                case WALL:
                    mouse.die();
                    break;
                case POWERUP_VISION:
                    mouse.applyVisionPowerUp();
                    gameManager.getLevelGenerator().spawnVisionPowerup();
                    break;
                case POWERUP_SPEED:
                    mouse.applyMoveSpeedPowerup();
                    gameManager.getLevelGenerator().spawnSpeedPowerup();
                    break;
            }

            if(tileMouseIsStandingOn == Tile.POWERUP_SPEED || tileMouseIsStandingOn == Tile.POWERUP_VISION
                    || tileMouseIsStandingOn == Tile.EMPTY)
                tileMouseIsStandingOn = Tile.MOUSE;
        }

    }
}
