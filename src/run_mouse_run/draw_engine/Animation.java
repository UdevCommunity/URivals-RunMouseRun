package run_mouse_run.draw_engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Oussama on 20/07/2017.
 */

public class Animation
{
    Graphics2D graphic;
    private ArrayList<BufferedImage> frames;
    private int currentIndex;

    private int centerX, centerY;

    public Animation(Graphics g)
    {
        this.graphic = (Graphics2D) g;
        graphic.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
    }

    public void setAnimation(ArrayList<BufferedImage> frames, int centerX, int centerY)
    {
        this.frames = frames;
        if(frames != null && !frames.isEmpty())
        {
            currentIndex = 0;
            this.centerX = centerX;
            this.centerY = centerY;
        }
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

        int x = centerX - frames.get(currentIndex).getWidth()/2;
        int y = centerY - frames.get(currentIndex).getHeight()/2;

        graphic.drawImage(frames.get(currentIndex), x, y, null);
    }

    public boolean isOver() {
        return (frames == null);
    }
}
