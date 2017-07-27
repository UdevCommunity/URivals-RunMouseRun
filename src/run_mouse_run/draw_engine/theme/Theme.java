package run_mouse_run.draw_engine.theme;

import java.awt.*;

public class Theme
{

    // Font
    public static final Font FONT_DEFAULT  = new Font("Cambria", Font.PLAIN, 18);
    public static final Font FONT_DEFAULT_BOLD  = new Font("Cambria", Font.BOLD, 16);
    public static final Font FONT_DEFAULT_MEDIUM = new Font("Cambria", Font.BOLD, 18);
    public static final Font FONT_DEFAULT_LARGE = new Font("Cambria", Font.BOLD, 32);
    public static final Font FONT_DEFAULT_BIG = new Font("Cambria", Font.BOLD, 48);

    // Font Color
    public static final Color FONT_DEFAULT_COLOR = Color.WHITE;
    public static final Color FONT_INPUT_COLOR = Color.BLACK;

    // Layout
    public static final int TOP_BAR_HEIGHT = 35;
    public static final int LOG_BAR_HEIGHT = 30;
    public static final int BOTTOM_BAR_HEIGHT = 35 + LOG_BAR_HEIGHT;
    public static final int SETTING_PANE_MARGIN = 75;
    public static final int END_TOP_BAR_HEIGHT = 70;
    public static final int END_BOTTOM_BAR_HEIGHT = 40;

    // Color
    public static final String BG_FILE_NAME = null; //"background.jpg"; set to null to use BG_COLOR
    public static final Color BG_COLOR = new Color(34, 37, 46);
    public static final Color DEFAULT = new Color(34, 37, 46);
    public static final Color COLOR_UDEV_YELLOW = new Color(241,196,15);
    public static final Color COLOR_UDEV_BLUE = new Color(52, 152, 219);
    public static final Color COLOR_UDEV_RED = new Color(231, 76, 60);
    public static final Color COLOR_UDEV_GREEN = new Color(46, 204, 113);
    public static final Color COLOR_CLOUD = new Color(236, 240, 241);
    public static final Color COLOR_POMEGRANATE = new Color(192, 57, 43);
    public static final Color COLOR_BELIZE_HOLE = new Color(41, 128, 185);
    public static final Color COLOR_CARROT = new Color(230, 126, 34);
    public static final Color COLOR_WISTERIA = new Color(142, 68, 173);
    public static final Color COLOR_GREEN_SEA = new Color(22, 160, 133);
    public static final Color COLOR_EMERALD = new Color(46, 204, 113);
    public static final Color COLOR_TURQUOISE = new Color(26, 188, 156);
    public static final Color COLOR_PUMPKIN = new Color(211, 84, 10);

    // Buttons
    public static final Font BTN_DEFAULT_FONT = FONT_DEFAULT_BOLD;
    public static final Color BTN_DEFAULT_COLOR = COLOR_UDEV_GREEN;
    public static final Color BTN_DEFAULT_TEXT_COLOR = Color.WHITE;
    public static final int BTN_DEFAULT_HEIGHT = 28;
    public static final int BTN_DEFAULT_WIDTH = 150;
    public static final int BORDER_PLUS = 4;
    public static final int HOVER_PLUS = 8;

    // TextInputs
    public static final Font INPUT_TEXT_FONT = FONT_DEFAULT_MEDIUM;
    public static final int LABELED_MARGIN = 2;


    // Custom settings
    public static final boolean GAME_DESCRIPTION_IS_OPAQUE = false;
    public static final Color GAME_DESCRIPTION_BG = Color.WHITE;    // only if opaque
    public static final Font GAME_DESCRIPTION_FONT = FONT_DEFAULT_MEDIUM;
    public static final Color GAME_DESCRIPTION_FONT_COLOR = Color.WHITE;
    public static final Color GAME_DESCRIPTION_BORDER_COLOR = Color.WHITE;
}
