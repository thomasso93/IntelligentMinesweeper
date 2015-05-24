package GameMapGenerator;

import PathFinding.GameMap;

import java.util.*;

public class MapGenerator {
    /** Max number of individuals in population */
    private static final int POPULATION_SIZE = 40;
    /** Max number of generations */
    private static final int GENERATIONS = 50;
    /** Random initial population */
    private List<GameMap> basePopulation;
    /** Best individuals from population */
    private List<GameMap> selectedPopulation;
    /** Evaluations for each individual */
    private TreeMap<Double, GameMap> evaluations;

    /**
     * Constructor for MapGenerator
     * @param previousIndividuals individuals to add to initial populations
     */
    public MapGenerator(GameMap[] previousIndividuals) {
        basePopulation = new ArrayList<GameMap>();
        selectedPopulation = new ArrayList<GameMap>();
        evaluations = new TreeMap<Double, GameMap>(Collections.reverseOrder());
        generateInitialPopulation(previousIndividuals);
    }

    /** Generate better map using genetic algorithm */
    public GameMap generateMap() {
        for (int i = 0; i < GENERATIONS; i++) {
            evaluation();
            selection();
            crossover();
        }
        return bestIndividual();
    }

    /** Evaluation of each individual from population */
    private void evaluation() {
        resetEvaluations();

        for (GameMap individual : getBasePopulation()) {
            double fitness = calculateFitness(individual);
            getEvaluations().put(fitness, individual);
        }
    }

    /**
     * Calculate fitness for individual
     * @param individual individual for which we calculate fitness
     * @return fitness for individual
     */
    private double calculateFitness(GameMap individual) {
        int[] units = MapCalculator.matrixToArray(individual.getUnits(), GameMap.HEIGHT, GameMap.WIDTH);
        int numberOfEasyBombs = MapCalculator.occurrences(units, GameMap.EASY_BOMB);
        int numberOfMediumBombs = MapCalculator.occurrences(units, GameMap.MEDIUM_BOMB);
        int numberOfHardBombs = MapCalculator.occurrences(units, GameMap.HARD_BOMB);

        return 0.2 * numberOfEasyBombs + 0.4 * numberOfMediumBombs + 0.6 * numberOfHardBombs;
    }

    /** Selection of best individuals from population */
    private void selection() {
        int selectedPopulationSize = POPULATION_SIZE / 2;
        List<GameMap> population = new ArrayList<GameMap>();
        Iterator iterator = getEvaluations().entrySet().iterator();

        for (int i = 0; i < selectedPopulationSize; i++) {
            Map.Entry element = (Map.Entry) iterator.next();
            population.add((GameMap) element.getValue());
        }

        setSelectedPopulation(population);
    }

    /** Creating new individuals from selected population */
    private void crossover() {
        List<GameMap> population = getSelectedPopulation();
        List<GameMap> children = new ArrayList<GameMap>();
        Iterator iterator = population.iterator();

        while (iterator.hasNext()) {
            GameMap firstParent = (GameMap) iterator.next();
            GameMap secondParent = (GameMap) iterator.next();
            Collections.addAll(children, createChildren(firstParent, secondParent));
        }

        setBasePopulation(getSelectedPopulation(), true);
        setBasePopulation(children, false);
    }

    /**
     * Create children from parents
     * @param firstParent first parent
     * @param secondParent second parent
     * @return children created from parent
     */
    private GameMap[] createChildren(GameMap firstParent, GameMap secondParent) {
        int[] firstParentUnits = MapCalculator.matrixToArray(firstParent.getUnits(), GameMap.HEIGHT, GameMap.WIDTH);
        int[] secondParentUnits = MapCalculator.matrixToArray(secondParent.getUnits(), GameMap.HEIGHT, GameMap.WIDTH);
        int size = firstParentUnits.length;
        int indexToSplit = MapCalculator.randomIndexForSize(size, 0.4, 0.6);
        GameMap firstChild = new GameMap();
        GameMap secondChild = new GameMap();

        int[] firstChildUnits = MapCalculator.mixArrays(firstParentUnits, secondParentUnits, indexToSplit, false);
        firstChild.setUnits(MapCalculator.arrayToMatrix(firstChildUnits, GameMap.WIDTH));
        int[] secondChildUnits = MapCalculator.mixArrays(firstParentUnits, secondParentUnits, indexToSplit, true);
        secondChild.setUnits(MapCalculator.arrayToMatrix(secondChildUnits, GameMap.WIDTH));

        return new GameMap[]{mutation(firstChild), mutation(secondChild)};
    }

    /**
     * Mutating new individual
     * @param individual individual to mutate
     * @return mutated individual
     */
    private GameMap mutation(GameMap individual) {
        int[] units = MapCalculator.matrixToArray(individual.getUnits(), GameMap.HEIGHT, GameMap.WIDTH);

        units = MapCalculator.replaceRandomElement(units, GameMap.UNIT_FIELDS, 1);
        individual.setUnits(MapCalculator.arrayToMatrix(units, GameMap.WIDTH));

        return individual;
    }

    /** @return best individual from population */
    private GameMap bestIndividual() {
        evaluation();
        return getEvaluations().firstEntry().getValue();
    }

    /**
     * Generate initial population
     * @param previousIndividuals individuals to add to initial populations
     */
    private void generateInitialPopulation(GameMap[] previousIndividuals) {
        List<GameMap> population = new ArrayList<GameMap>();
        if (previousIndividuals != null) Collections.addAll(population, previousIndividuals);

        for (int i = population.size(); i < POPULATION_SIZE; i++) {
            GameMap map = new GameMap();
            map.randomMap();
            population.add(map);
        }
        
        setBasePopulation(population, true);
    }

    private List<GameMap> getBasePopulation() { return basePopulation; }

    /**
     * Setter for population
     * @param population individuals to add to current population
     * @param clear if true all current indivuduals will be removed
     */
    private void setBasePopulation(List<GameMap> population, boolean clear) {
        if (clear) this.basePopulation.clear();
        this.basePopulation.addAll(population);
    }

    private List<GameMap> getSelectedPopulation() { return selectedPopulation; }

    private void setSelectedPopulation(List<GameMap> selectedPopulation) {
        this.selectedPopulation.clear();
        this.selectedPopulation.addAll(selectedPopulation);
    }

    private TreeMap<Double, GameMap> getEvaluations() {
        return evaluations;
    }

    private void resetEvaluations() {
        this.evaluations = new TreeMap<Double, GameMap>(Collections.reverseOrder());
    }
}
