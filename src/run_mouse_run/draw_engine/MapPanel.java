package run_mouse_run.draw_engine;

import run_mouse_run.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Created by TOSHIBA on 20/07/2017.
 */

public class MapPanel extends JPanel {

    public Map map;
    private DrawEngine drawEngine;

    public int TILE_SIZE;
    private final int CHARACTER_LAYER = 2;

    private ArrayList<JLabel[][]> layers;
    private GridLayout gridLayout;

    private final int NOT_DISCOVERED_SPRITE = 0, EMPTY_SPRITE = 1,
            WALL_SPRITE = 2, CHEESE_SPRITE = 3,
            POWERUP_VISION_SPRITE = 5, POWERUP_SPEED_SPRITE = 4,
            INVISIBLE_ZONE_SPRITE = 6, MINE_SPRITE = 7,
            CAT_SPRITE = 8, MOUSE_SPRITE = 9;
    private ArrayList<TileImage> sprites;
    private ArrayList<TileImage> customSprites;

    private ArrayList<BufferedImage> explosionFrames;


    public MapPanel(DrawEngine drawEngine, Map map, int TILE_SIZE)
    {
        this.drawEngine = drawEngine;
        this.map = map;
        this.TILE_SIZE = TILE_SIZE;

        adjustPanelSize();

        sprites = loadSprites();
        customSprites = loadCustomSprites();
        explosionFrames = loadExplosionFrames();

        gridLayout = new GridLayout(map.getHeight(), map.getWidth(), 0, 0);
        setLayout(gridLayout);

				/*Init Tiles layers */
        layers = new ArrayList<>();

        // won't use loop for better control
        layers.add(initLayer(null));
        layers.add(initLayer(layers.get(0)));
        layers.add(initLayer(layers.get(1)));

        // draw all
        update();
    }

    public JLabel[][] initLayer(JLabel[][] bgLayer) {
        JLabel[][] layer = new JLabel[map.getHeight()][map.getWidth()];

        for (int i = 0; i < map.getHeight(); i++)
        {
            for (int j = 0; j < map.getWidth(); j++)
            {
                layer[i][j] = new JLabel();

                layer[i][j].setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                layer[i][j].setSize(new Dimension(TILE_SIZE, TILE_SIZE));
                layer[i][j].setLocation(0, 0);

                if (bgLayer != null)
                {
                    bgLayer[i][j].add(layer[i][j], BorderLayout.CENTER);
                } else
                {
                    add(layer[i][j]);    // add to Panel
                }

            }
        }
        return layer;
    }

    public void adjustPanelSize() {
        setPreferredSize(new Dimension(map.getWidth() * TILE_SIZE,
                map.getHeight() * TILE_SIZE));

        if (sprites != null)
            sprites = loadSprites();
    }

    public void setMap(Map map) {
        this.map = map;
    }

