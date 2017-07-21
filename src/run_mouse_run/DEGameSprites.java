package run_mouse_run;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Oussama on 21/07/2017.
 */
public class DEGameSprites
{
    private final DrawEngine drawEngine;

    public int TILE_SIZE;

    private final String[] spritesFileNames = {
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

    public static final int NOT_DISCOVERED_SPRITE = 0, EMPTY_SPRITE = 1,
            WALL_SPRITE = 2, CHEESE_SPRITE = 3,
            POWERUP_VISION_SPRITE = 5, POWERUP_SPEED_SPRITE = 4,
            INVISIBLE_ZONE_SPRITE = 6, MINE_SPRITE = 7,
            CAT_SPRITE = 8, MOUSE_SPRITE = 9;

    public static final int EXPLOSION_FRAMES = 0;

    private ArrayList<DETileImage> sprites;
    private ArrayList<DETileImage> customSprites;

    private ArrayList<BufferedImage> explosionFrames;

    public DEGameSprites(DrawEngine drawEngine, int TILE_SIZE)
    {
        this.drawEngine = drawEngine;
        this.TILE_SIZE = TILE_SIZE;

        sprites = loadSprites();
        customSprites = loadCustomSprites();
        explosionFrames = loadExplosionFrames();
    }

    private ArrayList<DETileImage> loadSprites() {
        /*Load sprites files */
        ArrayList<DETileImage> sprites = new ArrayList<>();

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
                sprites.add(new DETileImage(sprite));
            } catch (IOException e)
            {
                System.err.println("Error loading Sprite : " + spritesFileNames[i]);
                // generate a black tile
                sprites.add(new DETileImage(
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
    private ArrayList<DETileImage> loadCustomSprites() {
        ArrayList<DETileImage> customSprites = new ArrayList<>();

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
                customSprites.add(new DETileImage(sprite));

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
                customSprites.add(new DETileImage(sprite));

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


    public boolean isLoaded()
    {
        return (sprites != null && !sprites.isEmpty());
    }

    public DETileImage getSprite(int index)
    {
        return sprites.get(index);
    }

    public DETileImage getCustomSprite(int index)
    {
        return customSprites.get(index);
    }

    public ArrayList<BufferedImage> getAnimationFrames(int index)
    {
        return explosionFrames;
    }
}
