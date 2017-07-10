package run_mouse_run;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;


public class DrawEngine extends JFrame
{
	public static int TILE_SIZE = 24; // Tiles will resize to this value

	private JPanel contentPane;
	private JScrollPane mapContainerPanel;
	private MapPanel mapPanel;
	private JLabel mapName, timeLabel;
	private JComboBox<String> mapsCmBox;
	private JButton startGameButton;

	PathFinder pathFinder;

	private ArrayList<Map> maps;

	/* For testing path finding */
	private Position initialPos = new Position(2,2);
	private Position finalPos = new Position(6,6);


	/**
	 * Create the frame.
	 * adds Map
	 * create buttons with ActionListeners :
	 * ClickListener on panel ( for map editor )
	 * ActionListeners on buttons
	 * init cmCheckBox
	 * @param map initialised map from LevelGenerator
	 */
	public DrawEngine(Map map)
	{
		maps = new ArrayList<>();
		if(map == null)
		{
			map = new Map("Blank map", LevelGenerator.MAP_WIDTH, LevelGenerator.MAP_HEIGHT);

			for(int i = 0; i < LevelGenerator.MAP_HEIGHT; i++)
			{
				for(int j = 0; j < LevelGenerator.MAP_WIDTH; j++)
				{
					map.setTile(j, i, Tile.NOT_DISCOVERED);
				}
			}
		}
		maps.add(map); // don't use addMap() because cmBox is null

		pathFinder = new PathFinder();


		initWindow();

		/*=============================Text Panel ===============================*/
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(0,0));

		mapName = new JLabel("Level Map");
		mapName.setFont(new Font("Calibri", Font.PLAIN, 18));

		// Start game button
		JPanel startButtonPanel = new JPanel();
		startGameButton = new JButton("Start Game");

		startGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/// TODO : Start game button actions
			}
		});

		startButtonPanel.add(startGameButton);

		// Time Label
		timeLabel = new JLabel("Time : 00:00");
		timeLabel.setFont(new Font("Calibri", Font.PLAIN, 18));

		topPanel.add(mapName, BorderLayout.WEST);
		topPanel.add(startButtonPanel, BorderLayout.CENTER);
		topPanel.add(timeLabel, BorderLayout.EAST);


		contentPane.add(topPanel, BorderLayout.NORTH);

		/* ===========================Init Map Panel ============================*/
		mapPanel = new MapPanel(map);

		mapContainerPanel = new JScrollPane(mapPanel);

		mapPanel.addMouseListener(new MouseAdapter()
		{
			/*
			 * On LeftClick : Set InitialPosition ( testing pathfinding )
			 * On RightClick : Set finalPosition ( testing pathfinding )
			 */
			@Override
			public void mouseClicked(MouseEvent event)
			{
				int i = event.getY()/TILE_SIZE;
				int j = event.getX()/TILE_SIZE;

				if(event.getButton() == MouseEvent.BUTTON1)
				{
					initialPos.setPosX(j);
					initialPos.setPosY(i);
					mapPanel.clear();
				}
				else if(event.getButton() == MouseEvent.BUTTON3)
				{
					finalPos.setPosX(j);
					finalPos.setPosY(i);
					mapPanel.clear();
				}
				else if(event.getButton() == MouseEvent.BUTTON2)
				{
					maps.get(0).switchTile(j, i);
					update();
				}
			}
		});

		mapContainerPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mapContainerPanel.getVerticalScrollBar().setUnitIncrement(10);
		contentPane.add(mapContainerPanel, BorderLayout.CENTER);

		/*==========================Init Button Panel==================================*/
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BorderLayout(5,5));

		// Draw Shortest Button
		JButton btnDrawShortest = new JButton("Draw shortest");
		btnDrawShortest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				mapPanel.drawPath(pathFinder.getShortestPath(maps.get(0), initialPos, finalPos));
			}
		});

		// Center buttons ( Checkbox )
		JPanel centerPanel = new JPanel();

		mapsCmBox = new JComboBox<>();

		mapsCmBox.addItem(map.name);

		mapsCmBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String mapName = (String) mapsCmBox.getSelectedItem();

				// find map
				for(Map m : maps)
				{
					if(m.name.equals(mapName))
					{
						switchToMap(m);
					}
				}
			}
		});
		centerPanel.add(mapsCmBox);

		// new Map button
		JButton newMapButton = new JButton("New Map");
		newMapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DrawEngine newFrame = new DrawEngine(maps);

				newFrame.setVisible(true);
			}
		});

		// Add to frame
		btnPanel.add(btnDrawShortest, BorderLayout.WEST);
		btnPanel.add(centerPanel, BorderLayout.CENTER);
		btnPanel.add(newMapButton, BorderLayout.EAST);

		contentPane.add(btnPanel, BorderLayout.SOUTH);

	} // End of constructor

	/**
	 * Constructor to instanciate a frame with several maps
	 * @param maps an arrayList of maps to show
	 */
	public DrawEngine(ArrayList<Map> maps)
	{
		this(maps.get(0));
		for(int i = 1; i < maps.size(); i++)
			addMap(maps.get(i));
	}

	/**
	 * Add map to maps list and ComboBox
	 * @param map well ... a MAP
	 */
	public void addMap(Map map)
	{
		maps.add(map);
		mapsCmBox.addItem(map.name);
	}

	/**
	 * Remove map from list and comboBox
	 * @param map (Map) map to remove
	 */
	public void removeMap(Map map)
	{
		maps.remove(map);
		mapsCmBox.removeItem(map.name);
	}

	/**
	 * Set map to show in mapPanel
	 * @param map map to show
	 */
	private void switchToMap(Map map)
	{
		mapPanel.setMap(map);
		mapName.setText(map.name);
		update();
		adjustFrameHeight(map);
		update();
	}

	/**
	 * Adjust frame height to given map size and window size
	 * @param map to adjust to
	 */
	private void adjustFrameHeight(Map map)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		int winHeight = TILE_SIZE * map.height + TILE_SIZE + 50;

		System.out.println("PANEL HEIGHT : " + winHeight);
		if(winHeight >= screenSize.getHeight() - 50)
			winHeight = (int) screenSize.getHeight() - 50;

		setSize(new Dimension(
				TILE_SIZE * map.width + TILE_SIZE + 10,
				winHeight
		));

		System.out.println("FRAME HEIGHT : " + winHeight);
	}
	/**
	 * Inits DrawEngine frame adapting to height
	 */
	private void initWindow()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Run Mouse Run!");

		adjustFrameHeight(maps.get(0));


		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

	public void update() /// TODO : draw cat computed path
	{
		mapPanel.clear();
		mapPanel.repaint();
	}

	class MapPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public Map map;

		private BufferedImage bufferedMap;
		private Graphics2D graphic;

		/*
        * Sprites : 0 : Cat , 1 : Mouse
        * Ordered as marked in Tile class :
         * 2 : NOT_DISCOVERED, 3: EMPTY, 4 : WALL , 5 : CHEESE, 6 : POWERUP_VISION,
         * 7 : POWERUP_SPEED, 8: INVISIBLE_ZONE, 9 : Mine
        */
		private ArrayList<BufferedImage> sprites;

		public MapPanel(Map map)
		{
			this.map = map;

			setPreferredSize(new Dimension(LevelGenerator.MAP_WIDTH*TILE_SIZE,
					LevelGenerator.MAP_HEIGHT*TILE_SIZE));

			/*init buffered run_mouse_run.Map */
			bufferedMap = new BufferedImage(LevelGenerator.MAP_WIDTH*TILE_SIZE,
					LevelGenerator.MAP_HEIGHT*TILE_SIZE,
					BufferedImage.TYPE_BYTE_INDEXED);
			graphic = bufferedMap.createGraphics();

			// Load Sprites
			loadSprites();
			// draw map
			drawMap();

			// draw chars
			bufferCharacters();
		}

		public void setMap(Map map)
		{
			this.map = map;
		}

		private void loadSprites()
		{
            /*Load sprites files */
			sprites = new ArrayList<>();

			final String[] fileNames = {
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


			for(int i = 0; i < fileNames.length; i++)
			{
				try
				{
					// Load file
					File spriteFile = new File("res/"+fileNames[i]);
					// Read image
					BufferedImage sprite = ImageIO.read(spriteFile);
					// resize
					sprite = resizeImage(sprite, TILE_SIZE, TILE_SIZE);
					//add to sprites
					sprites.add(sprite);
				} catch (IOException e)
				{
					System.err.println("Error loading Sprite : " + fileNames[i]);
					// generate a black tile
					sprites.add(new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_BYTE_INDEXED));
				}
			}

		}

		public BufferedImage resizeImage (BufferedImage image, int areaWidth, int areaHeight) {
			float scaleX = (float) areaWidth / image.getWidth();
			float scaleY = (float) areaHeight / image.getHeight();
			float scale = Math.min(scaleX, scaleY);
			int w = Math.round(image.getWidth() * scale);
			int h = Math.round(image.getHeight() * scale);

			int type = image.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

			boolean scaleDown = scale < 1;

			if (scaleDown) {
				// multi-pass bilinear div 2
				int currentW = image.getWidth();
				int currentH = image.getHeight();
				BufferedImage resized = image;
				while (currentW > w || currentH > h) {
					currentW = Math.max(w, currentW / 2);
					currentH = Math.max(h, currentH / 2);

					BufferedImage temp = new BufferedImage(currentW, currentH, type);
					Graphics2D g2 = temp.createGraphics();
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g2.drawImage(resized, 0, 0, currentW, currentH, null);
					g2.dispose();
					resized = temp;
				}
				return resized;
			} else {
				Object hint = scale > 2 ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_BILINEAR;

				BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = resized.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
				g2.drawImage(image, 0, 0, w, h, null);
				g2.dispose();
				return resized;
			}
		}

		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

            /*Draw buffered map*/
			g.drawImage(bufferedMap, 0, 0, null);

		}

		public void clear()
		{
			bufferMap();
			bufferObjects();
			bufferCharacters();
			repaint();
		}

        /* Draw functions, all buffer on map, bufferChars and repaint the map */

		/*Takes the DrawEngin map, buffer it, add objects and repaint it*/
		public void drawMap()
		{
			bufferMap();
			bufferObjects();
			bufferCharacters();
			repaint();
		}

		public void drawPoint(int x, int y, Color color)
		{
			graphic.setColor(color);
			graphic.fillRect(x*TILE_SIZE, y*TILE_SIZE,
					TILE_SIZE, TILE_SIZE);
			repaint();
		}



		public void drawPath(ArrayList<Position> path)
		{
			bufferPath(path);
			bufferObjects();
			bufferCharacters();
			repaint();
		}

        /*Buffer functions, don't repaint the map*/

		public void bufferCharacters()
		{
			// mouse
			graphic.drawImage(sprites.get(1),
					LevelGenerator.MOUSES_INITIAL_POS.getPosX()*TILE_SIZE,
					LevelGenerator.MOUSES_INITIAL_POS.getPosY()*TILE_SIZE,
					null);

			// cats
			graphic.drawImage(sprites.get(0),
					LevelGenerator.CATS_INITIAL_POS.getPosX()*TILE_SIZE,
					LevelGenerator.CATS_INITIAL_POS.getPosY()*TILE_SIZE,
					null);
		}

		private void bufferPath(ArrayList<Position> path)
		{
			if(path.isEmpty()) return;

			graphic.setColor(Color.BLUE);
			for(Position t : path)
			{
				graphic.fillRect(t.getPosX()*TILE_SIZE, t.getPosY()*TILE_SIZE,
						TILE_SIZE, TILE_SIZE);
			}
		}

		private void bufferObjects()
		{
    		/*For testing */
			drawPoint(initialPos.getPosX(), initialPos.getPosY(), Color.RED);
			drawPoint(finalPos.getPosX(), finalPos.getPosY(), Color.GREEN);
		}

		private void bufferMap()
		{
        	/* Draw run_mouse_run.Map */
			for (int i = 0; i < map.height; i++) {
				for (int j = 0; j < map.width; j++)
				{
					// Choose tile

					switch (map.getTile(j, i)) {

						case NOT_DISCOVERED:
							graphic.drawImage(sprites.get(2),j*TILE_SIZE, i*TILE_SIZE, null);
							break;
						case EMPTY:
							graphic.drawImage(sprites.get(3),j*TILE_SIZE, i*TILE_SIZE, null);
							break;
						case WALL:
							graphic.drawImage(sprites.get(4),j*TILE_SIZE, i*TILE_SIZE, null);
							break;
                            /*From here, draw empty first then object on it : */
						case CHEESE:
							graphic.drawImage(sprites.get(3),j*TILE_SIZE, i*TILE_SIZE, null);
							graphic.drawImage(sprites.get(5),j*TILE_SIZE, i*TILE_SIZE, null);
							break;
						case POWERUP_VISION:
							graphic.drawImage(sprites.get(3),j*TILE_SIZE, i*TILE_SIZE, null);
							graphic.drawImage(sprites.get(6),j*TILE_SIZE, i*TILE_SIZE, null);
							break;
						case POWERUP_SPEED:
							graphic.drawImage(sprites.get(3),j*TILE_SIZE, i*TILE_SIZE, null);
							graphic.drawImage(sprites.get(7),j*TILE_SIZE, i*TILE_SIZE, null);
							break;
						case INVISIBLE_ZONE:
							graphic.drawImage(sprites.get(3),j*TILE_SIZE, i*TILE_SIZE, null);
							graphic.drawImage(sprites.get(8),j*TILE_SIZE, i*TILE_SIZE, null);
							break;
						case MINE:
							graphic.drawImage(sprites.get(3),j*TILE_SIZE, i*TILE_SIZE, null);
							graphic.drawImage(sprites.get(9),j*TILE_SIZE, i*TILE_SIZE, null);
							break;
					}
				}
			}
			// ended
		}
	} // End of MapPanel

}	// End Of run_mouse_run.DrawEngine