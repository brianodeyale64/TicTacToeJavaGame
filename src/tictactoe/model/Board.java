package tictactoe.model;

/**
 * Represents the Tic Tac Toe board state and core game logic.
 */
public class Board {

    public static final int SIZE = 3;
    public static final char EMPTY = '-';
    public static final char PLAYER_X = 'X';
    public static final char PLAYER_O = 'O';

    private char[][] cells;
    private int moveCount;

    public Board() {
        cells = new char[SIZE][SIZE];
        reset();
    }

    /** Deep-copy constructor for AI lookahead. */
    public Board(Board other) {
        this.moveCount = other.moveCount;
        this.cells = new char[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                this.cells[r][c] = other.cells[r][c];
    }

    public void reset() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                cells[r][c] = EMPTY;
        moveCount = 0;
    }

    public char getCell(int row, int col) { return cells[row][col]; }

    public boolean isCellEmpty(int row, int col) { return cells[row][col] == EMPTY; }

    public boolean placeMarker(int row, int col, char marker) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return false;
        if (!isCellEmpty(row, col)) return false;
        cells[row][col] = marker;
        moveCount++;
        return true;
    }

    public char checkWinner() {
        // Rows & cols
        for (int i = 0; i < SIZE; i++) {
            if (checkLine(cells[i][0], cells[i][1], cells[i][2])) return cells[i][0];
            if (checkLine(cells[0][i], cells[1][i], cells[2][i])) return cells[0][i];
        }
        // Diagonals
        if (checkLine(cells[0][0], cells[1][1], cells[2][2])) return cells[0][0];
        if (checkLine(cells[0][2], cells[1][1], cells[2][0])) return cells[0][2];
        return EMPTY;
    }

    private boolean checkLine(char a, char b, char c) {
        return a != EMPTY && a == b && b == c;
    }

    public boolean isFull() { return moveCount >= SIZE * SIZE; }

    public boolean isGameOver() { return checkWinner() != EMPTY || isFull(); }

    public int getMoveCount() { return moveCount; }

    /** Returns the winning line as [r1,c1, r2,c2, r3,c3], or null. */
    public int[] getWinningLine() {
        for (int i = 0; i < SIZE; i++) {
            if (checkLine(cells[i][0], cells[i][1], cells[i][2]))
                return new int[]{i,0, i,1, i,2};
            if (checkLine(cells[0][i], cells[1][i], cells[2][i]))
                return new int[]{0,i, 1,i, 2,i};
        }
        if (checkLine(cells[0][0], cells[1][1], cells[2][2]))
            return new int[]{0,0, 1,1, 2,2};
        if (checkLine(cells[0][2], cells[1][1], cells[2][0]))
            return new int[]{0,2, 1,1, 2,0};
        return null;
    }
}
