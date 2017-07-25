package run_mouse_run;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;


class CustomTimer
{
    static int GAME_SPEED = 700;
    static int UPDATE_FREQUENCE = GAME_SPEED/8; // should be changed in setter too
    private long TIME_LIMIT = 300000;

    private Time currentTime = new Time(0);
    private Timer timer;

    private TimerTask createUpdateTask()
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                currentTime.setTime(currentTime.getTime() + UPDATE_FREQUENCE);

                if (currentTime.getTime() > TIME_LIMIT)
                {
                    GameManager.gameManager.stopGame("Losers .. losers everywhere ..");
                }

                GameManager.gameManager.getPhysicsEngine().update();
                GameManager.gameManager.getDrawEngine().update();
            }
        };
    }

    void startTimer()
    {
        TimerTask task = createUpdateTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, UPDATE_FREQUENCE);

        for (Mouse mouse: GameManager.gameManager.getMouses())
            mouse.startTimer();

        for (Cat cat: GameManager.gameManager.getCats())
            cat.startTimer();
    }

    void stopTimer()
    {
        timer.cancel();

        for (Mouse mouse: GameManager.gameManager.getMouses())
            mouse.stopTimer();

        for (Cat cat: GameManager.gameManager.getCats())
            cat.stopTimer();
    }

    void setTimeLimit(int sec)
    {
        this.TIME_LIMIT = sec*1000;
    }

    static void setGameSpeed(int speed)
    {
        GAME_SPEED = speed;
        UPDATE_FREQUENCE = GAME_SPEED/8;
    }
    Time getCurrentTime()
    {
        return currentTime;
    }
    Timer getTimer()
    {
        return timer;
    }
}

