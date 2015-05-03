package PathFindingFramework;

import java.util.ArrayList;
import java.util.Collections;

public class AStarPathFinder implements PathFinder {

    private ArrayList closed = new ArrayList();
    private ArrayList open = new ArrayList();
    private TileBasedMap map;
    private int maxSearchDistance;
    private Node[][] nodes;
    private boolean allowDiagMovement;
    private AStarHeuristic heuristic;

    public AStarPathFinder(TileBasedMap map, int maxSearchDistance, boolean allowDiagMovement) {
        this(map, maxSearchDistance, allowDiagMovement, new ClosestHeuristic());
    }

    public AStarPathFinder(TileBasedMap map, int maxSearchDistance,
            boolean allowDiagMovement, AStarHeuristic heuristic) {
        this.heuristic = heuristic;
        this.map = map;
        this.maxSearchDistance = maxSearchDistance;
        this.allowDiagMovement = allowDiagMovement;

        nodes = new Node[map.getWidthInTiles()][map.getHeightInTiles()];
        for (int x = 0; x < map.getWidthInTiles(); x++) {
            for (int y = 0; y < map.getHeightInTiles(); y++) {
                nodes[x][y] = new Node(x, y);
            }
        }
    }

    @Override
    public Path findPath(Mover mover, int sx, int sy, int tx, int ty) {
        if (map.blocked(mover, tx, ty)) {
            return null;
        }
        nodes[sx][sy].cost = 0;
        nodes[sx][sy].depth = 0;
        closed.clear();
        open.clear();
        open.add(nodes[sx][sy]);
        
        nodes[tx][ty].parent = null;
        
        int maxDepth = 0;
        while ((maxDepth < maxSearchDistance) && !open.isEmpty()) {

            Node current = (Node)open.get(0);
            if (current == nodes[tx][ty]) {
                break;
            }

            open.remove(current);
            closed.add(current);

            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    if ((x == 0) && (y == 0)) {
                        continue;
                    }
                    if (!allowDiagMovement) {
                        if ((x != 0) && (y != 0)) {
                            continue;
                        }
                    }

                    int xp = x + current.x;
                    int yp = y + current.y;

                    if (isValidLocation(mover, sx, sy, xp, yp)) {
                         float nextStepCost = current.cost + getMovementCost(mover, current.x, current.y, xp, yp);
                        Node neighbour = nodes[xp][yp];
                        map.pathFinderVisited(xp, yp);

                        if (nextStepCost < neighbour.cost) {
                            if (open.contains(neighbour)) {
                                open.remove(neighbour);
                            }
                            if (closed.contains(neighbour)) {
                                closed.remove(neighbour);
                            }
                        }

                        if (!open.contains(neighbour) && !closed.contains(neighbour)) {
                            neighbour.cost = nextStepCost;
                            neighbour.heuristic = getHeuristicCost(mover, xp, yp, tx, ty);
                            maxDepth = Math.max(maxDepth, neighbour.setParent(current));
                            open.add(neighbour);
                            Collections.sort(open);
                        }
                    }
                }
            }
        }
        if (nodes[tx][ty].parent == null) {
            return null;
        }

        Path path = new Path();
        Node target = nodes[tx][ty];
        while (target != nodes[sx][sy]) {
            path.prependStep(target.x, target.y);
            target = target.parent;
        }
        path.prependStep(sx, sy);
        return path;
    }

    protected boolean isValidLocation(Mover mover, int sx, int sy, int x, int y) {
        boolean invalid = (x < 0) || (y < 0) || (x >= map.getWidthInTiles()) || (y >= map.getHeightInTiles());

        if ((!invalid) && ((sx != x) || (sy != y))) {
            invalid = map.blocked(mover, x, y);
        }

        return !invalid;
    }

    public float getMovementCost(Mover mover, int sx, int sy, int tx, int ty) {
        return map.getCost(mover, sx, sy, tx, ty);
    }

    public float getHeuristicCost(Mover mover, int x, int y, int tx, int ty) {
        return heuristic.getCost(map, mover, x, y, tx, ty);
    }

    private class Node implements Comparable {

        private int x;
        private int y;
        private float cost;
        private Node parent;
        private float heuristic;
        private int depth;
        
        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int setParent(Node parent) {
            depth = parent.depth + 1;
            this.parent = parent;

            return depth;
        }

        @Override
        public int compareTo(Object other) {
            Node o = (Node) other;

            float f = heuristic + cost;
            float of = o.heuristic + o.cost;

            if (f < of) {
                return -1;
            } else if (f > of) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}