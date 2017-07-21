package run_mouse_run;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;


/**
 *
 */
public class DE_MapPanel extends JPanel {

    public Map map;
    private DrawEngine drawEngine;

    public int TILE_SIZE;
    private final int BACKGROUND_LAYER = 0,
            OBJECT_LAYER = 1, CHARACTER_LAYER = 2;

    private ArrayList<JLabel[][]> layers;
    private GridLayout gridLayout;

    private DE_GameSprites gameSprites;


    public DE_MapPanel(DrawEngine drawEngine, Map map, int TILE_SIZE)
    {
        this.drawEngine = drawEngine;
        this.map = map;
        this.TILE_SIZE = TILE_SIZE;

        gameSprites = new DE_GameSprites(drawEngine, TILE_SIZE);

        adjustPanelSize();

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
    }

    public void changeTileSize(int size)
    {
        TILE_SIZE = size;

        for(JLabel[][] layer : layers)
        {
            for(int i = 0; i < map.getHeight(); i++)
            {
                for(int j = 0; j < map.getWidth(); j++)
                {
                    layer[i][j].setPreferredSize(
                            new Dimension(TILE_SIZE, TILE_SIZE)
                    );
                    layer[i][j].setSize(
                            new Dimension(TILE_SIZE, TILE_SIZE)
                    );

                }
            }
        }

        gameSprites.resizeSprites(TILE_SIZE);

        adjustPanelSize();
    }

    public void setMap(Map map) {
        this.map = map;
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

    public void createAnimation(int animationIndex, int x, int y)
    {
        JLabel[][] layer = layers.get(2);
        DE_Animation animation = new DE_Animation(
                layer[y][x],
                gameSprites.getAnimationFrames(animationIndex)
        );

        animation.play();
    }

    public void drawMap() {
        JLabel[][] layer1 = layers.get(BACKGROUND_LAYER);

        for (int i = 0; i < map.getHeight(); i++)
        {
            for (int j = 0; j < map.getWidth(); j++)
            {
                // Choose tile
                switch (map.getTile(j, i))
                {
                    case NOT_DISCOVERED:
                        setTile(layer1[i][j], gameSprites.getSprite(DE_GameSprites.NOT_DISCOVERED_SPRITE));
                        break;
                    case EMPTY:
                        setTile(layer1[i][j], gameSprites.getSprite(DE_GameSprites.EMPTY_SPRITE));
                        break;
                    case WALL:
                        setTile(layer1[i][j], gameSprites.getSprite(DE_GameSprites.WALL_SPRITE));
                        break;
                    default:
                        setTile(layer1[i][j], gameSprites.getSprite(DE_GameSprites.EMPTY_SPRITE));
                        break;
                }
            }
        }
    }

    public void drawObjects() {
        JLabel[][] layer = layers.get(OBJECT_LAYER);

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
                        setTile(layer[i][j], gameSprites.getSprite(DE_GameSprites.CHEESE_SPRITE));
                        break;
                    case POWERUP_VISION:
                        setTile(layer[i][j], gameSprites.getSprite(DE_GameSprites.POWERUP_VISION_SPRITE));
                        break;
                    case POWERUP_SPEED:
                        setTile(layer[i][j], gameSprites.getSprite(DE_GameSprites.POWERUP_SPEED_SPRITE));
                        break;
                    case INVISIBLE_ZONE:
                        setTile(layer[i][j], gameSprites.getSprite(DE_GameSprites.INVISIBLE_ZONE_SPRITE));
                        break;
                    case MINE:
                        setTile(layer[i][j], gameSprites.getSprite(DE_GameSprites.MINE_SPRITE));
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
                    gameSprites.getCustomSprite(index)
            );
            index++;
        }
        // for each cat
        for (Cat c : drawEngine.getCats())
        {
            setTile(
                    layer[c.getPosition().getPosY()][c.getPosition().getPosX()],
                    gameSprites.getCustomSprite(index)
            );
            index++;
        }
    }

    public void drawPoint(int x, int y, Color color)
    {
    }


    public void drawCharacterPaths() {
        int index = 0;
        for (Mouse mouse : drawEngine.getMouses())
        {
            try
            {
                drawPath(mouse.getDestinationPath(),
                        gameSprites.getCustomSprite(index));
            } catch (ConcurrentModificationException e)
            {
            }
            index++;
        }
        for (Cat cat : drawEngine.getCats())
        {
            try
            {
                drawPath(cat.getDestinationPath(), gameSprites.getCustomSprite(index));
            } catch (ConcurrentModificationException e)
            {
            }
            index++;
        }
    }

    public void drawPath(ArrayList<Position> path) {
        drawPath(path, gameSprites.getSprite(DE_GameSprites.CAT_SPRITE));
    }

    public void drawPath(ArrayList<Position> path, DE_TileImage sprite) {
        if (path == null || path.isEmpty()) return;

        JLabel[][] layer = layers.get(2);

        DE_TileImage img = sprite.copy();
        img.setAlpha(0.25f);

        path = (ArrayList<Position>) path.clone();

        for (Position t : path)
        {
            if (layer[t.getPosY()][t.getPosX()].getIcon() == null)
                setTile(layer[t.getPosY()][t.getPosX()], img);
        }
    }

    private void setTile(JLabel tile, DE_TileImage img) {
        if (tile.getIcon() != img)
        {
            tile.setIcon(img);
        }
    }

}