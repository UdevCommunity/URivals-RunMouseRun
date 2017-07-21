package run_mouse_run.draw_engine;

/**
 */

import javax.swing.*;
import java.awt.*;

/**
 * Created by Oussama on 20/07/2017.
 * an ImageIcon with an alpha attribute
*/

public class TileImage extends ImageIcon
{
    private float alpha = 1;

    public TileImage(Image image) {
        super(image);
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y)
    {
        if(alpha != 1)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintIcon(c, g2, x, y);
            g2.dispose();
        }
        else
        {
            super.paintIcon(c, g, x, y);
        }
    }

    public void setAlpha(float alpha)
    {
        this.alpha = alpha;
    }

    public TileImage copy() {
        return new TileImage(getImage());
    }
}