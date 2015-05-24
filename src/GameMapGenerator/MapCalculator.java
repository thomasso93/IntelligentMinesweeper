package GameMapGenerator;

import java.util.Random;

public class MapCalculator {
    /**
     *  Transform matrix to array
     *  @param matrix matrix to transform
     *  @param rows number of rows in matrix
     *  @param columns number of columns in matrix
     *  @return array with all elements from matrix
     */
    public static int[] matrixToArray(int[][] matrix, int rows, int columns) {
        int[] result = new int[rows * columns];
        int i = 0;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                result[i++] = matrix[row][column];
            }
        }

        return result;
    }

    /**
     * Transform array to matrix
     * @param array array to transform
     * @param columns number of columns in new matrix
     * @return matrix with passed number of columns
     */
    public static int[][] arrayToMatrix(int[] array, int columns) {
        int rows = (array.length + columns - 1) / columns;
        int[][] matrix = new int[rows][columns];

        for (int i = 0; i < array.length; i++) {
            matrix[i / columns][i % columns] = array[i];
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
}

