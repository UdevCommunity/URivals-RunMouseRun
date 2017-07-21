package run_mouse_run;

import javafx.scene.input.KeyCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Frame that displays one of the maps in the drawEngine's maps
 */
public class DE_Frame extends JFrame {

    public int TILE_SIZE = 48; // Tiles will resize to this value

    public Font defaultFont;

    DrawEngine drawEngine;

    private JPanel contentPane;
    private JScrollPane mapContainerPanel;
    private DE_MapPanel mapPanel;
    private JLabel mapName, timeLabel;
    private JComboBox<String> mapsCmBox;
    public JButton startGameButton;
    private JButton btnDrawShortest;
    private JPanel topPanel, bottomPanel, gamePanel, controlPanel;

    /* For testing path finding */
    private Position initialPos = new Position(2, 2);
    private Position finalPos = new Position(6, 6);

    private boolean ctrlPressed = false;

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

        defaultFont = new Font("Calibri", Font.PLAIN, 18);

        gamePanel = new JPanel();    // Panel containing the Map, and inGame informations
        gamePanel.setLayout(new BorderLayout(5, 5));

        controlPanel = new JPanel(); // Panel containing options ( map with/height ..etc)
        controlPanel.setLayout(new BorderLayout(5, 5));

		initTopPanel();

        addMapContainerPanel(map, TILE_SIZE);

		initBottomPanel();

		initSettingPanel();

        /*Panels initialised, add to frame */
        contentPane.add(gamePanel);//, BorderLayout.WEST);
        contentPane.add(controlPanel);//, BorderLayout.EAST);


        /*Over, clean spaces and resize*/
        pack();
        setListeners();
    } // End of constructor

    /**
     *
     */
    private void initTopPanel()
    {
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(0, 0));

        mapName = new JLabel("Level Map");
        mapName.setFont(defaultFont);

        // Start game button
        JPanel startButtonPanel = new JPanel();
        startGameButton = new JButton("Start Game");

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

        startButtonPanel.add(startGameButton);

        // Time Label
        timeLabel = new JLabel("Time : 00:00");
        timeLabel.setFont(defaultFont);

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
        JPanel eastPanel = new JPanel();

        JLabel lblMapWidth = new JLabel("Map Width");
        lblMapWidth.setFont(defaultFont);
        JLabel lblMapHeight = new JLabel("Map Height");
        lblMapHeight.setFont(defaultFont);

        JTextField txtMapWidth = new JTextField("" + LevelGenerator.MAP_WIDTH);
        JTextField txtMapHeight = new JTextField("" + LevelGenerator.MAP_HEIGHT);

        JButton btnNewLevel = new JButton("New Level");

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

        JCheckBox chkBoxBehindWalls = new JCheckBox();
        JLabel lblBehindWalls = new JLabel("See Behind Walls");
        lblBehindWalls.setFont(defaultFont);

        chkBoxBehindWalls.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                for (Mouse m : drawEngine.getMouses())
                    m.setSeeBehindWalls(chkBoxBehindWalls.isSelected());

                for (Cat c : drawEngine.getCats())
                    c.setSeeBehindWalls(chkBoxBehindWalls.isSelected());
            }
        });

        // Brace Yourself .... GridBagLayout is coming
        eastPanel.setLayout(new GridBagLayout());

        //Objet to constraint componants
        GridBagConstraints gbc = new GridBagConstraints();

        // padding
        gbc.insets = new Insets(5, 0, 0, 5);

        // FirstLabel (0,0) , occupies 2 cells
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        eastPanel.add(lblMapWidth, gbc);

        // SecondLabel (0, 2) occupies 2 cells
        gbc.gridx = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // indique fin de ligne
        eastPanel.add(lblMapHeight, gbc);

        // First TextField (1,0), 2 cells
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        txtMapWidth.setPreferredSize(new Dimension(60, 20));
        eastPanel.add(txtMapWidth, gbc);

        // second TextField (1,2), 2 cells
        gbc.gridx = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // indique fin de ligne
        txtMapHeight.setPreferredSize(new Dimension(60, 20));
        eastPanel.add(txtMapHeight, gbc);

        // Button (2, 1) , 2 cells ( centered )
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        eastPanel.add(btnNewLevel, gbc);

        // check box ( 0, 3) 1 cell
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        eastPanel.add(chkBoxBehindWalls, gbc);

        // label ( 1, 3) 3 cell
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        eastPanel.add(lblBehindWalls, gbc);

        controlPanel.add(eastPanel, BorderLayout.CENTER);
    }

    /**
     *
     */
    private void initBottomPanel()
    {
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout(5, 5));

        // Draw Shortest Button
        btnDrawShortest = new JButton("Draw shortest");
        btnDrawShortest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });

        // Center buttons ( ComboBox )
        JPanel centerPanel = new JPanel();

        mapsCmBox = new JComboBox<>();

        mapsCmBox.setPreferredSize(new Dimension(120, 20));
        mapsCmBox.setFont(defaultFont);

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
        JButton newMapButton = new JButton("New Window");
        newMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawEngine.addNewFrame(drawEngine.getMaps());
            }
        });

        // Add to frame
        bottomPanel.add(btnDrawShortest, BorderLayout.WEST);
        bottomPanel.add(centerPanel, BorderLayout.CENTER);
        bottomPanel.add(newMapButton, BorderLayout.EAST);

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
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 10, 5, 10));

        contentPane.setLayout(new GridLayout(1, 2, 5, 0));
        setContentPane(contentPane);
    }

    /**
     *
     */
    private void setListeners()
    {
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_CONTROL)
                {
                    ctrlPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_CONTROL)
                    ctrlPressed = false;
            }
        };
        startGameButton.addKeyListener(keyListener);
    }

    /**
     * add a mapContainerPanel to gamePanel with MouseListener
     * @param map Map
     */
    private void addMapContainerPanel(Map map, int TILE_SIZE) {
        mapPanel = new DE_MapPanel(drawEngine, map, TILE_SIZE);
        mapContainerPanel = new JScrollPane(mapPanel);

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
                        drawEngine.getMaps().get(0).switchTile(j, i);
                        update();
                    }
                }
            }
        });

        // Set wheel listener for zoom in/out
        mapContainerPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown())
                {
                    if(ctrlPressed)
                    {
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
                    else
                    {
                        dispatchEvent(e);
                    }
                }

            }
        });

        // set Drag listener for moving scroll
        mapPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println("drag");
                int x = e.getX()/TILE_SIZE;
                int y = e.getY()/TILE_SIZE;
                centerScroll(new Position(x,y));
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
    private void centerScroll(Position p) {
			/*Get width (Vw) and height (Vh) of viewport*/
        double vw = mapContainerPanel.getWidth();
        double vh = mapContainerPanel.getHeight();

        int vx = (int) (p.getPosX() * TILE_SIZE - vw / 2) + TILE_SIZE / 2;
        int vy = (int) (p.getPosY() * TILE_SIZE - vh / 2) + TILE_SIZE / 2;

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

        mapPanel.drawCharacterPaths();
    }


    public void displayEndGameScreen(String result) {
        JPanel topEndGamePanel = new JPanel();
        JPanel bottomEndGamePanel = new JPanel();

        JLabel lblResult = new JLabel(result);
        lblResult.setFont(new Font("Cambria", Font.PLAIN, 48));

        JButton playAgainButton = new JButton("PlayAgain");

        playAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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
