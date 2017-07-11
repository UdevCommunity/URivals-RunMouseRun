package run_mouse_run;

import java.awt.*;
import java.util.ArrayList;


public class GameManager /// TODO : access via static VS references
{
    public static final int CATS_NUMBER = 2;
    public static final int MOUSES_NUMBER = 1;

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

        level = new LevelGenerator();
        physicsEngine = new PhysicsEngine(this);
        frame = new DrawEngine(this, level.getMap());
        timer = new CustomTimer(this);
    }

    private void startGame() throws Exception
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
        frame.update();

        // Start Timer
        timer.startTimer();
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    GameManager gameManager = new GameManager();
                    gameManager.startGame();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }


    public void stopGame(String result, String characterName) throws Exception
    {
        timer.stopTimer();

        switch (result)
        {
            case "Mouse Win":
                break;
            case "Everybody Lose": System.out.print("Looooser");
                break;
            case "Cat Win":
                break;

            default:
                throw new Exception("Wrong end game argument");
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

}
