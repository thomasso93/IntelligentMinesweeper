package PathFinding;

import PathFindingFramework.AStarHeuristic;
import PathFindingFramework.Mover;
import PathFindingFramework.TileBasedMap;

public class ClosestHeuristic implements AStarHeuristic {

    @Override
    public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {
        float dx = tx - x;
        float dy = ty - y;

        float result = (float) (Math.sqrt((dx * dx) + (dy * dy)));

        return result;
    }
}