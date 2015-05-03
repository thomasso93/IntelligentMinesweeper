package PathFindingFramework;

import java.util.ArrayList;

public class Path {

    private ArrayList steps = new ArrayList();

    public Path() {
    }

    public int getLength() {
        return steps.size();
    }

    public Step getStep(int index) {
        return (Step) steps.get(index);
    }

    public int getX(int index) {
        return getStep(index).x;
    }

    public int getY(int index) {
        return getStep(index).y;
    }

    public void appendStep(int x, int y) {
        steps.add(new Step(x, y));
    }

    public void prependStep(int x, int y) {
        steps.add(0, new Step(x, y));
    }
    
    public void removeStep(int step) {
        if(step < steps.size())
            steps.remove(step);
    }
    public boolean contains(int x, int y) {
        return steps.contains(new Step(x, y));
    }

    public class Step {

        private int x;
        private int y;

        public Step(int x, int y) {
            this.x = x;
            this.y = y;
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
            if (other instanceof Step) {
                Step o = (Step) other;

                return (o.x == x) && (o.y == y);
            }

            return false;
        }
    }
}