package topic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SudokuSolver {
    private static final int EMPTY = 0;
    private static int[][] grid;
    private static long startTime;
    private static int maxIterations = 100;
    private static double initialTemperature = 1.0;
    private static double coolingRate = 0.0005;
    private static int resetThreshold = 1000;

    public static void main(String[] args) {
        boolean solutionFound = false;
        int resetCount = 0;
        while (!solutionFound && resetCount < resetThreshold) {
            grid = generateRandomSudokuStart(30); // Adjust the number of pre-filled cells as needed
            System.out.println("Initial Sudoku Grid:");
            printGrid(grid);
            System.out.println("-------------------------------------------");
            startTime = System.nanoTime();

            // Solve Sudoku using simulated annealing
            if (solveSudoku(grid)) {
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1_000_000;
                System.out.println("Sudoku Solved Successfully:");
                printGrid(grid);
                System.out.println("Computational Time: " + duration + " milliseconds");
                int violations = calculateConstraintViolations(grid);
                System.out.println("Total Constraint Violations: " + violations);
                solutionFound = true;
            } else {
                System.out.println("Termination condition: No solution found for the Sudoku.");
                resetCount++;
            }
        }

        if (!solutionFound) {
            System.out.println("Unable to find a solution after " + resetCount + " resets.");
        }
    }

    public static boolean solveSudoku(int[][] grid) {
        double temperature = initialTemperature;
        int iterations = 0; // Track iterations

        while (temperature > 0.1 && iterations < maxIterations) {
            int[] cell = findUnassignedLocation(grid);
            if (cell == null) return true; // Puzzle solved
            int row = cell[0], col = cell[1];
            List<Integer> values = getPossibleValues(grid, row, col);
            boolean moved = false; // Track if any move is made in this iteration

            for (Integer value : values) {
                if (isSafe(grid, row, col, value)) {
                    int originalValue = grid[row][col];
                    grid[row][col] = value;
                    int newViolations = calculateConstraintViolations(grid);
                    int deltaViolations = newViolations - calculateConstraintViolations(grid);

                    if (deltaViolations <= 0 || Math.exp(-deltaViolations / temperature) > Math.random()) {
                        System.out.println("Move: Assigned value " + value + " to cell (" + row + "," + col + ")");
                        printGrid(grid);
                        moved = true; // Move made
                        break; // Break after the first successful move
                    }

                    grid[row][col] = originalValue; // Revert the move
                }
            }

            // Decrease temperature after every cooling
            temperature *= (1 - coolingRate);
            System.out.println("Temperature decreased to " + temperature);

            iterations++; // Increase iteration count
        }

        // Stop if maximum iterations reached
        if (iterations >= maxIterations) {
            System.out.println("Termination condition: Maximum iterations reached.");
            return false;
        }

        return false; // Failed to solve the Sudoku
    }

    private static boolean isSolved(int[][] grid) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int[] findUnassignedLocation(int[][] grid) {
        int minCandidates = Integer.MAX_VALUE;
        int[] minCell = null;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col] == EMPTY) {
                    List<Integer> possibleValues = getPossibleValues(grid, row, col);
                    int numCandidates = possibleValues.size();
                    if (numCandidates < minCandidates) {
                        minCandidates = numCandidates;
                        minCell = new int[]{row, col};
                    }
                }
            }
        }

        return minCell; // Return the cell with the fewest remaining candidates
    }

    private static int calculateConstraintViolations(int[][] grid) {
        int violations = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] != EMPTY) {
                    violations += countCellViolations(grid, i, j);
                }
            }
        }
        return violations;
    }

    private static int countCellViolations(int[][] grid, int row, int col) {
        int value = grid[row][col];
        int violations = 0;
        for (int j = 0; j < 9; j++) {
            if (j != col && grid[row][j] == value) {
                violations++;
            }
        }

        for (int i = 0; i < 9; i++) {
            if (i != row && grid[i][col] == value) {
                violations++;
            }
        }

        int boxStartRow = row - row % 3;
        int boxStartCol = col - col % 3;
        for (int i = boxStartRow; i < boxStartRow + 3; i++) {
            for (int j = boxStartCol; j < boxStartCol + 3; j++) {
                if (i != row && j != col && grid[i][j] == value) {
                    violations++;
                }
            }
        }

        return violations;
    }

    private static List<Integer> getPossibleValues(int[][] grid, int row, int col) {
        List<Integer> possibleValues = new ArrayList<>();
        for (int value = 1; value <= 9; value++) {
            if (isSafe(grid, row, col, value)) {
                possibleValues.add(value);
            }
        }
        return possibleValues;
    }

    private static boolean isSafe(int[][] grid, int row, int col, int value) {
        for (int i = 0; i < 9; i++) {
            if (grid[row][i] == value || grid[i][col] == value) {
                return false;
            }
        }

        int boxStartRow = row - row % 3;
        int boxStartCol = col - col % 3;
        for (int i = boxStartRow; i < boxStartRow + 3; i++) {
            for (int j = boxStartCol; j < boxStartCol + 3; j++) {
                if (grid[i][j] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    private static void printGrid(int[][] grid) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static int[][] generateRandomSudokuStart(int numFilledCells) {
        int[][] grid = new int[9][9];
        Random random = new Random();

        while (numFilledCells > 0) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);
            int value = random.nextInt(9) + 1;
            if (grid[row][col] == 0 && isSafe(grid, row, col, value)) {
                grid[row][col] = value;
                numFilledCells--;
            }
        }

        return grid;
    }
}
