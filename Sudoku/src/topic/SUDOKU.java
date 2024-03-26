package topic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SUDOKU {
    private static final int EMPTY = 0; // Constant representing an empty cell
    private static int[][] grid; // Sudoku grid
    private static long startTime; // Variable to store the start time

    public static void main(String[] args) {
        // Define the Sudoku grid (0 represents empty cells)
        grid = new int[][]{
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        // Start the timer
        startTime = System.nanoTime();

        // Solve the Sudoku
        int[][] solution = solveSudoku(grid);

        // End time
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // Convert to milliseconds

        // Print the solution and computational time
        if (solution != null) {
            System.out.println("Sudoku Solved Successfully:");
            printGrid(solution);
            int violations = calculateConstraintViolations(solution);
            System.out.println("Total Constraint Violations: " + violations);
            System.out.println("Computational Time: " + duration + " milliseconds");
        } else {
            System.out.println("No solution found for the Sudoku.");
        }
    }

    // Method to solve the Sudoku using backtracking with MRV heuristic
public static int[][] solveSudoku(int[][] grid) {
    if (isFilled(grid)) {
        return grid;  // Puzzle solved
    }

    // Find the next empty cell with the fewest remaining values (MRV heuristic)
    int[] cell = getNextEmptyCellWithMRV(grid);

    // Get possible values for the empty cell
    List<Integer> values = possibleValues(grid, cell);

    // Shuffle the values to break ties randomly
    Collections.shuffle(values);

    // Iterate through possible values for the empty cell
    for (int value : values) {
        // Assign the value to the cell
        if (assignValue(grid, cell, value)) { // Assign and print inside assignValue method
            // Recursively solve the Sudoku
            int[][] solution = solveSudoku(grid);
            if (solution != null) {
                return solution;
            }
        }

        // Undo the assignment
        grid[cell[0]][cell[1]] = EMPTY;
    }

    // If no solution found
    return null;
}

// Method to assign a value to a cell and print the grid
private static boolean assignValue(int[][] grid, int[] cell, int value) {
    grid[cell[0]][cell[1]] = value;
    if (checkConstraints(grid, cell) && isValidAssignment(grid, cell)) {
        System.out.println("Assigning value " + value + " to cell [" + cell[0] + ", " + cell[1] + "]");
        printGrid(grid); // Print the grid after each assignment
        System.out.println(); // Add an empty line for better readability
        return true;
    } else {
        grid[cell[0]][cell[1]] = EMPTY; // Undo assignment if not valid
        return false;
    }
}


 // Method to check if an assignment is valid
    private static boolean isValidAssignment(int[][] grid, int[] cell) {
        int row = cell[0];
        int col = cell[1];
        int value = grid[row][col];

        // Check row constraints
        for (int j = 0; j < 9; j++) {
            if (j != col && grid[row][j] == value) {
                return false;
            }
        }

        // Check column constraints
        for (int i = 0; i < 9; i++) {
            if (i != row && grid[i][col] == value) {
                return false;
            }
        }

        // Check 3x3 box constraints
        int boxStartRow = row - row % 3;
        int boxStartCol = col - col % 3;
        for (int i = boxStartRow; i < boxStartRow + 3; i++) {
            for (int j = boxStartCol; j < boxStartCol + 3; j++) {
                if (i != row && j != col && grid[i][j] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    // Method to check if the Sudoku grid is completely filled
    private static boolean isFilled(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    // Method to find the next empty cell with the fewest remaining values (MRV heuristic)
    private static int[] getNextEmptyCellWithMRV(int[][] grid) {
        int minRemainingValues = Integer.MAX_VALUE;
        int[] selectedCell = null;

        // Iterate through each cell in the grid
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == EMPTY) {
                    // Count remaining values for the empty cell
                    int remainingValues = countRemainingValues(grid, i, j);
                    // Update the cell with the fewest remaining values
                    if (remainingValues < minRemainingValues) {
                        minRemainingValues = remainingValues;
                        selectedCell = new int[]{i, j};
                    }
                }
            }
        }
        return selectedCell;
    }

    // Method to count the number of remaining values for an empty cell
    private static int countRemainingValues(int[][] grid, int row, int col) {
        int count = 0;
        for (int value = 1; value <= 9; value++) {
            if (isValidAssignment(grid, new int[]{row, col}, value)) {
                count++;
            }
        }
        return count;
    }

    // Method to get possible values for an empty cell
    private static List<Integer> possibleValues(int[][] grid, int[] cell) {
        List<Integer> values = new ArrayList<>();
        for (int value = 1; value <= 9; value++) {
            if (isValidAssignment(grid, cell, value)) {
                values.add(value);
            }
        }
        return values;
    }

    // Method to check constraints
    private static boolean checkConstraints(int[][] grid, int[] cell) {
        int row = cell[0];
        int col = cell[1];
        int value = grid[row][col];

        // Check row constraints
        for (int j = 0; j < 9; j++) {
            if (j != col && grid[row][j] == value) {
                return false;
            }
        }

        // Check column constraints
        for (int i = 0; i < 9; i++) {
            if (i != row && grid[i][col] == value) {
                return false;
            }
        }

        // Check 3x3 box constraints
        int boxStartRow = row - row % 3;
        int boxStartCol = col - col % 3;
        for (int i = boxStartRow; i < boxStartRow + 3; i++) {
            for (int j = boxStartCol; j < boxStartCol + 3; j++) {
                if (i != row && j != col && grid[i][j] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    // Method to check if an assignment is valid
    private static boolean isValidAssignment(int[][] grid, int[] cell, int value) {
        int row = cell[0];
        int col = cell[1];

        // Check constraints
        grid[row][col] = value;
        boolean isValid = checkConstraints(grid, cell);
        grid[row][col] = EMPTY; // Undo assignment
        return isValid;
    }

    // Method to calculate the number of constraint violations
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

    // Method to count constraint violations for a cell
    private static int countCellViolations(int[][] grid, int row, int col) {
        int value = grid[row][col];
        int violations = 0;

        // Check row constraints
        for (int j = 0; j < 9; j++) {
            if (j != col && grid[row][j] == value) {
                violations++;
            }
        }

        // Check column constraints
        for (int i = 0; i < 9; i++) {
            if (i != row && grid[i][col] == value) {
                violations++;
            }
        }

        // Check 3x3 box constraints
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

    // Method to print the Sudoku grid
    private static void printGrid(int[][] grid) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
}