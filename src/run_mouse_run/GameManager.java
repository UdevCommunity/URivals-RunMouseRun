package run_mouse_run;

import run_mouse_run.Cats.DumbTomTa3Bahaa;
import run_mouse_run.Mouses.DumbJerry;
import run_mouse_run.Cats.DumbTom;
import run_mouse_run.Mouses.PatchoFar;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;


class GameManager
{
    static GameManager gameManager;
    private GameMode GAME_MODE = GameMode.DEATH_MATCH;

    private ArrayList<Cat> cats;
    private ArrayList<Mouse> mouses;

    private final LevelGenerator level;
    private final PhysicsEngine physicsEngine;
    private final DrawEngine drawEngine;
    private final CustomTimer timer;

    GameManager()
    {
        gameManager = this;

        cats = new ArrayList<>();
        mouses = new ArrayList<>();

        // Instantiate mouses (do not use a loop, here we may instantiate mouses from different classes)
        mouses.add(new PatchoFar("Patcho"));
        mouses.add(new PatchoFar("Pitchou"));
        mouses.add(new PatchoFar("Potchou"));

        // Instantiate cats (do not use a loop, here we may instantiate cats from different classes)
        cats.add(new DumbTomTa3Bahaa("DumbTom"));
        cats.add(new DumbTomTa3Bahaa("Tom2"));

        level = new LevelGenerator();
        physicsEngine = new PhysicsEngine();
        drawEngine = new DrawEngine(level.getMap());
        timer = new CustomTimer();

        drawEngine.printMessage("Game Settings");
    }

    // We made a difference between startGame and resumeGame because we may have do to some initialisations at the beginning
    // of the game but not necessarily at resume.
    void startGame()
    {
        drawEngine.printMessage("Game Started !");
        timer.startTimer();

        SecurityManager securityManager = new SecurityManager();
        try
        {
            securityManager.checkForImportGameManager(getClass().getResource("Cats").getPath());
            securityManager.checkForImportGameManager(getClass().getResource("Mouses").getPath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(() ->
        {
            GameManager gameManager = new GameManager();
            gameManager.drawEngine.setVisible(true);
        });
    }

    void checkEndGameConditions(String result, String characterName)
    {
        switch (getGameMode())
        {
            case CLASSIC:
                stopGame(characterName + " Win !!");
                break;

            case DEATH_MATCH:
                switch (result)
                {
                    case "Mouses Win":
                        if(level.getMap().getSpecialTilesPosition(Tile.CHEESE).isEmpty())
                        {
                            StringBuilder winResult = new StringBuilder();
                            for (Mouse mouse: getMouses())
                            {
                                winResult.append(String.format(" {%s: %d}", mouse.getName(), mouse.getTargetReachedCount()));
                            }
                            stopGame(String.format("Mouses Win !! %s", winResult.toString()));
                        }
                        break;

                    case "Cats Win":
                        if(getAliveMousesCount() == 0)
                        {
                            StringBuilder winResult = new StringBuilder();
                            for (Cat cat: getCats())
                            {
                                winResult.append(String.format(" {%s: %d}", cat.getName(), cat.getTargetReachedCount()));
                            }
                            stopGame(String.format("Cats Win !! %s", winResult.toString()));
                        }
                        break;
                    default:
                        drawEngine.printMessage("Wrong end game argument passed !!");
                }
                break;
        }
    }

    void stopGame(String result)
    {
        try{timer.stopTimer();}catch (Exception ignored){}
        drawEngine.displayEndGameScreen(result);
    }


    void pauseGame()
    {
        timer.stopTimer();
    }

    void resumeGame()
    {
        timer.startTimer();
    }

    ArrayList<Cat> getCats()
    {
        return cats;
    }

    private int getAliveMousesCount()
    {
        int aliveMousesCount = 0;

        for(Mouse mouse: mouses)
        {
            if (mouse.isAlive())
                aliveMousesCount++;
        }

        return aliveMousesCount;
    }

    ArrayList<Mouse> getMouses()
    {
        return mouses;
    }

    PhysicsEngine getPhysicsEngine()
    {
        return physicsEngine;
    }

    LevelGenerator getLevelGenerator()
    {
        return level;
    }

    DrawEngine getDrawEngine()
    {
        return drawEngine;
    }

    CustomTimer getTimer()
    {
        return timer;
    }

    GameMode getGameMode()
    {
        return GAME_MODE;
    }

    void setGameMode(GameMode gameMode) {
        this.GAME_MODE = gameMode;
    }
}
