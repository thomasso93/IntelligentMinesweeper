package PathFinding;

import Game.Minesweeper;
import Game.Minesweeper.Personality;
import PathFindingFramework.Mover;
import PathFindingFramework.TileBasedMap;
import java.awt.Point;
import java.util.Random;

public class GameMap implements TileBasedMap {

    private Random rand = new Random();
    private int[][] terrain = new int[WIDTH][HEIGHT];
    private int[][] units = new int[WIDTH][HEIGHT];
    private boolean[][] visited = new boolean[WIDTH][HEIGHT];
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    public static final int EMPTY_FIELD = 0;
    public static final int SCANNED_FIELD = 1;
    public static final int UNKNOWN_FIELD = 2;
    public static final int MINESWEEPER = 3;
    public static final int EASY_BOMB = 4;
    public static final int MEDIUM_BOMB = 5;
    public static final int HARD_BOMB = 6;
    public static final int TRACKS = 7;
    public static final int CURVED_TRACKS = 8;
    public static final int MAX_NUMBER_OF_BOMBS = (int) (0.55 * WIDTH * HEIGHT);

    public GameMap() {
        randomMap();
    }

    private void fillArea(int x, int y, int width, int height, int type) {
        for (int xp = x; xp < x + width; xp++) {
            for (int yp = y; yp < y + height; yp++) {
                terrain[xp][yp] = type;
            }
        }
    }

    public void clearVisited() {
        for (int x = 0; x < getWidthInTiles(); x++) {
            for (int y = 0; y < getHeightInTiles(); y++) {
                visited[x][y] = false;
            }
        }
    }

    @Override
    public boolean blocked(Mover mover, int x, int y) {
        Minesweeper.Personality personality = ((UnitMover) mover).getPersonality();

        if (personality == Personality.CLEVER) {
            boolean isBomb = units[x][y] == EASY_BOMB || units[x][y] == MEDIUM_BOMB || units[x][y] == HARD_BOMB;
            return terrain[x][y] == SCANNED_FIELD || (terrain[x][y] == EMPTY_FIELD && isBomb);
        }
        if (personality == Personality.MAD) {
            boolean isBomb = units[x][y] == EASY_BOMB || units[x][y] == MEDIUM_BOMB || units[x][y] == HARD_BOMB;
            return terrain[x][y] == EMPTY_FIELD && isBomb;
        }
        return false;
    }

    public boolean isVisited(int x, int y) {
        return visited[x][y];
    }

    public int getTerrain(int x, int y) {
        return terrain[x][y];
    }

    public int getUnit(int x, int y) {
        return units[x][y];
    }

    public boolean isVisited(Point point) {
        return visited[point.x][point.y];
    }

    public int getTerrain(Point point) {
        return terrain[point.x][point.y];
    }

    public int getUnit(Point point) {
        return units[point.x][point.y];
    }

    public void setUnit(int x, int y, int unit) {
        units[x][y] = unit;
    }

    public void setTerrain(int x, int y, int unit) {
        terrain[x][y] = unit;
    }

    public void setUnit(Point point, int unit) {
        units[point.x][point.y] = unit;
    }
    
    public void setTerrain(Point point, int unit) {
        terrain[point.x][point.y] = unit;
    }

    public int[][] getTerrains() { return terrain; }

    public int[][] getUnits() { return units; }

    @Override
    public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
        return 1;
    }

    @Override
    public int getHeightInTiles() {
        return HEIGHT;
    }

    @Override
    public int getWidthInTiles() {
        return WIDTH;
    }

    @Override
    public void pathFinderVisited(int x, int y) {
        visited[x][y] = true;
    }

    public void randomMap() {
        int area = WIDTH * HEIGHT;
        int numberOfEasyBombs = rand.nextInt(maxNumberOfBombs(EASY_BOMB));
        int numberOfMediumBombs = rand.nextInt(maxNumberOfBombs(MEDIUM_BOMB));
        int numberOfHardBombs = rand.nextInt(maxNumberOfBombs(HARD_BOMB));

        for (int i = 0; i <  area; i++) {
            terrain[rand.nextInt(WIDTH)][rand.nextInt(HEIGHT)] = UNKNOWN_FIELD;
        }
        for (int i = 0; i < 0.05 * area; i++) {
            int x=rand.nextInt(WIDTH-1);
            int y=rand.nextInt(HEIGHT-1);
            terrain[x+1][y+1] = SCANNED_FIELD;
            terrain[x][y+1] = SCANNED_FIELD;
            terrain[x+1][y] = SCANNED_FIELD;
            terrain[x][y] = SCANNED_FIELD;
        }
        for (int i = 0; i < numberOfEasyBombs; i++) {
            units[rand.nextInt(WIDTH)][rand.nextInt(HEIGHT)] = EASY_BOMB;
        }
        for (int i = 0; i < numberOfMediumBombs; i++) {
            units[rand.nextInt(WIDTH)][rand.nextInt(HEIGHT)] = MEDIUM_BOMB;
        }
        for (int i = 0; i < numberOfHardBombs; i++) {
            units[rand.nextInt(WIDTH)][rand.nextInt(HEIGHT)] = HARD_BOMB;
        }
        units[rand.nextInt(WIDTH)][rand.nextInt(HEIGHT)] = TRACKS;
        units[rand.nextInt(WIDTH)][rand.nextInt(HEIGHT)] = CURVED_TRACKS;
        units[rand.nextInt(WIDTH)][rand.nextInt(HEIGHT)] = MINESWEEPER;
    }

    private int maxNumberOfBombs(int bomb) {
        switch (bomb) {
            case EASY_BOMB: return (int) (0.7 * MAX_NUMBER_OF_BOMBS);
            case MEDIUM_BOMB: return (int) (0.2 * MAX_NUMBER_OF_BOMBS);
            case HARD_BOMB: return (int) (0.1 * MAX_NUMBER_OF_BOMBS);
            default: return 0;
        }
    }
}
