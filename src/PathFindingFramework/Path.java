package PathFindingFramework;

import java.util.ArrayList;

public class Path {

    private ArrayList steps = new ArrayList();

    public Path() {
    }

    public int getLength() {
        return steps.size();
    }

    public State getStep(int index) {
        return (State) steps.get(index);
    }

    public int getX(int index) {
        return getStep(index).x;
    }

    public int getY(int index) {
        return getStep(index).y;
    }

    public void appendStep(int x, int y, PathFinder.Action action) {
        steps.add(new State(x, y, action));
    }

    public void prependStep(int x, int y,PathFinder.Action action) {
        steps.add(0, new State(x, y, action));
    }
    
    public void removeStep(int step) {
        if(step < steps.size())
            steps.remove(step);
    }
    public boolean contains(int x, int y) {
        return steps.contains(new State(x, y));
    }

    public class State {

        private int x;
        private int y;
        private PathFinder.Action action;

       public State(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public State(int x, int y, PathFinder.Action action) {
            this.x = x;
            this.y = y;
            this.action = action;
        }

       public PathFinder.Action getAction() {
            return action;
        }
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public int hashCode() {
            return x * y;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof State) {
                State o = (State) other;

                return (o.x == x) && (o.y == y);
            }

            return false;
        }
    }
}