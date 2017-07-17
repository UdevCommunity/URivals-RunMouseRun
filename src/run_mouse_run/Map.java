package run_mouse_run;

import java.util.ArrayList;

public class Map
{
    private Tile[][] map;
    private Position[][] positionsMap;

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
            {
                map[j][i] = defaultTile;
                positionsMap[j][i] = new Position(i, j);
            }
    }

    public Position getPosition(int x, int y)
    {
        return positionsMap[y][x];
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Tile getTile(int x, int y)
    {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return null;

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

    public void clear(Tile tile)
    {
        for(int i = 0; i < width; i++)
            for(int j = 0; j < height; j++)
                setTile(i,j, tile);
    }

    public ArrayList<Position> getSpecialTilesPosition(Tile tile)
    {
        ArrayList<Position> specialTilesPosition = new ArrayList<>();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (getTile(i, j) == tile)
                    specialTilesPosition.add(new Position(i, j));

        return specialTilesPosition;
    }

    public ArrayList<Tile> getAdjacentsTile(int x, int y)
    {
        ArrayList<Tile> adjacentsTile = new ArrayList<>();

        for (int i = x - 1; i <= x + 1; i++)
            for (int j = y - 1; j <= y + 1; j++)
                if(getTile(i, j) != null && x != i && y != j)
                    adjacentsTile.add(getTile(i, j));

        return adjacentsTile;
    }

    public ArrayList<Position> getBorders()
    {
        ArrayList<Position> borders = new ArrayList<>();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (getTile(i, j) != Tile.NOT_DISCOVERED && getAdjacentsTile(i, j).contains(Tile.NOT_DISCOVERED))
                    borders.add(new Position(i, j));

        return borders;
    }

    public Map copy()
    {
        Map mapCopy = new Map(name, width, height, Tile.NOT_DISCOVERED);

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                mapCopy.setTile(i, j, getTile(i, j));

        return mapCopy;
    }
}
