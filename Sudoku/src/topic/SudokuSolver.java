package topic;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SudokuSolver {
    private static final int EMPTY = 0;
    private static int[][] grid;
    private static long startTime;

    public static void main(String[] args) {
        grid = generateRandomSudokuStart(30); // Adjust the number of pre-filled cells as needed
        System.out.println("Initial Sudoku Grid:");
        printGrid(grid);
        System.out.println("-------------------------------------------");
        startTime = System.nanoTime();

        // Solve Sudoku using simulated annealing
        solveSudoku(grid);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;
        if (isSolved(grid)) {
            System.out.println("Sudoku Solved Successfully:");
            printGrid(grid);
            System.out.println("Computational Time: " + duration + " milliseconds");
            int violations = calculateConstraintViolations(grid);
            System.out.println("Total Constraint Violations: " + violations);
        } else {
            System.out.println("No solution found for the Sudoku.");
        }
    }

    public static void solveSudoku(int[][] grid) {
        double temperature = 1.0;
        double coolingRate = 0.003;
        while (temperature > 0.1) {
            int[] cell = findUnassignedLocation(grid);
            if (cell == null) return; // Puzzle solved
            int row = cell[0], col = cell[1];
            List<Integer> values = getPossibleValues(grid, row, col);
            for (Integer value : values) {
                if (isSafe(grid, row, col, value)) {
                    int originalValue = grid[row][col];
                    grid[row][col] = value;
                    int newViolations = calculateConstraintViolations(grid);
                    int deltaViolations = newViolations - calculateConstraintViolations(grid);
                    if (deltaViolations <= 0 || Math.exp(-deltaViolations / temperature) > Math.random()) {
                        return; // Accept the move
                    } else {
                        grid[row][col] = originalValue; // Revert the move
                    }
                }
            }
            temperature *= (1 - coolingRate); // Cool down
        }
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

    private static boolean isSafe(int[][] grid, int row, int col, int num) {
        return !usedInRow(grid, row, num) && !usedInCol(grid, col, num) && !usedInBox(grid, row - row % 3, col - col % 3, num);
    }

    private static boolean usedInRow(int[][] grid, int row, int num) {
        for (int col = 0; col < 9; col++) {
            if (grid[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    private static boolean usedInCol(int[][] grid, int col, int num) {
        for (int row = 0; row < 9; row++) {
            if (grid[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    private static boolean usedInBox(int[][] grid, int boxStartRow, int boxStartCol, int num) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (grid[row + boxStartRow][col + boxStartCol] == num) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<Integer> getPossibleValues(int[][] grid, int row, int col) {
        boolean[] possible = new boolean[9];
        for (int i = 0; i < 9; i++) {
            possible[i] = true;
        }

        for (int i = 0; i < 9; i++) {
            if (grid[row][i] != EMPTY) {
                possible[grid[row][i] - 1] = false;
            }
            if (grid[i][col] != EMPTY) {
                possible[grid[i][col] - 1] = false;
            }
            int boxRow = row - row % 3 + i / 3;
            int boxCol = col - col % 3 + i % 3;
            if (grid[boxRow][boxCol] != EMPTY) {
                possible[grid[boxRow][boxCol] - 1] = false;
            }
        }

        List<Integer> validNumbers = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (possible[i]) {
                validNumbers.add(i + 1);
            }
        }
        return validNumbers;
    }

    private static int[][] generateRandomSudokuStart(int fillCells) {
        int[][] newGrid = new int[9][9];
        Random rand = new Random();
        while (fillCells > 0) {
            int row = rand.nextInt(9);
            int col = rand.nextInt(9);
            if (newGrid[row][col] == EMPTY) {
                List<Integer> nums = getPossibleValues(newGrid, row, col);
                if (!nums.isEmpty()) {
                    int num = nums.get(rand.nextInt(nums.size()));
                    newGrid[row][col] = num;
                    fillCells--;
                }
            }
        }
        return newGrid;
    }

    private static void printGrid(int[][] grid) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                System.out.print(grid[row][col] + " ");
            }
            System.out.println();
        }
    }
}
