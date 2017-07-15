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


	public DrawEngine(Map map)
	{
		frames = new ArrayList<>();
		frames.add(new DrawEngineFrame(map));
	}

	public DrawEngine(ArrayList<Map> maps)
	{
		frames = new ArrayList<>();
		frames.add(new DrawEngineFrame(GameManager.gameManager.getLevelGenerator().getMap()));
	}

	public void addMap(Map map)
	{
		for(DrawEngineFrame frame : frames)
		{
			frame.addMap(map);
		}
	}

	public void removeMap(Map map)
	{
		for(DrawEngineFrame frame : frames)
		{
			frame.removeMap(map);
		}
	}

	/**
	 * Update all frames
	 */
	public void update()
	{
		for(DrawEngineFrame frame: frames)
		{
			frame.update();
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
		}
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

	public void displayEndGameScreen(String result) {

	}

	/**
	 * Add a new frame, open at small size
	 * apply changes depending on state
	 * @param maps
	 */
	private void addNewframe(ArrayList<Map> maps)
	{
		DrawEngineFrame newFrame = new DrawEngineFrame(maps);
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

	public class DrawEngineFrame extends JFrame {

		public int TILE_SIZE = 32; // Tiles will resize to this value

		private JPanel contentPane;
		private JScrollPane mapContainerPanel;
		private MapPanel mapPanel;
		private JLabel mapName, timeLabel;
		private JComboBox<String> mapsCmBox;
		private JButton startGameButton, btnDrawShortest;
		private JPanel gamePanel, controlPanel;

		private ArrayList<Map> maps;

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
			maps = new ArrayList<>();
			if (map == null)
			{
				map = new Map("Blank map", LevelGenerator.MAP_WIDTH, LevelGenerator.MAP_HEIGHT, Tile.NOT_DISCOVERED);
			}
			maps.add(map); // don't use addMap() because cmBox is null

			pathFinder = new PathFinder();

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
			JPanel topPanel = new JPanel();
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
			JPanel btnPanel = new JPanel();
			btnPanel.setLayout(new BorderLayout(5, 5));

			// Draw Shortest Button
			btnDrawShortest = new JButton("Draw shortest");
			btnDrawShortest.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					mapPanel.drawPath(pathFinder.getShortestPath(maps.get(0), initialPos, finalPos));

				}
			});

			// Center buttons ( Checkbox )
			JPanel centerPanel = new JPanel();

			mapsCmBox = new JComboBox<>();

			mapsCmBox.setPreferredSize(new Dimension(120, 20));
			mapsCmBox.setFont(defaultFont);
			mapsCmBox.addItem(map.getName());

			mapsCmBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					String mapName = (String) mapsCmBox.getSelectedItem();

					// find map
					for (Map m : maps)
					{
						if (m.getName().equals(mapName))
						{
							switchToMap(m);
						}
					}
				}
			});
			centerPanel.add(mapsCmBox);

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

            btnPanel.add(slide, BorderLayout.SOUTH);

			// new Map button
			JButton newMapButton = new JButton("New Window");
			newMapButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addNewframe(maps);
				}
			});

			// Add to frame
			btnPanel.add(btnDrawShortest, BorderLayout.WEST);
			btnPanel.add(centerPanel, BorderLayout.CENTER);
			btnPanel.add(newMapButton, BorderLayout.EAST);

			gamePanel.add(btnPanel, BorderLayout.SOUTH);

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
					/// TODO : implement new Level button
					// create a new map with TextFields dimensions
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

			controlPanel.add(eastPanel, BorderLayout.CENTER);

			/*Panels initialised, add to frame */
			contentPane.add(gamePanel);//, BorderLayout.WEST);
			contentPane.add(controlPanel);//, BorderLayout.EAST);


			/*Over, clean spaces and resize*/
			pack();

		} // End of constructor

		/**
		 * Constructor to instantiate a frame with several maps
		 * @param maps an arrayList of maps to show
		 */
		public DrawEngineFrame(ArrayList<Map> maps) {
			this(maps.get(0));

			for (int i = 1; i < maps.size(); i++)
				addMap(maps.get(i));
		}

		/**
		 * Adjust Tile size to fit all the screen
		 * @param map to adjust to
         * @param size (int)
		 */
		private void adjustTileSize(Map map, int size)
		{
			// TODO : Adjust tile size
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

            // Speed up scrolling TODO : Smooth scrolling value
            mapContainerPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            mapContainerPanel.getVerticalScrollBar().setUnitIncrement(TILE_SIZE/4);
            mapContainerPanel.getHorizontalScrollBar().setUnitIncrement(TILE_SIZE/4);

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
		 * Add map to maps list and ComboBox
		 * @param map well ... a MAP
		 */
		public void addMap(Map map) {
			maps.add(map);
			mapsCmBox.addItem(map.getName());
		}

		/**
		 * Remove map from list and comboBox
		 * @param map (Map) map to remove
		 */
		public void removeMap(Map map) {
			maps.remove(map);
			mapsCmBox.removeItem(map.getName());
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
		 * Refresh map ( draw level, characters and objects )
		 * Draw Cat and mouse DestinationPath /// TODO
		 */
		public void update() /// TODO : draw cat computed path
		{
			mapPanel.update();
			updateTime(GameManager.gameManager.getTimer().getCurrentTime().toString());

			for (Cat cat : GameManager.gameManager.getCats())
				mapPanel.drawPath(cat.getDestinationPath());
			for (Mouse mouse : GameManager.gameManager.getMouses())
				mapPanel.drawPath(mouse.getDestinationPath());


		}

		class MapPanel extends JPanel {
			private static final long serialVersionUID = 1L;

			public Map map;

			private BufferedImage bufferedMap;
			private Graphics2D graphic;

			private final int CAT_SPRITE = 0, MOUSE_SPRITE = 1,
					NOT_DISCOVERED_SPRITE = 2, EMPTY_SPRITE = 3,
					WALL_SPRITE = 4, CHEESE_SPRITE = 5,
					POWERUP_VISION_SPRITE = 6, POWERUP_SPEED_SPRITE = 7,
					INVISIBLE_ZONE_SPRITE = 8, MINE_SPRITE = 9;

			private ArrayList<BufferedImage> sprites;

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
						"cat_Sprite.png",
						"mouse_sprite.png",
						"not_discovered_sprite.png",
						"empty_sprite.png",
						"wall_sprite.png",
						"cheese_sprite.png",
						"powerup_speed_sprite.png",
						"powerup_vision_sprite.png",
						"invisible_zone_sprite.png",
						"mine_sprite.png"
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


			public void drawPath(ArrayList<Position> path) {
				bufferPath(path);
				bufferObjects();
				bufferCharacters();
				repaint();
			}

        /*Buffer functions, don't repaint the map*/

			public void bufferCharacters() {
				// for each mouse
				/* // Chars are on map now, no need to display them
				for (Mouse m : gameManager.getMouses())
				{
					graphic.drawImage(sprites.get(MOUSE_SPRITE),
							m.getPosition().getPosX() * TILE_SIZE,
							m.getPosition().getPosY() * TILE_SIZE,
							null);
				}
				// for each cat
				for (Cat c : GameManager.gameManager.getCats())
				{
					graphic.drawImage(sprites.get(CAT_SPRITE),
							c.getPosition().getPosX() * TILE_SIZE,
							c.getPosition().getPosY() * TILE_SIZE,
							null);
				}*/
			}

			private void bufferPath(ArrayList<Position> path) {
				if (path == null || path.isEmpty()) return;

				graphic.setColor(Color.BLUE);
				for (Position t : path)
				{
					graphic.fillRect(t.getPosX() * TILE_SIZE, t.getPosY() * TILE_SIZE,
							TILE_SIZE, TILE_SIZE);
				}
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
						}
					}
				}
				// ended
			}
		} // End of MapPanel

	}    // End Of DrawEngine
}