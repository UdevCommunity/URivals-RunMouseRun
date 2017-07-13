package run_mouse_run;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;


public class CustomTimer
{
    private GameManager gameManager;
    private Time currentTime = new Time(0); // In milliseconds
    public static final int GAME_SPEED = 1000; // In milliseconds
    public static final int UPDATE_FREQUENCE = GAME_SPEED/4; // In milliseconds
    public static final int POWERUP_DURABILITY = 10000; // In milliseconds
    private final long TIME_LIMIT = 180000; // In milliseconds
    private Timer timer;
    private TimerTask task;


    public CustomTimer(GameManager gameManager)
    {
        this.gameManager = gameManager;

        task = createUpdateTask();
        timer = new Timer();
    }

    public TimerTask createUpdateTask()
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                currentTime.setTime(currentTime.getTime() + UPDATE_FREQUENCE);

                if (currentTime.getTime() > TIME_LIMIT)
                {
                    gameManager.stopGame("Everybody Lose", "");
                }

                gameManager.getPhysicsEngine().update();
                gameManager.getDrawEngine().update();
            }
        };
    }

    public void startTimer()
    {
        timer.scheduleAtFixedRate(task, 0, UPDATE_FREQUENCE);
    }
    public void stopTimer()
    {
        timer.cancel();
    }

    public void resumeTimer()
    {
        task = createUpdateTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, GAME_SPEED/4);
    }

    public Time getCurrentTime()
    {
        return currentTime;
    }
}

