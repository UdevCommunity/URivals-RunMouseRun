package theme;

import javax.swing.*;
import java.awt.*;


public class BackgroundPanel extends JPanel {
    Image backgroundImage;

    public BackgroundPanel()
    {
        super();
        this.backgroundImage = null;
    }

    public BackgroundPanel(Color bgColor)
    {
        super();
        setBackground(bgColor);
        backgroundImage = null;
    }
    public BackgroundPanel(Image backgroundImage)
    {
        super();
        this.backgroundImage = backgroundImage;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(backgroundImage == null) return;

        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

    }
}