    private ArrayList<TileImage> loadSprites() {
                /*Load sprites files */
        ArrayList<TileImage> sprites = new ArrayList<>();

        final String[] spritesFileNames = {
                "not_discovered_sprite.png",
                "empty_sprite.png",
                "wall_sprite.png",
                "cheese_sprite.png",
                "powerup_speed_sprite.png",
                "powerup_vision_sprite.png",
                "invisible_zone_sprite.png",
                "mine_sprite.png",
                "cat_sprite.png",
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
                sprites.add(new TileImage(sprite));
            } catch (IOException e)
            {
                System.err.println("Error loading Sprite : " + spritesFileNames[i]);
                // generate a black tile
                sprites.add(new TileImage(
                        new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_BYTE_INDEXED)
                ));
            }
        }
        return sprites;
    }

    /**
     * Load Custom Sprites, file name format : [CharName]_sprite.png
     * if file not found; load default from sprites
     *
     * @return customSprites (ArrayList)
     */
    private ArrayList<TileImage> loadCustomSprites() {
        ArrayList<TileImage> customSprites = new ArrayList<>();

        for (Mouse m : drawEngine.getMouses())
        {
            try
            {
                // Load file
                File spriteFile = new File("res/" + m.getName() + ".png");
                // Read image
                BufferedImage sprite = ImageIO.read(spriteFile);
                // resize
                int type = sprite.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : sprite.getType();
                sprite = resizeImage(sprite, type, TILE_SIZE, TILE_SIZE);
                //add to customSprites
                customSprites.add(new TileImage(sprite));

            } catch (Exception e)
            {
                customSprites.add(sprites.get(MOUSE_SPRITE));
            }
        }

        for (Cat c : drawEngine.getCats())
        {
            try
            {
                // Load file
                File spriteFile = new File("res/" + c.getName() + "_sprite.png");
                // Read image
                BufferedImage sprite = ImageIO.read(spriteFile);
                // resize
                int type = sprite.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : sprite.getType();
                sprite = resizeImage(sprite, type, TILE_SIZE, TILE_SIZE);
                //add to customSprites
                customSprites.add(new TileImage(sprite));

            } catch (Exception e)
            {
                customSprites.add(sprites.get(CAT_SPRITE));
            }
        }
        return customSprites;
    }

    private ArrayList<BufferedImage> loadExplosionFrames() {
				/*Load sprites files */
        ArrayList<BufferedImage> frames = new ArrayList<>();
        final int ROWS = 2;
        final int COLS = 5;

        try
        {
            // Load file
            File spriteFile = new File("res/anim/explosion.png");
            // Read image
            BufferedImage frameSheet = ImageIO.read(spriteFile);

            final int w = frameSheet.getWidth() / COLS;
            final int h = frameSheet.getHeight() / ROWS;

            for (int i = 0; i < ROWS; i++)
            {
                for (int j = 0; j < COLS; j++)
                {
                    BufferedImage frame = frameSheet
                            .getSubimage(
                                    j * w,
                                    i * h,
                                    w,
                                    h
                            );
                    // resize
                    int type = frameSheet.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : frameSheet.getType();
                    frame = resizeImage(frame, type, TILE_SIZE, TILE_SIZE);

                    frames.add(frame);
                }
            }
            for (int i = frames.size() - 1; i >= 0; i--)
                frames.add(frames.get(i));

        } catch (IOException e)
        {
            System.err.println("Error loading Sprite");
        }

        return frames;
    }

    /**
     * Resize image
     *
     * @param originalImage (BufferedImage) image to resize
     * @param type          type of image, use ARGB for tranparancy
     * @param IMG_WIDTH     (int)
     * @param IMG_HEIGHT    (int)
     * @return
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
        Image resizedImage = originalImage.getScaledInstance(IMG_WIDTH, IMG_HEIGHT, Image.SCALE_SMOOTH);

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(resizedImage, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public void update() {
        clearLayer(layers.get(2));    // clear paths
        drawMap();
        drawObjects();
        drawCharacters();
        repaint();
    }

    public void clearLayer(JLabel[][] layer) {
        for (int i = 0; i < map.getHeight(); i++)
            for (int j = 0; j < map.getWidth(); j++)
                setTile(layer[i][j], null);
    }

    public void createAnimation(int x, int y) {
        Animation animation = new Animation(getGraphics());

        animation.setAnimation(explosionFrames,
                x * TILE_SIZE + TILE_SIZE / 2,
                y * TILE_SIZE + TILE_SIZE / 2);
        Thread animThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!animation.isOver())
                {
                    animation.draw();
                    try
                    {
                        Thread.sleep(50);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    animation.nextFrame();
                    if (animation.isOver())
                        repaint();
                }
            }
        });
        animThread.start();
    }

    public void drawMap() {
        JLabel[][] layer1 = layers.get(0);

        for (int i = 0; i < map.getHeight(); i++)
        {
            for (int j = 0; j < map.getWidth(); j++)
            {
                // Choose tile
                switch (map.getTile(j, i))
                {
                    case NOT_DISCOVERED:
                        setTile(layer1[i][j], sprites.get(NOT_DISCOVERED_SPRITE));
                        break;
                    case EMPTY:
                        setTile(layer1[i][j], sprites.get(EMPTY_SPRITE));
                        break;
                    case WALL:
                        setTile(layer1[i][j], sprites.get(WALL_SPRITE));
                        break;
                    default:
                        setTile(layer1[i][j], sprites.get(EMPTY_SPRITE));
                        break;
                }
            }
        }
    }

    public void drawObjects() {
        JLabel[][] layer = layers.get(1);

        for (int i = 0; i < map.getHeight(); i++)
        {
            for (int j = 0; j < map.getWidth(); j++)
            {
                // Choose tile
                switch (map.getTile(j, i))
                {
                    case NOT_DISCOVERED:
                    case EMPTY:
                    case WALL:
                    case CAT:
                    case MOUSE:
                        setTile(layer[i][j], null);
                        break;
                    case CHEESE:
                        setTile(layer[i][j], sprites.get(CHEESE_SPRITE));
                        break;
                    case POWERUP_VISION:
                        setTile(layer[i][j], sprites.get(POWERUP_VISION_SPRITE));
                        break;
                    case POWERUP_SPEED:
                        setTile(layer[i][j], sprites.get(POWERUP_SPEED_SPRITE));
                        break;
                    case INVISIBLE_ZONE:
                        setTile(layer[i][j], sprites.get(INVISIBLE_ZONE_SPRITE));
                        break;
                    case MINE:
                        setTile(layer[i][j], sprites.get(MINE_SPRITE));
                        break;
                    default:
                        setTile(layer[i][j], null);
                        break;
                }
            }
        }
    }

    public void drawCharacters() {
        // for each mouse
        int index = 0;
        JLabel[][] layer = layers.get(CHARACTER_LAYER);
        for (Mouse m : drawEngine.getMouses())
        {
            setTile(
                    layer[m.getPosition().getPosY()][m.getPosition().getPosX()],
                    customSprites.get(index)
            );
            index++;
        }
        // for each cat
        for (Cat c : drawEngine.getCats())
        {
            setTile(
                    layer[c.getPosition().getPosY()][c.getPosition().getPosX()],
                    customSprites.get(index)
            );
            index++;
        }
    }

    public void drawPoint(int x, int y, Color color) {
    }


    public void drawCharacterPaths() {
        int index = 0;
        for (Mouse mouse : drawEngine.getMouses())
        {
            try
            {
                drawPath(mouse.getDestinationPath(),
                        customSprites.get(index));
            } catch (ConcurrentModificationException e)
            {
            }
            index++;
        }
        for (Cat cat : drawEngine.getCats())
        {
            try
            {
                drawPath(cat.getDestinationPath(), customSprites.get(index));
            } catch (ConcurrentModificationException e)
            {
            }
            index++;
        }
    }

    public void drawPath(ArrayList<Position> path) {
        drawPath(path, sprites.get(CAT_SPRITE));
    }

    public void drawPath(ArrayList<Position> path, TileImage sprite) {
        if (path == null || path.isEmpty()) return;

        JLabel[][] layer = layers.get(2);

        TileImage img = sprite.copy();
        img.setAlpha(0.25f);

        path = (ArrayList<Position>) path.clone();

        for (Position t : path)
        {
            if (layer[t.getPosY()][t.getPosX()].getIcon() == null)
                setTile(layer[t.getPosY()][t.getPosX()], img);
        }
    }

    private void setTile(JLabel tile, TileImage img) {
        if (tile.getIcon() != img)
        {
            tile.setIcon(img);
        }
    }

}