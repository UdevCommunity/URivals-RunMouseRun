package run_mouse_run;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DrawEngine {

	private PathFinder pathFinder;

	private ArrayList<DrawEngineFrame> frames;	// All open frames

    private ArrayList<Map> maps;

    public DrawEngine(Map map)
	{
        maps = new ArrayList<>();
        updateMapsList(map);

		frames = new ArrayList<>();
		frames.add(new DrawEngineFrame(map));

		pathFinder = new PathFinder(map);
	}

    private synchronized void updateMapsList(Map map)
    {
        maps.clear();
        maps.add(map);
        // add mouse Maps
        for(Mouse m: GameManager.gameManager.getMouses())
            maps.add(m.getViewedMap());
        // add cat Maps
        for(Cat c : GameManager.gameManager.getCats())
            maps.add(c.getViewedMap());
    }

    /**
	 * Update all frames
	 */
	public void update()
	{
        //updateMapsList(maps.get(0));

        for(int i = 0; i < frames.size(); i++)
		{
			frames.get(i).update();
		}
	}

	/**
	 * Set visibility of all frames
	 * @param visible (boolean)
	 */
	public void setVisible(boolean visible)
	{
		for(DrawEngineFrame frame : frames)
			frame.setVisible(visible);
	}

	/**
	 * Functions for button Action, call gameManager method and change all frames
	 */
	private void startGame()
	{
		GameManager.gameManager.startGame();
		for(DrawEngineFrame frame : frames)
		{
			frame.changeState("Start Game");
            //frame.updateCmBox();
		}

		update();
	}

	private void pauseGame()
	{
		GameManager.gameManager.pauseGame();
		for(DrawEngineFrame frame: frames)
		{
			frame.changeState("Pause Game");
		}
	}

	private void resumeGame()
	{
		GameManager.gameManager.resumeGame();
		for(DrawEngineFrame frame: frames)
		{
			frame.changeState("Resume Game");
		}
	}

	public void displayEndGameScreen(String result)
	{
		for(int i = 1; i < frames.size(); i++)
			frames.get(i).dispose();

		frames.get(0).displayEndGameScreen(result);
	}

	/**
	 * Add a new frame, open at small size
	 * apply changes depending on state
	 * @param maps
	 */
	private void addNewFrame(ArrayList<Map> maps)
	{
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				DrawEngineFrame newFrame = new DrawEngineFrame(maps.get(0));
				newFrame.startGameButton.setText(frames.get(0).startGameButton.getText());

				// We only need one control panel
				newFrame.hideControlPanel();
				newFrame.setBounds(100,100, 500,500);
				newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				// Set state to the same state of her
				if(newFrame.startGameButton.getText().equals("Resume Game")) // if game Paused
				{
					newFrame.changeState("Pause Game");
					newFrame.hideControlPanel();
				}
				else if(newFrame.startGameButton.getText().equals("Pause Game")) // if game running
				{
					newFrame.changeState("Resume Game");
					newFrame.hideControlPanel();
				}

				newFrame.setVisible(true);
				frames.add(newFrame);
			}
		});

	}

    /**
     * Frame that displays one of the maps in the maps ArrayList
     */
	public class DrawEngineFrame extends JFrame {

		public int TILE_SIZE = 48; // Tiles will resize to this value

		private JPanel contentPane;
		private JScrollPane mapContainerPanel;
		private MapPanel mapPanel;
		private JLabel mapName, timeLabel;
		private JComboBox<String> mapsCmBox;
		private JButton startGameButton, btnDrawShortest;
		private JPanel topPanel, bottomPanel, gamePanel, controlPanel;

		/* For testing path finding */
		private Position initialPos = new Position(2, 2);
		private Position finalPos = new Position(6, 6);


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
		public DrawEngineFrame(Map map)
		{
			if (map == null)
			{
				map = new Map("Blank map", LevelGenerator.MAP_WIDTH, LevelGenerator.MAP_HEIGHT, Tile.NOT_DISCOVERED);
			}

			// Set GUI
			initWindow();

			Font defaultFont = new Font("Calibri", Font.PLAIN, 18);

			gamePanel = new JPanel();    // Panel containing the Map, and inGame informations
			gamePanel.setLayout(new BorderLayout(5, 5));

			controlPanel = new JPanel(); // Panel containing options ( map with/height ..etc)
			controlPanel.setLayout(new BorderLayout(5, 5));

		/*=========================================================================================*/
		/*====================================== Top Panel ========================================*/
		/*=========================================================================================*/
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
						startGame();

					} else if (startGameButton.getText().equals("Pause Game"))
					{
						pauseGame();
					} else
					{
						resumeGame();
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

		/*=========================================================================================*/
		/*====================================== Map Panel ========================================*/
		/*=========================================================================================*/
		    addMapContainerPanel(map);

		/*=========================================================================================*/
		/*====================================== Bottom Panel =====================================*/
		/*=========================================================================================*/
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new BorderLayout(5, 5));

			// Draw Shortest Button
			btnDrawShortest = new JButton("Draw shortest");
			btnDrawShortest.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					mapPanel.drawPath(
					        pathFinder.getShortestPath(maps.get(0), initialPos, finalPos)
                    );

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

                        switchToMap(maps.get(i));
                    }
                    catch (Exception ex)
                    {
                        // do nothing
                    }
				}
			});
			centerPanel.add(mapsCmBox);

			// Control Buttons
			JButton btnCenterScroll = new JButton("Find Mouse");

			btnCenterScroll.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					centerScroll(GameManager.gameManager.getMouses().get(0).getPosition());
				}
			});
			centerPanel.add(btnCenterScroll);

			// TILE SIZE Slider
            JSlider slide = new JSlider();

            slide.setMaximum(128);
            slide.setMinimum(16);
            slide.setValue(TILE_SIZE);
            slide.setPaintTicks(true);
            slide.setPaintLabels(true);
            slide.setMinorTickSpacing(8);
            slide.setMajorTickSpacing(8);

            slide.addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent event)
                {
                    int size = ((JSlider)event.getSource()).getValue();

                    adjustTileSize(maps.get(mapsCmBox.getSelectedIndex()), size);
                }
            });

            bottomPanel.add(slide, BorderLayout.SOUTH);

			// new Map button
			JButton newMapButton = new JButton("New Window");
			newMapButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addNewFrame(maps);
				}
			});

			// Add to frame
			bottomPanel.add(btnDrawShortest, BorderLayout.WEST);
			bottomPanel.add(centerPanel, BorderLayout.CENTER);
			bottomPanel.add(newMapButton, BorderLayout.EAST);

			gamePanel.add(bottomPanel, BorderLayout.SOUTH);

		/*=========================================================================================*/
		/*====================================== East Panel ========================================*/
		/*=========================================================================================*/
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

						maps.remove(GameManager.gameManager.getLevelGenerator().getMap()); // removeLevelMap
						GameManager.gameManager.getLevelGenerator().setMap(w, h);
						maps.add(0, GameManager.gameManager.getLevelGenerator().getMap()); // add new LevelMap

						update();
					}
					catch (Exception ex)
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
                    for(Mouse m : GameManager.gameManager.getMouses())
                        m.setSeeBehindWalls(chkBoxBehindWalls.isSelected());

                    for(Cat c : GameManager.gameManager.getCats())
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

			/*Panels initialised, add to frame */
			contentPane.add(gamePanel);//, BorderLayout.WEST);
			contentPane.add(controlPanel);//, BorderLayout.EAST);


			/*Over, clean spaces and resize*/
			pack();

		} // End of constructor

        /**
         *
         */
        private void updateCmBox()
        {
            if(mapsCmBox == null) return;

            mapsCmBox.removeAllItems();
            for(Map m : maps)
            {
                mapsCmBox.addItem(m.getName());
            }
        }

        /**
		 * Adjust Tile size to fit all the screen
		 * @param map to adjust to
         * @param size (int)
		 */
		private void adjustTileSize(Map map, int size)
		{
            TILE_SIZE = size;
			gamePanel.remove(mapContainerPanel);
            addMapContainerPanel(map);

			update();
		}

        /**
         * add a mapContainerPanel to gamePanel with MouseListener
         * @param map Map
         */
		private void addMapContainerPanel(Map map)
		{
            mapPanel = new MapPanel(map);
            mapContainerPanel = new JScrollPane(mapPanel);

			mapPanel.addMouseListener(new MouseAdapter() {
				/*
                 * On LeftClick : Set InitialPosition ( testing pathfinding )
                 * On RightClick : Set finalPosition ( testing pathfinding )
                 * On MiddleClick : switch tile ( edit map )
                 */
				@Override
				public void mouseClicked(MouseEvent event) {
					int i = event.getY() / TILE_SIZE;
					int j = event.getX() / TILE_SIZE;

					if (!startGameButton.getText().equals("Pause Game")) // if game not running
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
							maps.get(0).switchTile(j, i);
							update();
						}
					}
				}
			});

            // Speed up scrolling
            mapContainerPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            mapContainerPanel.getVerticalScrollBar().setUnitIncrement(TILE_SIZE/2);
            mapContainerPanel.getHorizontalScrollBar().setUnitIncrement(TILE_SIZE/2);

            gamePanel.add(mapContainerPanel, BorderLayout.CENTER);
		}

		/**
		 * Inits DrawEngine frame adapting to height
		 */
		private void initWindow() {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setTitle("Run Mouse Run!");

			// Set on FullScreen
			setExtendedState(JFrame.MAXIMIZED_BOTH );

			//setResizable(false);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 10, 5, 10));

			contentPane.setLayout(new GridLayout(1,2,5,0));
			setContentPane(contentPane);
		}

		/**
		 * Change window state ( Before Start/Running/Paused )
		 * State given from button text
		 * @param buttonText (String) Current Button Text
		 */
		private void changeState(String buttonText)
		{
			if(buttonText.equals("Start Game"))// RUN GAME FROM START
			{
				startGameButton.setText("Pause Game");
				btnDrawShortest.setEnabled(false);
				hideControlPanel();
			}
			else if(buttonText.equals("Pause Game"))// PAUSE GAME
			{
				startGameButton.setText("Resume Game");
				btnDrawShortest.setEnabled(true);
			}
			else // RUN GAME FROM PAUSED
			{
				startGameButton.setText("Pause Game");
				btnDrawShortest.setEnabled(false);
			}
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
		 * Remove Control Panel from contentPane
		 */
		private void hideControlPanel()
		{
			controlPanel.setVisible(false);
			contentPane.remove(controlPanel);
			contentPane.invalidate();

			adjustTileSize(maps.get(0), TILE_SIZE);
		}

		/**
		 * Convert time to mm:ss format and show on timeLabel
		 * @param currentTime (float) current time in miliseconds
		 */
		private void updateTime(String currentTime)
		{
			timeLabel.setText("Time : " + currentTime.substring(3));
		}

		/**
		 * A little hard coded, know which character to follow depending on map
		 */
		private void updateScroll()
		{

			int index = mapsCmBox.getSelectedIndex();
			if( index > 0) // if not levelMap
			{
				if(index < GameManager.gameManager.getMouses().size()+1)
				{
					// Mouse
					centerScroll(
							GameManager.gameManager.getMouses().get(index-1)
							.getPosition()
					);
				}
				else if(index < GameManager.gameManager.getCats().size()
						+ GameManager.gameManager.getMouses().size() +1)
				{
					// Cat
					centerScroll(
							GameManager.gameManager.getCats()
							.get(index -GameManager.gameManager.getMouses().size() -1)
							.getPosition()
					);
				}
			}
		}

		/**
		 * Calculate and set position of scrol View port (mapContainerPanel)
		 * se that the given p is in center
		 * @param p (Position)
		 */
		private void centerScroll(Position p)
		{
			/*Get width (Vw) and height (Vh) of viewport*/
			double vw = mapContainerPanel.getWidth();
			double vh = mapContainerPanel.getHeight();

			int vx = (int) (p.getPosX()*TILE_SIZE - vw/2) + TILE_SIZE/2;
			int vy = (int) (p.getPosY()*TILE_SIZE - vh/2) + TILE_SIZE/2;

			// check for bounds
			int maxX = (int) (maps.get(mapsCmBox.getSelectedIndex()).getWidth()*TILE_SIZE-vw);
			int maxY = (int) (maps.get(mapsCmBox.getSelectedIndex()).getHeight()*TILE_SIZE-vh);
			maxX = (maxX >= 0)? maxX: 0;  maxY = (maxY >= 0)? maxY: 0;

			vx = (vx >= 0)? vx: 0;  vy = (vy >= 0)? vy: 0;
			vx = (vx >= maxX)? maxX : vx; vy = (vy >= maxY)? maxY : vy;


			mapContainerPanel.getViewport().setViewPosition(new Point(vx,vy));

		}
		/**
		 * Refresh map ( draw level, characters and objects )
		 * Draw Cat and mouse DestinationPath
		 */
		public void update()
		{
		    mapPanel.setMap(maps.get(mapsCmBox.getSelectedIndex()));
			mapPanel.update();
			updateTime(GameManager.gameManager.getTimer().getCurrentTime().toString());
			updateScroll();

			mapPanel.drawCharacterPaths();
		}


		public void displayEndGameScreen(String result)
		{
			JPanel topEndGamePanel = new JPanel();
			JPanel bottomEndGamePanel = new JPanel();

			JLabel lblResult = new JLabel(result);
			lblResult.setFont(new Font("Cambria", Font.PLAIN, 48));

			JButton playAgainButton = new JButton("PlayAgain");

			playAgainButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{

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

		class MapPanel extends JPanel {
			private static final long serialVersionUID = 1L;

			public Map map;

			private BufferedImage bufferedMap;
			private Graphics2D graphic;

			private final int NOT_DISCOVERED_SPRITE = 0, EMPTY_SPRITE = 1,
					WALL_SPRITE = 2, CHEESE_SPRITE = 3,
					POWERUP_VISION_SPRITE = 5, POWERUP_SPEED_SPRITE = 4,
					INVISIBLE_ZONE_SPRITE = 6, MINE_SPRITE = 7,
					CAT_SPRITE = 8, MOUSE_SPRITE = 9;

			private ArrayList<BufferedImage> sprites;
			private ArrayList<BufferedImage> customSprites;


			public MapPanel(Map map) {
				this.map = map;

				adjustPanelSize();

				/*init buffered Map */
				bufferedMap = new BufferedImage(LevelGenerator.MAP_WIDTH * TILE_SIZE,
						LevelGenerator.MAP_HEIGHT * TILE_SIZE,
						BufferedImage.TYPE_BYTE_INDEXED);
				graphic = bufferedMap.createGraphics();

				// Load Sprites
				sprites = loadSprites();
				customSprites = loadCustomSprites();

				// draw map
				drawMap();

				// draw all
				update();
			}

			public void adjustPanelSize() {
				setPreferredSize(new Dimension(map.getWidth() * TILE_SIZE,
						map.getHeight() * TILE_SIZE));

				if(sprites != null)
					sprites = loadSprites();
			}

			public void setMap(Map map) {
				this.map = map;
			}

			private ArrayList<BufferedImage> loadSprites() {
            /*Load sprites files */
				ArrayList<BufferedImage> sprites = new ArrayList<>();

				final String[] spritesFileNames = {
						"not_discovered_sprite.png",
						"empty_sprite.png",
						"wall_sprite.png",
						"cheese_sprite.png",
						"powerup_speed_sprite.png",
						"powerup_vision_sprite.png",
						"invisible_zone_sprite.png",
						"mine_sprite.png",
						"cat_Sprite.png",
						"mouse_sprite.png"
				};


				for (int i = 0; i < spritesFileNames.length; i++)
				{
					try
					{
						// Load file
						File spriteFile = new File("res/" + spritesFileNames[i]);
						// Read image
						BufferedImage sprite = ImageIO.read(spriteFile);
						// resize
						int type = sprite.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : sprite.getType();
						sprite = resizeImage(sprite, type, TILE_SIZE, TILE_SIZE);
						//add to sprites
						sprites.add(sprite);
					} catch (IOException e)
					{
						System.err.println("Error loading Sprite : " + spritesFileNames[i]);
						// generate a black tile
						sprites.add(new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_BYTE_INDEXED));
					}
				}
				return sprites;
			}

			/**
			 * Load Custom Sprites, file name format : [CharName]_sprite.png
			 * if file not found; load default from sprites
			 * @return customSprites (ArrayList)
			 */
			private ArrayList<BufferedImage> loadCustomSprites()
			{
				ArrayList<BufferedImage> customSprites = new ArrayList<>();

				for(Mouse m: GameManager.gameManager.getMouses())
				{
					try{
						// Load file
						File spriteFile = new File("res/" + m.getName()+ ".png");
						// Read image
						BufferedImage sprite = ImageIO.read(spriteFile);
						// resize
						int type = sprite.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : sprite.getType();
						sprite = resizeImage(sprite, type, TILE_SIZE, TILE_SIZE);
						//add to customSprites
						customSprites.add(sprite);

					}catch (Exception e)
					{
						customSprites.add(sprites.get(MOUSE_SPRITE));
					}
				}

				for(Cat c: GameManager.gameManager.getCats())
				{
					try{
						// Load file
						File spriteFile = new File("res/" + c.getName()+ "_sprite.png");
						// Read image
						BufferedImage sprite = ImageIO.read(spriteFile);
						// resize
						int type = sprite.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : sprite.getType();
						sprite = resizeImage(sprite, type, TILE_SIZE, TILE_SIZE);
						//add to customSprites
						customSprites.add(sprite);

					}catch (Exception e)
					{
						customSprites.add(sprites.get(CAT_SPRITE));
					}
				}
				return customSprites;
			}

			/**
			 * Resize image
			 * @param originalImage (BufferedImage) image to resize
			 * @param type	type of image, use ARGB for tranparancy
			 * @param IMG_WIDTH (int)
			 * @param IMG_HEIGHT (int)
			 * @return
			 */
			private BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
				BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
				Graphics2D g = resizedImage.createGraphics();
				g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
				g.dispose();

				return resizedImage;
			}

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

            /*Draw buffered map*/
				g.drawImage(bufferedMap, 0, 0, null);

			}

			public void update() {
				bufferMap();
				bufferObjects();
				bufferCharacters();
				repaint();
			}

        /* Draw functions, all buffer on map, bufferChars and repaint the map */

			/*Takes the DrawEngin map, buffer it, add objects and repaint it*/
			public void drawMap() {
				bufferMap();
				bufferObjects();
				bufferCharacters();
				repaint();
			}

			public void drawPoint(int x, int y, Color color) {
				graphic.setColor(color);
				graphic.fillRect(x * TILE_SIZE, y * TILE_SIZE,
						TILE_SIZE, TILE_SIZE);
				repaint();
			}


			public void drawCharacterPaths()
			{
				int index = 0;
				for (Mouse mouse : GameManager.gameManager.getMouses())
				{
					mapPanel.drawPath(mouse.getDestinationPath(), customSprites.get(index));
					index++;
				}

				for (Cat cat : GameManager.gameManager.getCats())
				{
					mapPanel.drawPath(cat.getDestinationPath(), customSprites.get(index));
					index++;
				}
			}

			public void drawPath(ArrayList<Position> path)
			{
				drawPath(path, sprites.get(CAT_SPRITE));
			}

			public void drawPath(ArrayList<Position> path, BufferedImage sprite)
			{
				bufferPath((ArrayList<Position>) path.clone(), sprite);
				bufferObjects();
				bufferCharacters();
				repaint();
			}

        /*Buffer functions don't repaint the map*/

			private void bufferCharacters() {
				// for each mouse
				int index = 0;
				for (Mouse m : GameManager.gameManager.getMouses())
				{
					graphic.drawImage(customSprites.get(index),
							m.getPosition().getPosX() * TILE_SIZE,
							m.getPosition().getPosY() * TILE_SIZE,
							null);
					index++;
				}
				// for each cat
				for (Cat c : GameManager.gameManager.getCats())
				{
					graphic.drawImage(customSprites.get(index),
							c.getPosition().getPosX() * TILE_SIZE,
							c.getPosition().getPosY() * TILE_SIZE,
							null);
					index++;
				}
			}

			private void bufferPath(ArrayList<Position> path, BufferedImage sprite) {
				if (path == null || path.isEmpty()) return;

				graphic.setColor(Color.BLUE);
               // Composite composite = graphic.getComposite();

                graphic.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
				for (Position t : path)
				{
                    graphic.drawImage(sprite,
                            t.getPosX() * TILE_SIZE, t.getPosY() * TILE_SIZE,
                            null);

                }

				graphic.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
			}

			private void bufferObjects() {
				if (!startGameButton.getText().equals("Pause Game")) // if game not running
				{
					drawPoint(initialPos.getPosX(), initialPos.getPosY(), Color.RED);
					drawPoint(finalPos.getPosX(), finalPos.getPosY(), Color.GREEN);
				}
			}

			private void bufferMap() {
        	/* Draw run_mouse_run.Map */
				for (int i = 0; i < map.getHeight(); i++)
				{
					for (int j = 0; j < map.getWidth(); j++)
					{
						// Choose tile
						switch (map.getTile(j, i))
						{
							case NOT_DISCOVERED:
								graphic.drawImage(sprites.get(NOT_DISCOVERED_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								break;
							case EMPTY:
								graphic.drawImage(sprites.get(EMPTY_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								break;
							case WALL:
								graphic.drawImage(sprites.get(WALL_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								break;
                            /*From here, draw empty first then object on it : */
							case CHEESE:
								graphic.drawImage(sprites.get(EMPTY_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								graphic.drawImage(sprites.get(CHEESE_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								break;
							case POWERUP_VISION:
								graphic.drawImage(sprites.get(EMPTY_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								graphic.drawImage(sprites.get(POWERUP_VISION_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								break;
							case POWERUP_SPEED:
								graphic.drawImage(sprites.get(EMPTY_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								graphic.drawImage(sprites.get(POWERUP_SPEED_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								break;
							case INVISIBLE_ZONE:
								graphic.drawImage(sprites.get(EMPTY_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								graphic.drawImage(sprites.get(INVISIBLE_ZONE_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								break;
							case MINE:
								graphic.drawImage(sprites.get(EMPTY_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								graphic.drawImage(sprites.get(MINE_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								break;
							case CAT:
								graphic.drawImage(sprites.get(EMPTY_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								break;
							case MOUSE:
								graphic.drawImage(sprites.get(EMPTY_SPRITE), j * TILE_SIZE, i * TILE_SIZE, null);
								break;
						}
					}
				}
				// ended
			}
		} // End of MapPanel

	}    // End Of DrawEngine
}