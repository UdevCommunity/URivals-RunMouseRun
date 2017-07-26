package run_mouse_run.draw_engine.theme;

import java.awt.*;

public class Theme {

    // Font
    public static final Font FONT_DEFAULT  = new Font("Cambria", Font.PLAIN, 18);
    public static final Font FONT_DEFAULT_MEDIUM = new Font("Cambria", Font.BOLD, 20);
    public static final Font FONT_DEFAULT_LARGE = new Font("Cambria", Font.BOLD, 32);
    public static final Font FONT_DEFAULT_BIG = new Font("Cambria", Font.BOLD, 48);

    // Font Color
    public static final Color FONT_DEFAULT_COLOR = Color.BLACK;
    public static final Color FONT_INPUT_COLOR = Color.BLACK;

    // Layout
    public static final int TOP_BAR_HEIGHT = 30;
    public static final int LOG_BAR_HEIGHT = 30;
    public static final int BOTTOM_BAR_HEIGHT = 35 + LOG_BAR_HEIGHT;
    public static final int SETTING_PANE_MARGIN = 75;

    // Color
    public static final Color BG_COLOR = new Color(242, 242, 242);
    public static final Color DEFAULT = new Color(95, 179, 203);
    public static final Color COLOR_UDEV_YELLOW = new Color(241,196,15);
    public static final Color COLOR_UDEV_BLUE = new Color(52, 152, 219);
    public static final Color COLOR_UDEV_RED = new Color(231, 76, 60);
    public static final Color COLOR_UDEV_GREEN = new Color(46, 204, 113);

    // Buttons
    public static final Font BTN_DEFAULT_FONT = FONT_DEFAULT;
    public static final Color BTN_DEFAULT_COLOR = COLOR_UDEV_GREEN;
    public static final Color BTN_DEFAULT_TEXT_COLOR = Color.WHITE;
    public static final int BTN_DEFAULT_HEIGHT = 28;
    public static final int BTN_DEFAULT_WIDTH = 120;
    public static final int BORDER_PLUS = 4;
    public static final int HOVER_PLUS = 8;

    // TextInputs
    public static final int LABELED_MARGIN = 2;

}
