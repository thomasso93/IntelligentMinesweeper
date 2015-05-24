package GameMapGenerator;

import PathFinding.GameMap;

import java.util.Random;

public class MapCalculator {
    private static final int ROWS = GameMap.HEIGHT;
    private static final int COLUMNS = GameMap.WIDTH;

    /**
     *  Transform matrix to array
     *  @param matrix matrix to transform
     *  @return array with all elements from matrix
     */
    public static int[] matrixToArray(int[][] matrix) {
        int[] result = new int[ROWS * COLUMNS];
        int i = 0;

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                result[i++] = matrix[row][column];
            }
        }

        return result;
    }

    /**
     * Transform array to matrix
     * @param array array to transform
     * @return matrix with passed number of columns
     */
    public static int[][] arrayToMatrix(int[] array) {
        int rows = (array.length + COLUMNS - 1) / COLUMNS;
        int[][] matrix = new int[rows][COLUMNS];

        for (int i = 0; i < array.length; i++) {
            matrix[i / COLUMNS][i % COLUMNS] = array[i];
        }

        return matrix;
    }

    /**
     * Count occurrences of object
     * @param array
     * @param object
     * @return number of occurrences of object in array
     */
    public static int occurrences(int[] array, int object) {
        int occurrences = 0;

        for (int x : array) if (object == x) occurrences++;

        return  occurrences;
    }

    /**
     * Count of bomb occurrences
     * @param array
     * @return number of bombs
     */
    public static int bombOccurrences(int[] array) {
        int occurrences = 0;

        for (int x : array) if (x > GameMap.EMPTY_FIELD) occurrences++;

        return  occurrences;
    }

    /**
     * Mix two arrays by taking n elements from first and m elements from second array
     * @param firstArray first array to mix
     * @param secondArray second array to mix
     * @param index where we should split first and second array
     * @param reverse if true we will take first elements from second array
     * @return new mixed array from first and second array*/
    public static int[] mixArrays(int[] firstArray, int[] secondArray, int index, boolean reverse) {
        int size = firstArray.length;
        int[] result = new int[size];

        if (reverse) {
            System.arraycopy(secondArray, 0, result, 0, index);
            System.arraycopy(firstArray, index, result, index, size - index);
        } else {
            System.arraycopy(firstArray, 0, result, 0, index);
            System.arraycopy(secondArray, index, result, index, size - index);
        }

        return  result;
    }

    /**
     * Reduce number of bombs in array
     * @param array
     * @return array with reduced number of bombs
     */
    public static int[] reduceNumberOfBombs(int[] array) {
        int easyBombsCount = occurrences(array, GameMap.EASY_BOMB);
        int mediumBombsCount = occurrences(array, GameMap.MEDIUM_BOMB);
        int hardBombsCount = occurrences(array, GameMap.HARD_BOMB);

        while (bombOccurrences(array) > GameMap.MAX_NUMBER_OF_BOMBS - 1) {
            if (easyBombsCount > GameMap.maxNumberOfBombs(GameMap.EASY_BOMB)) {
                array = replaceFirstOccurrence(array, GameMap.EASY_BOMB, GameMap.EMPTY_FIELD);
                easyBombsCount = occurrences(array, GameMap.EASY_BOMB);
            } else if (mediumBombsCount > GameMap.maxNumberOfBombs(GameMap.MEDIUM_BOMB)) {
                array = replaceFirstOccurrence(array, GameMap.MEDIUM_BOMB, GameMap.EMPTY_FIELD);
                mediumBombsCount = occurrences(array, GameMap.MEDIUM_BOMB);
            } else if (hardBombsCount > GameMap.maxNumberOfBombs(GameMap.HARD_BOMB)) {
                array = replaceFirstOccurrence(array, GameMap.HARD_BOMB, GameMap.EMPTY_FIELD);
                hardBombsCount = occurrences(array, GameMap.HARD_BOMB);
            }
        }

        return array;
    }

    /**
     * Generate random index from range based on size
     * @param size size of collection
     * @param minPercent minimum percent of size for range
     * @param maxPercent maximum percent of size for range
     * @return random number from range
     */
    public static int randomIndexForSize(int size, double minPercent, double maxPercent) {
        int min = (int) (size * minPercent);
        int max = (int) ((size * maxPercent - min) + 1);
        Random random = new Random();

        return random.nextInt(max) + min;
    }

    /**
     * Random index in range
     * @param min
     * @param max
     * @return random number from range
     */
    public static int randomIndexFromRange(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    /**
     * Replace random element in array with another random element
     * @param array
     * @param objects objects to use to replace random element
     * @param minIndex minimum index for random index
     * @return array with random replaced element
     */
    public static int[] replaceRandomElement(int[] array, int[] objects, int minIndex) {
        int randomIndex = MapCalculator.randomIndexFromRange(minIndex, array.length - 1);
        int randomObjectIndex = MapCalculator.randomIndexFromRange(0, objects.length - 1);

        while(array[randomIndex] == objects[randomObjectIndex])
            randomObjectIndex = MapCalculator.randomIndexFromRange(0, objects.length - 1);

        array[randomIndex] = objects[randomObjectIndex];

        return array;
    }

    /**
     * Replace first occurrence of object
     * @param array
     * @param object
     * @param replaceWith
     * @return array with replaced object
     */
    public static int[] replaceFirstOccurrence(int[] array, int object, int replaceWith) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == object) {
                array[i] = replaceWith;
                break;
            }
        }
        return array;
    }

    /**
     * Calculate bombs count fitness
     * @param array
     * @return fitness for bomb counts
     */
    public static double bombCountFitness(int[] array) {
        int easyBombsCount = occurrences(array, GameMap.EASY_BOMB);
        int mediumBombsCount = occurrences(array, GameMap.MEDIUM_BOMB);
        int hardBombsCount = occurrences(array, GameMap.HARD_BOMB);

        return 0.2 * easyBombsCount + 0.4 * mediumBombsCount + 0.6 * hardBombsCount;
    }

    /**
     * Calculate number of occurrences of elements bigger than object in each column
     * @param matrix
     * @param object
     * @return number of occurrences for each column
     */
    public static int[] numberOfOccurrencesInColumns(int[][] matrix, int object) {
        int[] occurrencesInColumns = new int[matrix[0].length];

        for(int i = 0; i < occurrencesInColumns.length; i++) {
            occurrencesInColumns[i] = 0;

            for (int[] row : matrix) {
                if (row[i] > object) occurrencesInColumns[i]++;
            }
        }

        return occurrencesInColumns;
    }

    /**
     * Calculate  number of occurrences of elements bigger than object in each row
     * @param matrix
     * @param object
     * @return number of occurrences for each row
     */
    public static int[] numberOfOccurrencesInRows(int[][] matrix, int object) {
        int[] occurrencesInRows = new int[matrix.length];

        for(int i = 0; i < occurrencesInRows.length; i++) {
            occurrencesInRows[i] = 0;

            for (int x : matrix[i]) {
                if (x > object) occurrencesInRows[i]++;
            }
        }

        return occurrencesInRows;
    }

    /**
     * Calculate column fitness for matrix
     * @param matrix
     * @return overall fitness for column counts
     */
    public static double calculateColumnsFitness(int[][] matrix) {
        int[] counts = numberOfOccurrencesInColumns(matrix, GameMap.EMPTY_FIELD);
        double fitness = 0;

        for (int i = 0; i < counts.length; i++) {
            int prev = i - 1;
            int next = i + 1;

            if (prev < 0) {
                fitness += (0.5 * counts[i] + 0.6 * counts[next]) / 2;
            } else if (next > counts.length - 1) {
                fitness += (0.6 * counts[prev] + 0.5 * counts[i]) / 2;
            } else {
                fitness += (0.8 * counts[prev] + counts[i] + 0.8 * counts[next]) / 3;
            }
        }

        return fitness;
    }

    /**
     * Calculate rows fitness for matrix
     * @param matrix
     * @return overall fitness for row counts
     */
    public static double calculateRowsFitness(int[][] matrix) {
        int[] counts = numberOfOccurrencesInRows(matrix, GameMap.EMPTY_FIELD);
        double fitness = 0;

        for (int i = 0; i < counts.length; i++) {
            int prev = i - 1;
            int next = i + 1;

            if (prev < 0) {
                fitness += (0.4 * counts[i] + 0.55 * counts[next]) / 2;
            } else if (next > counts.length - 1) {
                fitness += (0.55 * counts[prev] + 0.4 * counts[i]) / 2;
            } else {
                fitness += (0.65 * counts[prev] + 0.85 * counts[i] + 0.65 * counts[next]) / 3;
            }
        }

        return fitness;
    }
}

