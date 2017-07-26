package run_mouse_run.draw_engine.theme;

import java.awt.*;

public class Theme {

    // Font
    public static final Font FONT_DEFAULT  = new Font("Cambria", Font.PLAIN, 18);
    public static final Font FONT_DEFAULT_MEDIUM = new Font("Cambria", Font.BOLD, 20);
    public static final Font FONT_DEFAULT_LARGE = new Font("Cambria", Font.BOLD, 32);

    public static final Color FONT_DEFAULT_COLOR = new Color(0,0,0);

    // Layout
    public static final int TOP_BAR_HEIGHT = 30;
    public static final int LOG_BAR_HEIGHT = 30;
    public static final int BOTTOM_BAR_HEIGHT = 30 + LOG_BAR_HEIGHT;
    public static final int SETTING_PANE_MARGIN = 75;


    // Color
    public static final Color BG_COLOR = new Color(128,128,128);
    public static final Color DEFAULT = new Color(95, 179, 203);


    // Buttons
    public static final int BTN_DEFAULT_HEIGHT = 28;
    public static final int BTN_DEFAULT_WIDTH = 100;
    public static final Color BTN_DEFAULT_TEXT_COLOR = Color.BLACK;
    public static final int BORDER_PLUS = 4;
    public static final int HOVER_PLUS = 8;


    // LabeledTextFields
    public static final int LABELED_MARGIN = 2;

}
