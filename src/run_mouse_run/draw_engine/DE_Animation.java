package run_mouse_run.draw_engine;

import javax.swing.*;
import java.util.ArrayList;

/**
 *
 */

public class DE_Animation
{
    JLabel tile;
    private ArrayList<DE_TileImage> frames;
    private int currentIndex;

    public DE_Animation(JLabel tile, ArrayList<DE_TileImage> frames)
    {
        this.tile = tile;
        setAnimation(frames);
    }

    public void setAnimation(ArrayList<DE_TileImage> frames)
    {
        this.frames = frames;
        if(frames != null && !frames.isEmpty())
        {
            currentIndex = 0;
        }
    }

    public void play()
    {
        Thread animThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isOver())
                {
                    draw();
                    try
                    {
                        Thread.sleep(50);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    nextFrame();
                    if (isOver())
                        tile.setIcon(null);
                }
            }
        });
        animThread.start();
    }
    public void nextFrame()
    {
        currentIndex++;
        if(frames == null || currentIndex >= frames.size())
            frames = null;
    }

    public void draw()
    {
        if(frames == null)
        {
            return;
        }
        tile.setIcon(frames.get(currentIndex));
    }

    public boolean isOver() {
        return (frames == null);
    }
}
