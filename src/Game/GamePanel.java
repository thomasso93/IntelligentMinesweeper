package Game;

import GameMapGenerator.MapGenerator;
import PathFinding.GameMap;
import PathFinding.UnitMover;
import PathFindingFramework.AStarPathFinder;
import PathFindingFramework.Path;
import PathFindingFramework.PathFinder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GamePanel extends JPanel {

    private Minesweeper minesweeper;
    private Point posOnMap;
    private GameMap map;
    private MapGenerator mapGenerator = new MapGenerator();
    private ArrayList<GameMap> previousMaps = new ArrayList<>();;
    private Image[] tiles = new Image[9];
    private Point tileSize;
    private PathFinder finder;
    private Path path;
    private int frameCount = 0;
    private int fps = 0;
    private float interpolation;
    public boolean gameOver = false;

    private InputStream getResource(String ref) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(ref);
        if (in != null) {
            return in;
        }
        return new FileInputStream(ref);
    }

    private void loadImages() {
        try {
            tiles[GameMap.EMPTY_FIELD] = ImageIO.read(getResource("res/emptyField.png"));
            tiles[GameMap.SCANNED_FIELD] = ImageIO.read(getResource("res/scannedField.png"));
            tiles[GameMap.UNKNOWN_FIELD] = ImageIO.read(getResource("res/unknownField.png"));
            tiles[GameMap.MINESWEEPER] = ImageIO.read(getResource("res/minesweeper.png"));
            tiles[GameMap.EASY_BOMB] = ImageIO.read(getResource("res/easyBomb.png"));
            tiles[GameMap.MEDIUM_BOMB] = ImageIO.read(getResource("res/mediumBomb.png"));
            tiles[GameMap.HARD_BOMB] = ImageIO.read(getResource("res/hardBomb.png"));
            tiles[GameMap.TRACKS] = ImageIO.read(getResource("res/tracks.png"));
            tiles[GameMap.CURVED_TRACKS] = ImageIO.read(getResource("res/curvedTracks.png"));
            tileSize = new Point(tiles[0].getWidth(this), tiles[0].getHeight(this));
        } catch (IOException e) {
            System.err.println("Failed to load resources: " + e.getMessage());
            System.exit(0);
        }
    }

    private void makeMinesweeper() {
        for (int x = 0; x < map.getWidthInTiles(); x++) {
            for (int y = 0; y < map.getHeightInTiles(); y++) {
                if (map.getUnit(x, y) == GameMap.MINESWEEPER) {
                    minesweeper = new Minesweeper(new Point(x * tileSize.x, y * tileSize.y));
                    posOnMap = new Point(x, y);
                }
            }
        }
    }

    private void drawBars(Graphics g) {
        int healthBar = (int) (((float) minesweeper.getHealth() / 100) * 25);
        int fuelBar = (int) (((float) minesweeper.getFuel() / 100) * 25);
        int x = minesweeper.getPos().x + 4;
        int y = minesweeper.getPos().y;
        if (y - 18 < 0) {
            y += 50;
        }
        g.setColor(Color.RED);
        g.fillRect(x - 1, y - 18, 26, 10);
        g.setColor(Color.black);
        g.drawRect(x - 1, y - 18, 26, 10);
        g.drawLine(x - 1, y - 13, x + 25, y - 13);

        g.setColor(Color.green);
        g.fillRect(x, y - 17, healthBar, 4);

        g.setColor(Color.decode("#582072"));
        g.fillRect(x, y - 12, fuelBar, 4);
    }

    public GamePanel() {
        loadImages();
        map = mapGenerator.generateMap(previousMaps);
        previousMaps.add(map);
        makeMinesweeper();
        finder = new AStarPathFinder(map, 500, false);
        path = finder.findPath(new UnitMover(minesweeper.getPersonality()), posOnMap.x, posOnMap.y, 0, 0);

        Dimension dim = new Dimension(map.getWidthInTiles() * tileSize.x, map.getHeightInTiles() * tileSize.y);
        setPreferredSize(dim);
    }

    public void drawGame(float interp) {
        interpolation = interp;
        repaint();
    }

    public void update() {
        Point newPosOnMap = new Point(minesweeper.getPos().x / tileSize.x, minesweeper.getPos().y / tileSize.y);

        if (path.getLength() != 0) {
            Point lastNode = new Point(path.getStep(0).getX() * tileSize.x, path.getStep(0).getY() * tileSize.y);

            if (lastNode.x > minesweeper.getPos().x) {
                minesweeper.move(1, 0);
            } else if (lastNode.x < minesweeper.getPos().x) {
                minesweeper.move(-1, 0);
            } else if (lastNode.y > minesweeper.getPos().y) {
                minesweeper.move(0, 1);
            } else if (lastNode.y < minesweeper.getPos().y) {
                minesweeper.move(0, -1);
            } else if (lastNode.x == minesweeper.getPos().x && lastNode.y == minesweeper.getPos().y) {
                if (path.getLength() != 0) {
                    path.removeStep(0);
                }
            }
        }
        if (posOnMap.x != newPosOnMap.x) {
            minesweeper.harm(map.getUnit(newPosOnMap));
            minesweeper.decreaseFuel(1);

            map.setUnit(newPosOnMap, GameMap.MINESWEEPER);
            map.setTerrain(newPosOnMap, GameMap.EMPTY_FIELD);
            map.setUnit(posOnMap, 0);
            posOnMap = newPosOnMap;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        for (int x = 0; x < map.getWidthInTiles(); x++) {
            for (int y = 0; y < map.getHeightInTiles(); y++) {
                g.drawImage(tiles[map.getTerrain(x, y)], x * tileSize.x, y * tileSize.y, null);
                if (map.getTerrain(x, y) == GameMap.EMPTY_FIELD && map.getUnit(x, y) != GameMap.MINESWEEPER) {
                    g.drawImage(tiles[map.getUnit(x, y)], x * tileSize.x, y * tileSize.y, null);
                }
                if (path != null) {
                    if (path.contains(x, y)) {
                        g.setColor(Color.blue);
                        g.fillRect((x * tileSize.x) + 4, (y * tileSize.y) + 4, 7, 7);
                    }
                }

            }
        }
        g.drawImage(tiles[GameMap.MINESWEEPER], minesweeper.getPos().x, minesweeper.getPos().y, null);
        drawBars(g);
        g.setColor(Color.BLUE);
        Font font = new Font("Comic Sans MS", Font.BOLD, 20);
        g.setFont(font);
        g.drawString("FPS: " + fps, 5, 30);
        frameCount++;
    }

    public int GetFrameCount() {
        return frameCount;
    }

    public void SetFPS(int fps) {
        this.fps = fps;
    }

    public void SetFrameCount(int fc) {
        frameCount = fc;
    }
}
