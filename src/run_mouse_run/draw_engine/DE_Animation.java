package run_mouse_run.draw_engine;

import javax.swing.*;
import java.util.ArrayList;


class DE_Animation
{
    private JLabel tile;
    private ArrayList<DE_TileImage> frames;
    private int currentIndex;

    DE_Animation(JLabel tile, ArrayList<DE_TileImage> frames)
    {
        this.tile = tile;
        setAnimation(frames);
    }

    private void setAnimation(ArrayList<DE_TileImage> frames)
    {
        this.frames = frames;
        if(frames != null && !frames.isEmpty())
            currentIndex = 0;
    }

    void play()
    {
        Thread animThread = new Thread(() ->
        {
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
        });
        animThread.start();
    }
    private void nextFrame()
    {
        currentIndex++;
        if(frames == null || currentIndex >= frames.size())
            frames = null;
    }

    private void draw()
    {
        if(frames == null)
        {
            return;
        }
        tile.setIcon(frames.get(currentIndex));
    }

    private boolean isOver() {
        return (frames == null);
    }
}
