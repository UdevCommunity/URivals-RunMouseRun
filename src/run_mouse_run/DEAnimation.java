package run_mouse_run;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 */

public class DEAnimation
{
    JLabel tile;
    private ArrayList<DETileImage> frames;
    private int currentIndex;

    public DEAnimation(JLabel tile, ArrayList<DETileImage> frames)
    {
        this.tile = tile;
        setAnimation(frames);
    }

    public void setAnimation(ArrayList<DETileImage> frames)
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
