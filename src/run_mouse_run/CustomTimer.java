package run_mouse_run;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;


public class CustomTimer
{
    private Time currentTime = new Time(0); // In milliseconds
    public static final int GAME_SPEED = 500; // In milliseconds
    public static final int UPDATE_FREQUENCE = GAME_SPEED/5; // In milliseconds
    private final long TIME_LIMIT = 180000; // In milliseconds
    private Timer timer;
    private TimerTask task;

    public CustomTimer()
    {
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
                    GameManager.gameManager.stopGame("Everybody Lose", "");
                }

                GameManager.gameManager.getPhysicsEngine().update();
                GameManager.gameManager.getDrawEngine().update();
            }
        };
    }

    public void startTimer()
    {
        timer.scheduleAtFixedRate(task, 0, UPDATE_FREQUENCE);

        for (Mouse mouse: GameManager.gameManager.getMouses())
            mouse.startTimer();

        for (Cat cat: GameManager.gameManager.getCats())
            cat.startTimer();
    }
    public void stopTimer()
    {
        timer.cancel();

        for (Mouse mouse: GameManager.gameManager.getMouses())
            mouse.stopTimer();

        for (Cat cat: GameManager.gameManager.getCats())
            cat.stopTimer();
    }

    public void resumeTimer()
    {
        task = createUpdateTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, UPDATE_FREQUENCE);

        for (Mouse mouse: GameManager.gameManager.getMouses())
            mouse.resumeTimer();

        for (Cat cat: GameManager.gameManager.getCats())
            cat.resumeTimer();
    }

    public Time getCurrentTime()
    {
        return currentTime;
    }
}

