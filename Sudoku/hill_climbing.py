import numpy as np
import sys
import time

class Sudoku():

    def __init__(self):
        self.reset()

    def reset(self):
        # Initialize the Sudoku board with numbers from 1 to 9 in random order
        self.board = (np.indices((9,9)) + 1)[1]
        for i in range(len(self.board)):
            self.board[i] = np.random.permutation(self.board[i])
        # Define fixed values for the Sudoku puzzle
        self.fixedValues = []
        for row in range(9):
            for col in range(9):
                if np.random.random() < 0.5:  # Probability of fixing a number
                    value = np.random.randint(1, 10)
                    self.fixedValues.append((value, row, col))
        self.setup()

    def printBoard(self, board=None):
        # Print the Sudoku board
        if board is None:
            board = self.board
        
        for i in range(len(board)):
            if(i % 3 == 0 and i != 0):
                print("------+------+------")
            for j in range(len(board[i])):
                if(j % 3 == 0 and j != 0):
                    sys.stdout.write("|")
                sys.stdout.write(str(board[i][j]) + " ")
            print("")

    def swapToPlace(self, val, line, col):
        # Swap a value to a specific position on the board
        valIndex = np.where(self.board[line]==val)[0][0]
        self.swap(self.board[line], valIndex, col)

    def setup(self):
        # Setup the initial configuration of the Sudoku board
        for (val, row, col) in self.fixedValues:
            self.swapToPlace(val, row, col)

    def fitness(self, board=None):
        # Calculate the fitness score of the Sudoku board (number of unique values)
        if board is None:
            board = self.board
        score = 0
        rows, cols = board.shape
        for row in board:
            score += len(np.unique(row))
        for col in board.T:
            score += len(np.unique(col))
        for i in range(0, 3):
            for j in range(0, 3):
                sub = board[3*i:3*i+3, 3*j:3*j+3]
                score += len(np.unique(sub))
        return score

    def swap(self, arr, pos1, pos2):
        # Swap two elements in an array
        arr[pos1], arr[pos2] = arr[pos2], arr[pos1]

    def isFixed(self, row, col):
        # Check if a position on the board corresponds to a fixed value
        for t in self.fixedValues:
            if(row == t[1] and col == t[2]):
                return True
        return False

    def bestNeighbor(self):
        # Find the best neighboring configuration of the Sudoku board
        tempBoard = self.board.copy()
        # best = (row, (col1, col2), val)
        # col1 and col2 will be swapped with the swap.
        best = (0, (0,0), -1)
        for i in range(len(tempBoard)):
            for j in range(len(tempBoard[i])):
                for k in range(i,len(tempBoard)):
                    if(self.isFixed(i,j) or self.isFixed(i,k)):
                        continue
                    self.swap(tempBoard[i], j, k)
                    contestant = (i, (j,k), self.fitness(tempBoard))
                    if(contestant[2] > best[2]):
                        best = contestant
                    # Undo the swap to reuse the board
                    self.swap(tempBoard[i], j, k)
        return best

    def climbHill(self):
        # Perform hill climbing algorithm to solve the Sudoku puzzle
        scores = []
        maxScore = self.fitness()
        print("Initial board:")
        self.printBoard(self.board)
        print("\nInitial score:", maxScore)
        start_time = time.time()
        while True:
            scores.append(maxScore)
            (row, (col1, col2), nextScore) = self.bestNeighbor()
            if(nextScore <= maxScore):
                end_time = time.time()
                elapsed_time = (end_time - start_time) * 1000  # in milliseconds
                print(f"\nTime taken to find solution: {elapsed_time:.2f} milliseconds")
                return scores
            self.swap(self.board[row], col1, col2)
            maxScore = nextScore
            print("\nUpdated board:")
            self.printBoard(self.board)
            print("Score:", maxScore)

sud = Sudoku()
print("Hill Climbing")
print("The higher the score, the better. (Max = 243)")
print("The score reflects the number of unique values per row, column, and quadrant.")
trials = []
maxScore = -1
bestBoard = []
for i in range(10):
    sud.reset()
    finalScore = sud.climbHill()
    maxFinalScore = max(finalScore)
    if(maxScore < maxFinalScore):
        maxScore = maxFinalScore
        bestBoard = sud.board.copy()
    print(str(i) + ") " + str(finalScore[-1]) + "/243")
    if(finalScore[-1] == 243):
        print("CORRECT SOLUTION!")
        sud.printBoard()
        break
    trials.append(finalScore)
    # print(finalScore)
print("Best score: %i" % maxScore)
sud.printBoard(bestBoard)