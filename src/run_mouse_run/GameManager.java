package run_mouse_run;

import java.awt.*;
import java.util.ArrayList;


public class GameManager /// TODO : access via static VS references
{
    private ArrayList<Cat> cats;
    private ArrayList<Mouse> mouses;

    private final LevelGenerator level;
    private final PhysicsEngine physicsEngine;
    private final DrawEngine frame;
    private final CustomTimer timer;

    private GameManager()
    {
        cats = new ArrayList<>();
        mouses = new ArrayList<>();

        level = new LevelGenerator(this);
        physicsEngine = new PhysicsEngine(this);
        frame = new DrawEngine(this, level.getMap());
        timer = new CustomTimer(this);
    }

    public void startGame()
    {
        // Instantiate mouses (do not use a loop, here we may instantiate mouses from different classes)
        mouses.add(new Mouse(this, "Jerry", LevelGenerator.MOUSES_INITIAL_POS));

        // Instantiate cats (do not use a loop, here we may instantiate cats from different classes)
        cats.add(new Cat(this, "Tom", LevelGenerator.CATS_INITIAL_POS));
        cats.add(new Cat(this, "Tom2", LevelGenerator.CATS_INITIAL_POS));

        // Check instantiated characters number
        //if(cats.size() != CATS_NUMBER || mouses.size() != MOUSES_NUMBER)
        // throw new Exception("Not enough characters instantiated");

        // Display game
        frame.setVisible(true); // add characters maps here ?

        // Start Timer
        timer.startTimer();
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                GameManager gameManager = new GameManager();
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
                frame.displayEndGameScreen(characterName + " Win !!");
                break;
            case "Everybody Lose":
                frame.displayEndGameScreen("Losers .. losers everywhere ..");
                break;
        }
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
        return frame;
    }
}
