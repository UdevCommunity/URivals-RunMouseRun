package run_mouse_run.draw_engine;

import run_mouse_run.Cat;
import run_mouse_run.DrawEngine;
import run_mouse_run.FileManager;
import run_mouse_run.Mouse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class DE_GameSprites
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
            "mouse_sprite.png",
            "mouse_sprite_dead.png",
            "flag.png"
    };

    public static final int NOT_DISCOVERED_SPRITE = 0, EMPTY_SPRITE = 1,
            WALL_SPRITE = 2, CHEESE_SPRITE = 3,
            POWERUP_VISION_SPRITE = 5, POWERUP_SPEED_SPRITE = 4,
            INVISIBLE_ZONE_SPRITE = 6, MINE_SPRITE = 7,
            CAT_SPRITE = 8, MOUSE_SPRITE = 9, MOUSE_SPRITE_DEAD = 10,
            FLAG_SPRITE = 11;
    ;

    private ArrayList<DE_TileImage> sprites, customSprites, spritesDead;
    private ArrayList<BufferedImage> spritesOriginal, customSpritesOriginal, spritesDeadOriginal;

    private final String[] animationFileNames = {
            "explosion.png",
            "stun.png"
    };

    public static final int EXPLOSION_FRAMES = 0, STUN_FRAMES = 1;

    private ArrayList<ArrayList<DE_TileImage>> animationFrames;
    private ArrayList<ArrayList<BufferedImage>> animationFramesOriginal;

    public DE_GameSprites(DrawEngine drawEngine, int TILE_SIZE)
    {
        this.drawEngine = drawEngine;
        this.TILE_SIZE = TILE_SIZE;

        /*Load original files*/
        spritesOriginal = loadSprites();
        customSpritesOriginal = loadCustomSprites();
        spritesDeadOriginal = loadCustomDeadSprites();

        animationFramesOriginal = loadAnimationFrames();


        animationFrames = new ArrayList<>();
        sprites = new ArrayList<>();
        customSprites = new ArrayList<>();
        spritesDead = new ArrayList<>();

        resizeSprites(TILE_SIZE);
    }

    private ArrayList<BufferedImage> loadSprites() {
        /*Load sprites files */
        ArrayList<BufferedImage> sprites = new ArrayList<>();

        for (int i = 0; i < spritesFileNames.length; i++)
        {
            try
            {
                // Load file
                File spriteFile = FileManager.getResourceFile(spritesFileNames[i]);
                // Read image
                BufferedImage sprite = ImageIO.read(spriteFile);
                //add to sprites
                sprites.add(sprite);
            } catch (IOException e)
            {
                System.err.println("Error loading Sprite : " + spritesFileNames[i]);
                // generate a black tile
                sprites.add(
                        new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_BYTE_INDEXED)
                );
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
    private ArrayList<BufferedImage> loadCustomSprites() {
        ArrayList<BufferedImage> customSprites = new ArrayList<>();

        for (Mouse m : drawEngine.getMouses())
        {
            try
            {
                // Load file
                File spriteFile = FileManager.getResourceFile(m.getName() + ".png");
                // Read image
                BufferedImage sprite = ImageIO.read(spriteFile);
                //add to customSprites
                customSprites.add(sprite);
            } catch (Exception e)
            {
                customSprites.add(spritesOriginal.get(MOUSE_SPRITE));
            }
        }

        for (Cat c : drawEngine.getCats())
        {
            try
            {
                // Load file
                File spriteFile = FileManager.getResourceFile(c.getName() + "_sprite.png");
                // Read image
                BufferedImage sprite = ImageIO.read(spriteFile);
                //add to customSprites
                customSprites.add(sprite);
            } catch (Exception e)
            {
                customSprites.add(spritesOriginal.get(CAT_SPRITE));
            }
        }
        return customSprites;
    }

    /**
     * Load Custom Sprites, file name format : [CharName]_sprite.png
     * if file not found; load default from sprites
     *
     * @return customSprites (ArrayList)
     */
    private ArrayList<BufferedImage> loadCustomDeadSprites() {
        ArrayList<BufferedImage> customDeadSprites = new ArrayList<>();

        for (Mouse m : drawEngine.getMouses())
        {
            try
            {
                // Load file
                File spriteFile = FileManager.getResourceFile( m.getName() + "_sprite_dead.png");
                // Read image
                BufferedImage sprite = ImageIO.read(spriteFile);
                //add to customSprites
                customDeadSprites.add(sprite);
            } catch (Exception e)
            {
                customDeadSprites.add(spritesOriginal.get(MOUSE_SPRITE_DEAD));
            }
        }

        for (Cat c : drawEngine.getCats())
        {
            try
            {
                // Load file
                File spriteFile = FileManager.getResourceFile(c.getName() + "_sprite_dead.png");
                // Read image
                BufferedImage sprite = ImageIO.read(spriteFile);
                //add to customSprites
                customDeadSprites.add(sprite);
            } catch (Exception e)
            {
                customDeadSprites.add(spritesOriginal.get(CAT_SPRITE));
            }
        }
        return customDeadSprites;
    }

    private ArrayList<ArrayList<BufferedImage>> loadAnimationFrames()
    {
        ArrayList<ArrayList<BufferedImage>> animationFrames = new ArrayList<>();

        animationFrames.add(loadAnimation(EXPLOSION_FRAMES, 2, 5, 1, true));
        animationFrames.add(loadAnimation(STUN_FRAMES, 2, 3, 5, false));

        return animationFrames;
    }

    /**
     * Load animation frames from a spriteSheet into a list of separate frames
     * @param frameIndex index of spritesheet's filename from animationFileNames
     * @param rows number of rows in spritesheet
     * @param cols number of cols in spritesheet
     * @param repeat number of repetition for that animation ( loops ) // temp fix
     * @param reverse if true, add a reverse animation at the end
     * @return ArrayList of BufferedImage of the loaded frames
     */
    private ArrayList<BufferedImage> loadAnimation(int frameIndex, int rows, int cols, int repeat, boolean reverse)
    {
        ArrayList<BufferedImage> frames = new ArrayList<>();
        final int ROWS = rows;
        final int COLS = cols;

        try
        {
            // Load file
            File spriteFile = FileManager.getResourceFile("/anim/" + animationFileNames[frameIndex]);
            // Read image
            BufferedImage frameSheet = ImageIO.read(spriteFile);

            final int w = frameSheet.getWidth() / COLS;
            final int h = frameSheet.getHeight() / ROWS;

            for(int k = 0; k < repeat; k++)
            {
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
                        frames.add(frame);
                    }
                }

                if (reverse)
                {
                    /*if too lazy to change the spritesheet, read in reverse to make a full animation*/
                    for (int i = frames.size() - 1; i >= 0; i--)
                        frames.add(frames.get(i));
                }
            }
        } catch (IOException e)
        {
            System.err.println("Error loading animation sprite" + animationFileNames[frameIndex]);
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
    static BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
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

    public void resizeSprites(int size)
    {
        this.TILE_SIZE = size;

        /* Resize sprites*/
        ArrayList<DE_TileImage> sprites = new ArrayList<>();

        for(BufferedImage sprite : spritesOriginal)
        {
            // resize
            int type = sprite.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : sprite.getType();
            sprite = resizeImage(sprite, type, TILE_SIZE, TILE_SIZE);

            sprites.add(new DE_TileImage(sprite));
        }

        this.sprites = sprites;

        /*Resize custom Sprites*/
        ArrayList<DE_TileImage> customSprites = new ArrayList<>();
        for(BufferedImage sprite : customSpritesOriginal)
        {
            // resize
            int type = sprite.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : sprite.getType();
            sprite = resizeImage(sprite, type, TILE_SIZE, TILE_SIZE);

            customSprites.add(new DE_TileImage(sprite));
        }

        this.customSprites = customSprites;

        /*Resize dead Sprites*/
        ArrayList<DE_TileImage> spritesDead = new ArrayList<>();
        for(BufferedImage sprite : spritesDeadOriginal)
        {
            // resize
            int type = sprite.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : sprite.getType();
            sprite = resizeImage(sprite, type, TILE_SIZE, TILE_SIZE);

            spritesDead.add(new DE_TileImage(sprite));
        }

        this.spritesDead = spritesDead;

        /*Resize animation frames*/

        ArrayList<ArrayList<DE_TileImage>> animationFrames = new ArrayList<>();

        int i = 0;
        for(ArrayList<BufferedImage> animation : animationFramesOriginal)
        {
            animationFrames.add(new ArrayList<>());
            for(BufferedImage frame : animation)
            {
                // resize
                int type = frame.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : frame.getType();
                frame = resizeImage(frame, type, TILE_SIZE, TILE_SIZE);

                animationFrames.get(i).add(new DE_TileImage(frame));
            }
            i++;
        }

        this.animationFrames = animationFrames;
    }

    public boolean isLoaded()
    {
        return (sprites != null && !sprites.isEmpty());
    }

    public DE_TileImage getSprite(int index)
    {
        return sprites.get(index);
    }

    public DE_TileImage getCustomSprite(int index)
    {
        return customSprites.get(index);
    }

    public DE_TileImage getDeadSprite(int index)
    {
        return spritesDead.get(index);
    }

    public ArrayList<DE_TileImage> getAnimationFrames(int index)
    {
        return animationFrames.get(index);
    }
}
