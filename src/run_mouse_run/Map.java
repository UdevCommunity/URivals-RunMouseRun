package run_mouse_run;

import java.util.ArrayList;

/**
 * Created by Oussama on 09/07/2017.
 */
public class Map
{
    private Tile[][] map;

    private String name;
    private int height;
    private int width;

    public Map(String name, int width, int height, Tile defaultTile)
    {
        this.name = name;
        this.height = height;
        this.width = width;
        this.map = new Tile[height][width];

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                map[j][i] = defaultTile;
    }

    public Tile getTile(int x, int y)
    {
        return map[y][x];
    }

    public void setTile(int x, int y, Tile tile)
    {
        map[y][x] = tile;
    }

    public void switchTile(int x, int y)
    {
        map[y][x] = map[y][x].next();
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public String getName()
    {
        return name;
    }

    public ArrayList<Position> getSpecialTilesPosition(Tile tile)
    {
        ArrayList<Position> cheesesPosition = new ArrayList<>();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (getTile(i, j) == tile)
                    cheesesPosition.add(new Position(i, j));

        return cheesesPosition;
    }
}
