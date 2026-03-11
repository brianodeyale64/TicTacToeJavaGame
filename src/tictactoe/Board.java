package tictactoe;

/**
 * Represents the Tic-Tac-Toe game board.
 * Handles game state, win detection, and board logic.
 */
public class Board {

    public static final int SIZE = 3;
    public static final char EMPTY = '-';
    public static final char PLAYER_X = 'X';
    public static final char PLAYER_O = 'O';

    private char[][] grid;
    private int moveCount;

    public Board() {
        grid = new char[SIZE][SIZE];
        reset();
    }

    /** Resets the board to its initial empty state. */
    public void reset() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                grid[r][c] = EMPTY;
        moveCount = 0;
    }

    /**
     * Places a mark at the given position if it is empty.
     *
     * @param row    row index (0–2)
     * @param col    column index (0–2)
     * @param player the player's mark (X or O)
     * @return true if the move was successful, false otherwise
     */
    public boolean makeMove(int row, int col, char player) {
        if (!isValidMove(row, col)) return false;
        grid[row][col] = player;
        moveCount++;
        return true;
    }

    /** Returns whether the given cell is empty and in range. */
    public boolean isValidMove(int row, int col) {
        return row >= 0 && row < SIZE
            && col >= 0 && col < SIZE
            && grid[row][col] == EMPTY;
    }

    /** Returns the mark at the given cell. */
    public char getCell(int row, int col) {
        return grid[row][col];
    }

    /** Returns true if the specified player has won. */
    public boolean checkWin(char player) {
        // Check rows and columns
        for (int i = 0; i < SIZE; i++) {
            if (grid[i][0] == player && grid[i][1] == player && grid[i][2] == player) return true;
            if (grid[0][i] == player && grid[1][i] == player && grid[2][i] == player) return true;
        }
        // Check diagonals
        if (grid[0][0] == player && grid[1][1] == player && grid[2][2] == player) return true;
        if (grid[0][2] == player && grid[1][1] == player && grid[2][0] == player) return true;
        return false;
    }

    /** Returns true if the board is full (draw). */
    public boolean isFull() {
        return moveCount >= SIZE * SIZE;
    }

    /** Returns the winning line as int[2][2] {start, end} or null if none. */
    public int[][] getWinLine(char player) {
        for (int i = 0; i < SIZE; i++) {
            if (grid[i][0] == player && grid[i][1] == player && grid[i][2] == player)
                return new int[][]{{i, 0}, {i, 2}};
            if (grid[0][i] == player && grid[1][i] == player && grid[2][i] == player)
                return new int[][]{{0, i}, {2, i}};
        }
        if (grid[0][0] == player && grid[1][1] == player && grid[2][2] == player)
            return new int[][]{{0, 0}, {2, 2}};
        if (grid[0][2] == player && grid[1][1] == player && grid[2][0] == player)
            return new int[][]{{0, 2}, {2, 0}};
        return null;
    }

    /** Returns a deep copy of the current grid for AI use. */
    public char[][] copyGrid() {
        char[][] copy = new char[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            copy[r] = grid[r].clone();
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                sb.append(grid[r][c]);
                if (c < SIZE - 1) sb.append('|');
            }
            if (r < SIZE - 1) sb.append("\n-+-+-\n");
        }
        return sb.toString();
    }
}
