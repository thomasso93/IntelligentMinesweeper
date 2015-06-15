package PathFindingFramework;

public interface PathFinder {
    public  enum Action {
        E, W, N, S
    }
    public Path findPath(Mover mover, int sx, int sy, int tx, int ty);
}