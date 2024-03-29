package topic;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SUDOKU {
    private static final int EMPTY = 0; // Empty cell marker
    private static int[][] grid; // The Sudoku grid
    private static long startTime; // Start time for measuring execution duration

    public static void main(String[] args) {
        // Generate a semi-random starting grid
        grid = generateRandomSudokuStart(5); // we can Adjust the number of pre-filled cells as needed
        // Print the initial Sudoku grid
        System.out.println("Initial Sudoku Grid:");
        printGrid(grid);
        System.out.println("-------------------------------------------");
        // Start solving timer
        startTime = System.nanoTime();
        

        // Solve the Sudoku
        if (solveSudoku(grid)) {
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // Duration in milliseconds
            System.out.println("Sudoku Solved Successfully:");
            printGrid(grid);
            System.out.println("Computational Time: " + duration + " milliseconds");
            int violations = calculateConstraintViolations(grid);
            System.out.println("Total Constraint Violations: "+violations);
        } else {
            System.out.println("No solution found for the Sudoku.");
        }
    }

    // Solve the Sudoku using backtracking and MRV heuristic
public static boolean solveSudoku(int[][] grid) {
    int[] cell = findUnassignedLocation(grid);
    if (cell == null) {
        return true; // Puzzle solved
    }
    int row = cell[0], col = cell[1];
    List<Integer> values = getPossibleValues(grid, row, col);
    for (Integer value : values) {
        if (isSafe(grid, row, col, value)) {
            grid[row][col] = value;
            System.out.println("Assigning value " + value + " to cell [" + row + ", " + col + "]");
            printGrid(grid); // Print the grid after each assignment
            System.out.println("-------------------------------------------");
            if (solveSudoku(grid)) {
                return true;
            }
            grid[row][col] = EMPTY; // Undo & try again
        }
    }
    return false; // Trigger backtracking
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



//Find the unassigned location with MRV heuristic
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

    // Checks whether it will be legal to assign num to the given row, col
    private static boolean isSafe(int[][] grid, int row, int col, int num) {
        return !usedInRow(grid, row, num) && !usedInCol(grid, col, num) && !usedInBox(grid, row - row % 3, col - col % 3, num);
    }

    // Check if num is not in the current row -C1-
    // Check if num is not in the current row -C1-
    private static boolean usedInRow(int[][] grid, int row, int num) {
        for (int col = 0; col < 9; col++) {
            if (grid[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    // Check if num is not in the current column -C2-
    // Check if num is not in the current column -C2-
    private static boolean usedInCol(int[][] grid, int col, int num) {
        for (int row = 0; row < 9; row++) {
            if (grid[row][col] == num) {
                return true;
            }
        }
        return false;
    }
 // Check if num is not in the current 3x3 box -C3-
 // Check if num is not in the current 3x3 box -C3-
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

    // Generate a list of possible values for a cell
    private static List<Integer> getPossibleValues(int[][] grid, int row, int col) {
        boolean[] possible = new boolean[9];
        for (int i = 0; i < 9; i++) {
            possible[i] = true;
        }

        // Eliminate numbers based on the current row, column, and 3x3 box
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

        // Compile the list of valid numbers
        List<Integer> validNumbers = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (possible[i]) {
                validNumbers.add(i + 1);
            }
        }
        return validNumbers;
    }

    // Generate a semi-random starting grid
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

    // Print the Sudoku grid
    private static void printGrid(int[][] grid) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                System.out.print(grid[row][col] + " ");
            }
            System.out.println();
        }
    }
}