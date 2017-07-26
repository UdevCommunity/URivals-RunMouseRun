package run_mouse_run;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import theme.*;

/**
 * Frame that displays one of the maps in the drawEngine's maps
 */
public class DE_Frame extends JFrame {

    public int TILE_SIZE = 48; // Tiles will resize to this value

    DrawEngine drawEngine;

    private JPanel contentPane;
    private JScrollPane mapContainerPanel;
    private DE_MapPanel mapPanel;
    private JLabel mapName, timeLabel;
    private JComboBox<String> mapsCmBox;
    public UButton startGameButton;
    private UButton btnDrawShortest;
    private JPanel topPanel, bottomPanel, gamePanel, controlPanel;
    private JLabel logLabel;

    /* For testing path finding */
    private Position initialPos = new Position(2, 2);
    private Position finalPos = new Position(6, 6);
    Color transColor = new Color(0, 0, 0, 0);


    private double mousePressedAtPosX;
    private double mousePressedAtPosY;
    private ArrayList<Boolean> drawCharPath = new ArrayList<>();


    /**
     * Create the frame.
     * adds Map
     * create buttons with ActionListeners :
     * ClickListener on panel ( for map editor )
     * ActionListeners on buttons
     * init cmCheckBox
     *
     * @param map initialised map from LevelGenerator
     */
    public DE_Frame(DrawEngine drawEngine, Map map) {
        this.drawEngine = drawEngine;

        if (map == null)
        {
            map = new Map("Blank map", LevelGenerator.MAP_WIDTH, LevelGenerator.MAP_HEIGHT, Tile.NOT_DISCOVERED);
        }

        // Set GUI
        initWindow();

        gamePanel = new JPanel();    // Panel containing the Map, and inGame informations
        gamePanel.setLayout(new BorderLayout(5, 5));

        controlPanel = new JPanel(); // Panel containing options ( map with/height ..etc)

		initTopPanel();

        addMapContainerPanel(map, TILE_SIZE);

		initBottomPanel();

		initSettingPanel();

        /*Panels initialised, add to frame */
        contentPane.add(gamePanel);
        contentPane.add(controlPanel);


        SetTransparency();
        /*Over, clean spaces and resize*/
        pack();
    } // End of constructor

    private void SetTransparency()
    {
        controlPanel.setOpaque(false);
        gamePanel.setOpaque(false);
        topPanel.setOpaque(false);
        bottomPanel.setOpaque(false);
    }

    /**
     *
     */
    private void initTopPanel()
    {
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(0, 0));
        topPanel.setPreferredSize(new Dimension(topPanel.getWidth(), Theme.TOP_BAR_HEIGHT));

        mapName = new JLabel("Level Map");
        mapName.setFont(Theme.FONT_DEFAULT);

        mapName.setForeground(Theme.FONT_DEFAULT_COLOR);
        // Start game button
        JPanel startButtonPanel = new JPanel();

