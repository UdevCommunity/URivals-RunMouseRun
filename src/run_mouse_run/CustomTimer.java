package run_mouse_run;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;


class CustomTimer
{
    static int GAME_SPEED = 700;
    private static int UPDATE_FREQUENCY = GAME_SPEED * 10/100;
    private long TIME_LIMIT = 450000;

    private Time currentTime = new Time(0);
    private Timer timer;

    private TimerTask createUpdateTask()
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                currentTime.setTime(currentTime.getTime() + UPDATE_FREQUENCY);

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
        timer.scheduleAtFixedRate(task, 0, UPDATE_FREQUENCY);

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
        GAME_SPEED /= (speed/100.0);
        UPDATE_FREQUENCY = GAME_SPEED * 10/100;

        for (Mouse mouse: GameManager.gameManager.getMouses())
            mouse.setUpdateFrequency(GAME_SPEED);

        for (Cat cat: GameManager.gameManager.getCats())
            cat.setUpdateFrequency(GAME_SPEED);
    }
    Time getCurrentTime()
    {
        return currentTime;
    }
}

