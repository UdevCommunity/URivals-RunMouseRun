package run_mouse_run;

import run_mouse_run.Mouses.DumbJerry;
import run_mouse_run.Cats.DumbTom;

import java.awt.*;
import java.util.ArrayList;


public class GameManager
{
    static GameManager gameManager;

    public final int CAT_NUMBER = 2;
    public final int MOUSE_NUMBER = 1;

    private ArrayList<Cat> cats;
    private ArrayList<Mouse> mouses;

    private final LevelGenerator level;
    private final PhysicsEngine physicsEngine;
    private final DrawEngine drawEngine;
    private final CustomTimer timer;

    private GameManager()
    {
        gameManager = this;

        cats = new ArrayList<>();
        mouses = new ArrayList<>();

        level = new LevelGenerator();

        // Instantiate mouses (do not use a loop, here we may instantiate mouses from different classes)
        mouses.add(new DumbJerry("DumbJerry", LevelGenerator.MOUSES_INITIAL_POS));

        // Instantiate cats (do not use a loop, here we may instantiate cats from different classes)
        cats.add(new DumbTom("DumbTom", LevelGenerator.CATS_INITIAL_POS));
        cats.add(new DumbTom("Tom2", LevelGenerator.CATS_INITIAL_POS));

        physicsEngine = new PhysicsEngine();
        drawEngine = new DrawEngine(level.getMap());
        timer = new CustomTimer();
    }

    public void startGame()
    {
        timer.startTimer();
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                SecurityManager securityManager = new SecurityManager();
                securityManager.checkForImportGameManager("Cats");
                securityManager.checkForImportGameManager("Mouses");

                GameManager gameManager = new GameManager();
                gameManager.drawEngine.setVisible(true);
            }
        });
    }

    public void stopGame(String result, String characterName)
    {
        timer.stopTimer();

        switch (result)
        {
            case "Cat Win":
            case "Mouse Win":
                drawEngine.displayEndGameScreen(characterName + " Win !!");
                break;
            case "Cat Lose":
                drawEngine.displayEndGameScreen(characterName + " Ghouchaaaach fa9oulek ..");
            case "Everybody Lose":
                drawEngine.displayEndGameScreen("Losers .. losers everywhere ..");
                break;
            case "Mouses Win":
                drawEngine.displayEndGameScreen("Mouses Win !!");
                break;
            case "Cats Win":
                drawEngine.displayEndGameScreen("Cats Win !!");
                break;
        }
    }

    public void pauseGame()
    {
        timer.stopTimer();
    }

    public void resumeGame()
    {
        timer.resumeTimer();
    }

    public ArrayList<Cat> getCats()
    {
        return cats;
    }

    public ArrayList<Mouse> getMouses()
    {
        return mouses;
    }

    public PhysicsEngine getPhysicsEngine()
    {
        return physicsEngine;
    }

    public LevelGenerator getLevelGenerator()
    {
        return level;
    }

    public DrawEngine getDrawEngine()
    {
        return drawEngine;
    }

    public CustomTimer getTimer()
    {
        return timer;
    }
}