        startGameButton = new UButton("Start Game");

        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startGameButton.getText().equals("Start Game"))
                {
                    drawEngine.startGame();

                } else if (startGameButton.getText().equals("Pause Game"))
                {
                    drawEngine.pauseGame();
                } else
                {
                    drawEngine.resumeGame();
                }

            }
        });
        startButtonPanel.setOpaque(false);
        startButtonPanel.add(startGameButton);

        // Time Label
        timeLabel = new JLabel("Time : 00:00");
        timeLabel.setFont(Theme.FONT_DEFAULT);
        timeLabel.setForeground(Theme.FONT_DEFAULT_COLOR);

        topPanel.add(mapName, BorderLayout.WEST);
        topPanel.add(startButtonPanel, BorderLayout.CENTER);
        topPanel.add(timeLabel, BorderLayout.EAST);


        gamePanel.add(topPanel, BorderLayout.NORTH);
    }

    /**
     *
     */
    private void initSettingPanel()
    {
       // Logo
        JLabel logoUDEv = null;
        try
        {
            logoUDEv = new JLabel(
                    new ImageIcon(ImageIO.read(new File("res/udev-logo.png")))
            );
        } catch (IOException e)
        {
            logoUDEv = new JLabel("UDev");
            System.err.println("Couldn't load UDev Logo file");
        }

        JLabel lblGameName = new JLabel(
                "<html>" +
                        "<center>" +
                            "URivals 1.0" +
                        "</center>" +
                    //    "<br>" +
                        "Run Mouse Run" +
                    "<html>"
        );

        // TextArea
        JLabel txtAreaGame = new JLabel();
        txtAreaGame.setText(
                "<html><b>How To Play :</b><br>" +
                "- Create your bot.<br>" +
                "- Grab Popcorn.<br>" +
                "- Click Start Game.<br>" +
                "- Enjoy.</html>");

        // GameMode setting
        JLabel lblGameMode = new JLabel("Game Mode");
        lblGameMode.setFont(Theme.FONT_DEFAULT);
        lblGameMode.setForeground(Theme.FONT_DEFAULT_COLOR);

        JComboBox<String> cmboxGameMode = new JComboBox<>();
        cmboxGameMode.setFont(Theme.FONT_DEFAULT);
        cmboxGameMode.setForeground(Theme.FONT_DEFAULT_COLOR);

        for(GameMode mode : GameMode.values())
            cmboxGameMode.addItem(mode.toString());

        cmboxGameMode.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                GameManager.gameManager.setGameMode(
                        GameMode.valueOf(cmboxGameMode.getSelectedItem().toString())
                );
            }
        });

        cmboxGameMode.setSelectedItem(GameManager.gameManager.getGameMode().toString());

        // Time Limit
        JLabel lblTimeLimit = new JLabel("Time Limit (s) : ");
        JTextField txtTimeLimit = new JTextField("180");

        lblTimeLimit.setFont(Theme.FONT_DEFAULT);
        lblTimeLimit.setForeground(Theme.FONT_DEFAULT_COLOR);
        txtTimeLimit.setFont(Theme.FONT_DEFAULT);
        txtTimeLimit.setForeground(Theme.FONT_DEFAULT_COLOR);

        UButton btnTimeLimit = new UButton("Set");

        btnTimeLimit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int x = Integer.parseInt(txtTimeLimit.getText());

                    if(x > 0)
                    {
                        GameManager.gameManager.getTimer().setTimeLimit(x);
                    }
                    else
                    {
                        txtTimeLimit.setText("180");
                        GameManager.gameManager.getTimer().setTimeLimit(180);
                    }
                }catch (NumberFormatException ex)
                {
                    txtTimeLimit.setText("180");
                    GameManager.gameManager.getTimer().setTimeLimit(180);
                }
            }
        });

        // Game Speed
        JLabel lblGameSpeed = new JLabel("Game Speed (%) : ");
        JTextField txtGameSpeed = new JTextField("100");

        lblGameSpeed.setFont(Theme.FONT_DEFAULT);
        lblGameSpeed.setForeground(Theme.FONT_DEFAULT_COLOR);
        txtGameSpeed.setFont(Theme.FONT_DEFAULT);
        txtGameSpeed.setForeground(Theme.FONT_DEFAULT_COLOR);

        UButton btnGameSpeed = new UButton("Set");

        btnGameSpeed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int x = Integer.parseInt(txtGameSpeed.getText());

                    if(x > 0 && x < 300)
                    {
                        CustomTimer.setGameSpeed(x);
                    }
                    else
                    {
                        txtGameSpeed.setText("100");
                        CustomTimer.setGameSpeed(100);
                    }
                }catch (NumberFormatException ex)
                {
                    txtGameSpeed.setText("100");
                    CustomTimer.setGameSpeed(100);
                }
            }
        });

        // Level settings
        JLabel lblMapWidth = new JLabel("Map Width");
        lblMapWidth.setFont(Theme.FONT_DEFAULT);
        lblMapWidth.setForeground(Theme.FONT_DEFAULT_COLOR);
        JLabel lblMapHeight = new JLabel("Map Height");
        lblMapHeight.setFont(Theme.FONT_DEFAULT);
        lblMapHeight.setForeground(Theme.FONT_DEFAULT_COLOR);

        JTextField txtMapWidth = new JTextField("" + LevelGenerator.MAP_WIDTH);
        txtMapWidth.setFont(Theme.FONT_DEFAULT);
        txtMapWidth.setForeground(Theme.FONT_DEFAULT_COLOR);
        JTextField txtMapHeight = new JTextField("" + LevelGenerator.MAP_HEIGHT);
        txtMapHeight.setFont(Theme.FONT_DEFAULT);
        txtMapHeight.setForeground(Theme.FONT_DEFAULT_COLOR);

        UButton btnNewLevel = new UButton("New Level");

        btnNewLevel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    int w = Integer.parseInt(txtMapWidth.getText());
                    int h = Integer.parseInt(txtMapHeight.getText());

                    drawEngine.createNewLevel(w, h);

                    update();
                } catch (Exception ex)
                {
                    System.err.println("Error " + ex.getMessage());
                }
            }
        });

        /* A bit complicated/hacked, set each checkbox to change in the boolean list*/

        ArrayList<JCheckBox> chkDrawPath = new ArrayList<>();
        ArrayList<JLabel> lblchckPath = new ArrayList<>();

        for(Mouse mouse : drawEngine.getMouses())
        {
            drawCharPath.add(false);

            JLabel lbl = new JLabel("Draw "+mouse.getName()+" path");
            lbl.setFont(Theme.FONT_DEFAULT);
            lbl.setForeground(Theme.FONT_DEFAULT_COLOR);
            lblchckPath.add(lbl);

            JCheckBox chk = new JCheckBox();
            chk.setOpaque(false);

            chk.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    //drawCharPath.set(index, chk.isSelected());
                    int i = 0;
                    for(Mouse m : drawEngine.getMouses())
                    {
                        if (m == mouse)
                        {
                            drawCharPath.set(i, chk.isSelected());
                        }
                        i++;
                    }
                }
            });

            chkDrawPath.add(chk);
        }

        for(Cat cat : drawEngine.getCats())
        {
            drawCharPath.add(false);

            JLabel lbl = new JLabel("Draw "+cat.getName()+" path");
            lbl.setFont(Theme.FONT_DEFAULT);
            lbl.setForeground(Theme.FONT_DEFAULT_COLOR);
            lblchckPath.add(lbl);

            JCheckBox chk = new JCheckBox();
            chk.setOpaque(false);

            chk.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    //drawCharPath.set(index, chk.isSelected());
                    int i = drawEngine.getMouses().size();
                    for(Cat c : drawEngine.getCats())
                    {
                        if (c == cat)
                        {
                            drawCharPath.set(i, chk.isSelected());
                        }
                        i++;
                    }
                }
            });

            chkDrawPath.add(chk);
        }

        /*===========================================================================*/
        // Brace Yourself .... Crazy Layouts coming xD
        /*===========================================================================*/

        JPanel upperPanel = new JPanel();
        JPanel lowerPanel = new JPanel();

        upperPanel.setOpaque(false);
        lowerPanel.setOpaque(false);

        /*First : set upper Panel */
        upperPanel.setLayout(new GridLayout(3, 1, 0, 0));
        upperPanel.setBorder(new EmptyBorder(20,50,5,50));
        // add logo
        upperPanel.add(logoUDEv, BorderLayout.NORTH);

        // add game Name
        lblGameName.setFont(Theme.FONT_DEFAULT_LARGE);
        lblGameName.setForeground(Theme.FONT_DEFAULT_COLOR);
        lblGameName.setHorizontalAlignment(JLabel.CENTER);
        upperPanel.add(lblGameName);

        // set how to play panel
        txtAreaGame.setFont(Theme.FONT_DEFAULT_MEDIUM);
        txtAreaGame.setForeground(Theme.FONT_DEFAULT_COLOR);
        txtAreaGame.setBorder(new EmptyBorder(0,50,0,50));

        JScrollPane txtGamePanel = new JScrollPane(txtAreaGame);
        txtGamePanel.setBorder(new LineBorder(Color.black, 1, true));
        txtGamePanel.setSize(new Dimension(400, 400));

        // add how to play panel
        txtGamePanel.setOpaque(false);
        upperPanel.add(txtGamePanel, BorderLayout.CENTER);

        /*Second : set lower Panel*/
        // Init GridBag Layout
        lowerPanel.setLayout(new GridBagLayout());

        //Objet to constraint componants
        GridBagConstraints gbc = new GridBagConstraints();

        // set Panel margin
        lowerPanel.setBorder(new EmptyBorder(20, 50, 5, 50));

        // set padding
        gbc.insets = new Insets(5, 5, 10, 5);

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL; // stretch when componant too small

        // GameMode setting
        gbc.gridx = 0;      gbc.gridy = 0;
        gbc.gridheight = 1; gbc.gridwidth = 2;
        gbc.weightx = 50;
        lowerPanel.add(lblGameMode, gbc);

        gbc.gridx = 2;      gbc.gridy = 0;
        gbc.gridheight = 1; gbc.gridwidth = 2;
        gbc.weightx = 50;
        lowerPanel.add(cmboxGameMode, gbc);

        // timeLimit
        gbc.gridx = 0;      gbc.gridy = 1;
        gbc.gridheight = 1; gbc.gridwidth = 2;
        lowerPanel.add(lblTimeLimit, gbc);

        gbc.gridx = 2;      gbc.gridy = 1;
        gbc.gridheight = 1; gbc.gridwidth = 1;
        lowerPanel.add(txtTimeLimit, gbc);

        gbc.gridx = 3;      gbc.gridy = 1;
        gbc.gridheight = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        lowerPanel.add(btnTimeLimit, gbc);

        // gameSpeed
        gbc.gridx = 0;      gbc.gridy = 2;
        gbc.gridheight = 1; gbc.gridwidth = 2;
        lowerPanel.add(lblGameSpeed, gbc);

        gbc.gridx = 2;      gbc.gridy = 2;
        gbc.gridheight = 1; gbc.gridwidth = 1;
        lowerPanel.add(txtGameSpeed, gbc);

        gbc.gridx = 3;      gbc.gridy = 2;
        gbc.gridheight = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        lowerPanel.add(btnGameSpeed, gbc);

        // MapLabels
        gbc.gridx = 0;      gbc.gridy = 3;
        gbc.gridheight = 1; gbc.gridwidth = 2;
        lowerPanel.add(lblMapWidth, gbc);

        gbc.gridx = 2;      gbc.gridy = 3;
        gbc.gridheight = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        lowerPanel.add(lblMapHeight, gbc);

        // Map dimensions text
        gbc.gridx = 0;      gbc.gridy = 4;
        gbc.gridheight = 1; gbc.gridwidth = 2;
        lowerPanel.add(txtMapWidth, gbc);

        gbc.gridx = 2;      gbc.gridy = 4;
        gbc.gridheight = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        lowerPanel.add(txtMapHeight, gbc);

        // New Level Button
        gbc.gridx = 0;      gbc.gridy = 5;
        gbc.gridheight = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        lowerPanel.add(btnNewLevel, gbc);

        // setCheckBoxes Panel
        JPanel chckBoxesPanel = new JPanel();
        chckBoxesPanel.setBorder(new LineBorder(Color.BLACK, 1, true));
        chckBoxesPanel.setLayout(new GridBagLayout());

        for(int i = 0; i < chkDrawPath.size(); i++)
        {
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;      gbc.gridy = i;
            gbc.gridheight = 1; gbc.gridwidth = 1;
            gbc.weightx = 20;
            chckBoxesPanel.add(chkDrawPath.get(i), gbc);

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 1;      gbc.gridy = i;
            gbc.gridheight = 1; gbc.gridwidth = 3;
            gbc.weightx = 80;
            chckBoxesPanel.add(lblchckPath.get(i), gbc);
        }

        // add CheckBoxes Panel
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;      gbc.gridy = 6;
        gbc.gridheight = GridBagConstraints.REMAINDER; gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 100;
        chckBoxesPanel.setOpaque(false);
        lowerPanel.add(chckBoxesPanel, gbc);

        // add to controlPanel
        controlPanel.setLayout(new GridLayout(2,1));
        controlPanel.setBorder(new EmptyBorder(10, Theme.SETTING_PANE_MARGIN,5,Theme.SETTING_PANE_MARGIN));
        controlPanel.add(upperPanel);
        JScrollPane lowerScrollPane = new JScrollPane(lowerPanel);
        lowerScrollPane.setBorder(null);
        lowerScrollPane.setOpaque(false);
        lowerScrollPane.getViewport().setOpaque(false);
        controlPanel.add(lowerScrollPane);
    }

    /**
     *
     */
    private void initBottomPanel()
    {
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout(5, 5));
        bottomPanel.setPreferredSize(new Dimension(bottomPanel.getWidth(), Theme.BOTTOM_BAR_HEIGHT));

        // Draw Shortest Button
        btnDrawShortest = new UButton("Draw shortest");
        btnDrawShortest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });

        // Center buttons ( ComboBox )
        JPanel centerPanel = new JPanel();

        centerPanel.setOpaque(false);
        mapsCmBox = new JComboBox<>();

        mapsCmBox.setPreferredSize(new Dimension(120, 20));
        mapsCmBox.setFont(Theme.FONT_DEFAULT);
        mapsCmBox.setForeground(Theme.FONT_DEFAULT_COLOR);

        updateCmBox();

        mapsCmBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                try
                {
                    int i = mapsCmBox.getSelectedIndex();

                    switchToMap(drawEngine.getMaps().get(i));
                } catch (Exception ex)
                {
                    // do nothing
                }
            }
        });
        centerPanel.add(mapsCmBox);

        // new Map button
        UButton newMapButton = new UButton("New Window");
        newMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawEngine.addNewFrame(drawEngine.getMaps());
            }
        });

        // log label
        logLabel = new JLabel("Here we print current events like when a cat hits a mine");

        logLabel.setFont(Theme.FONT_DEFAULT);
        logLabel.setForeground(Theme.FONT_DEFAULT_COLOR);
        logLabel.setHorizontalAlignment(JLabel.CENTER);
        logLabel.setPreferredSize(new Dimension(logLabel.getWidth(), Theme.LOG_BAR_HEIGHT));

        // Add to frame
        bottomPanel.add(btnDrawShortest, BorderLayout.WEST);
        bottomPanel.add(centerPanel, BorderLayout.CENTER);
        bottomPanel.add(newMapButton, BorderLayout.EAST);
        bottomPanel.add(logLabel, BorderLayout.SOUTH);

        gamePanel.add(bottomPanel, BorderLayout.SOUTH);
    }



    /**
     * Inits DrawEngine frame adapting to height
     */
    private void initWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Run Mouse Run!");

        // Set on FullScreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        //setResizable(false);
        try
        {
            File bgFile = new File("res/background.jpg");
            BufferedImage backgroundImage = ImageIO.read(bgFile); // TODO: Resize Background
            contentPane = new BackgroundPanel(backgroundImage);
        } catch(Exception e)
        {
            System.err.println("Couldn't load background file");
            contentPane = new BackgroundPanel(Theme.BG_COLOR);
        }

        contentPane.setBorder(new EmptyBorder(5, 10, 5, 10));
        contentPane.setLayout(new GridLayout(1, 2, 5, 0));
        setContentPane(contentPane);
    }

    /**
     * add a mapContainerPanel to gamePanel with MouseListener
     * @param map Map
     */
    private void addMapContainerPanel(Map map, int TILE_SIZE) {
        mapPanel = new DE_MapPanel(drawEngine, map, TILE_SIZE);
        mapContainerPanel = new JScrollPane(mapPanel);
        mapContainerPanel.setWheelScrollingEnabled(false);

        setMouseListener();

        // Speed up scrolling
        mapContainerPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mapContainerPanel.getVerticalScrollBar().setUnitIncrement(TILE_SIZE / 2);
        mapContainerPanel.getHorizontalScrollBar().setUnitIncrement(TILE_SIZE / 2);

        gamePanel.add(mapContainerPanel, BorderLayout.CENTER);
    }

    private void setMouseListener()
    {
        mapPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e)
            {
                mousePressedAtPosX = e.getPoint().getX();
                mousePressedAtPosY = e.getPoint().getY();
            }

            /**
             * On LeftClick : Set InitialPosition ( testing pathfinding )
             * On RightClick : Set finalPosition ( testing pathfinding )
             * On MiddleClick : switch tile ( edit map )
             */
            @Override
            public void mouseClicked(MouseEvent event) {
                int i = event.getY() / TILE_SIZE;
                int j = event.getX() / TILE_SIZE;

                // if game not running
                if (!startGameButton.getText().equals("Pause Game"))
                {
                    if (event.getButton() == MouseEvent.BUTTON1)
                    {
                        initialPos.setPosX(j);
                        initialPos.setPosY(i);
                        mapPanel.update();
                    } else if (event.getButton() == MouseEvent.BUTTON3)
                    {
                        finalPos.setPosX(j);
                        finalPos.setPosY(i);
                        mapPanel.update();
                    } else if (event.getButton() == MouseEvent.BUTTON2)
                    {
                        do
                        {
                            drawEngine.getMaps().get(0).switchTile(j, i);
                        }while(drawEngine.getMaps().get(0).getTile(j, i) == Tile.CAT
                                || drawEngine.getMaps().get(0).getTile(j, i) == Tile.MOUSE);

                        update();
                    }
                }
            }
        });

        // Set wheel listener for zoom in/out
        mapContainerPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0)
                {
                    // ZOOM IN
                    if (TILE_SIZE <= 120)
                        adjustTileSize(TILE_SIZE + 8);
                } else
                {
                    if (TILE_SIZE >= 24)
                        adjustTileSize(TILE_SIZE - 8);
                }
            }
        });

        // set Drag listener for moving scroll
        mapPanel.addMouseMotionListener(new MouseMotionListener()
        {
            @Override
            public void mouseDragged(MouseEvent e)
            {
                double mapContainerPanelFocusPositionX = mapContainerPanel.getViewport().getViewPosition().getX() + mapContainerPanel.getWidth()/2;
                double mapContainerPanelFocusPositionY = mapContainerPanel.getViewport().getViewPosition().getY() + mapContainerPanel.getHeight()/2;

                int targetPositionX = (int)(mapContainerPanelFocusPositionX + (mousePressedAtPosX - e.getPoint().getX()))/TILE_SIZE;
                int targetPositionY = (int)(mapContainerPanelFocusPositionY + (mousePressedAtPosY - e.getPoint().getY()))/TILE_SIZE;

                centerScroll(new Position(targetPositionX, targetPositionY));
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
    }


    /**
     * Change window state ( Before Start/Running/Paused )
     * State given from button text
     *
     * @param buttonText (String) Current Button Text
     */
    public void changeState(String buttonText) {
        if (buttonText.equals("Start Game"))// RUN GAME FROM START
        {
            startGameButton.setText("Pause Game");
            btnDrawShortest.setEnabled(false);
            hideControlPanel();
        } else if (buttonText.equals("Pause Game"))// PAUSE GAME
        {
            startGameButton.setText("Resume Game");
            btnDrawShortest.setEnabled(true);
        } else // RUN GAME FROM PAUSED
        {
            startGameButton.setText("Pause Game");
            btnDrawShortest.setEnabled(false);
        }
        update();
    }

    /**
     * Remove Control Panel from contentPane
     */
    public void hideControlPanel() {
        controlPanel.setVisible(false);
        contentPane.remove(controlPanel);
        contentPane.invalidate();

        adjustTileSize(TILE_SIZE);
    }

    /**
     *
     */
    private void updateCmBox() {
        if (mapsCmBox == null) return;

        mapsCmBox.removeAllItems();
        for (Map m : drawEngine.getMaps())
        {
            mapsCmBox.addItem(m.getName());
        }
    }

    /**
     * Adjust Tile size to fit all the screen
     *
     * @param size (int)
     */
    private void adjustTileSize(int size) {
        TILE_SIZE = size;

        mapPanel.changeTileSize(size);

        update();
    }

    public void changeMapSize(Map map)
    {
        gamePanel.remove(mapContainerPanel);
        addMapContainerPanel(map, TILE_SIZE);

        revalidate();
        repaint();
    }

    /**
     * Set map to show in mapPanel
     * @param map map to show
     */
    private void switchToMap(Map map) {
        mapPanel.setMap(map);
        mapName.setText(map.getName());
        update();
        mapPanel.adjustPanelSize();
        update();
    }


    /**
     * Convert time to mm:ss format and show on timeLabel
     *
     * @param currentTime (float) current time in miliseconds
     */
    private void updateTime(String currentTime) {
        timeLabel.setText("Time : " + currentTime.substring(3));
    }

    /**
     * A little hard coded, know which character to follow depending on map
     */
    private void updateScroll() {

        int index = mapsCmBox.getSelectedIndex();
        if (index > 0) // if not levelMap
        {
            if (index < drawEngine.getMouses().size() + 1)
            {
                // Mouse
                centerScroll(
                        drawEngine.getMouses().get(index - 1)
                                .getPosition()
                );
            } else if (index < drawEngine.getCats().size()
                    + drawEngine.getMouses().size() + 1)
            {
                // Cat
                centerScroll(
                        drawEngine.getCats()
                                .get(index - drawEngine.getMouses().size() - 1)
                                .getPosition()
                );
            }
        }
    }

    /**
     * Calculate and set position of scrol View port (mapContainerPanel)
     * se that the given p is in center
     *
     * @param p (Position)
     */
    private void centerScroll(Position p)
    {
		/*Get width (Vw) and height (Vh) of viewport*/
        double vw = mapContainerPanel.getWidth();
        double vh = mapContainerPanel.getHeight();

        int vx = (int) (p.getPosX() * TILE_SIZE - vw / 2);
        int vy = (int) (p.getPosY() * TILE_SIZE - vh / 2);

        // check for bounds
        int maxX = (int) (drawEngine.getMaps().get(mapsCmBox.getSelectedIndex()).getWidth() * TILE_SIZE - vw);
        int maxY = (int) (drawEngine.getMaps().get(mapsCmBox.getSelectedIndex()).getHeight() * TILE_SIZE - vh);
        maxX = (maxX >= 0) ? maxX : 0;
        maxY = (maxY >= 0) ? maxY : 0;

        vx = (vx >= 0) ? vx : 0;
        vy = (vy >= 0) ? vy : 0;
        vx = (vx > maxX+1) ? maxX : vx;
        vy = (vy > maxY+1) ? maxY : vy;

        mapContainerPanel.getViewport().setViewPosition(new Point(vx, vy));
    }

    /**
     * Refresh map ( draw level, characters and objects )
     * Draw Cat and mouse DestinationPath
     */
    public void update() {
        mapPanel.setMap(drawEngine.getMaps().get(mapsCmBox.getSelectedIndex()));
        mapPanel.update();
        updateTime(drawEngine.getTimer().getCurrentTime().toString());
        updateScroll();

        mapPanel.drawCharacterPaths(drawCharPath);

        contentPane.repaint();
    }

    public void printLog(String message)
    {
        logLabel.setText(message);
    }

    public void displayEndGameScreen(String result) {
        JPanel topEndGamePanel = new JPanel();
        JPanel bottomEndGamePanel = new JPanel();

        JLabel lblResult = new JLabel(result);
        lblResult.setFont(new Font("Cambria", Font.PLAIN, 48));
        lblResult.setForeground(Theme.FONT_DEFAULT_COLOR);

        UButton playAgainButton = new UButton("PlayAgain");

        playAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                drawEngine.startNewGame();

            }
        });

        bottomEndGamePanel.add(playAgainButton);
        topEndGamePanel.add(lblResult);

        // Show and udpdate screen
        topEndGamePanel.setVisible(false);
        bottomEndGamePanel.setVisible(false);
        gamePanel.add(topEndGamePanel, BorderLayout.NORTH);
        gamePanel.add(bottomEndGamePanel, BorderLayout.SOUTH);
        gamePanel.remove(topPanel);
        gamePanel.remove(bottomPanel);
        topEndGamePanel.setVisible(true);
        bottomEndGamePanel.setVisible(true);
    }

    public void explodeMine(int x, int y) {
        mapPanel.createAnimation(DE_GameSprites.EXPLOSION_FRAMES, x, y);
    }

}
