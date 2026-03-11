package tictactoe;

/**
 * AI player using the Minimax algorithm with alpha-beta pruning.
 * Plays optimally — it will never lose, only draw or win.
 */
public class AIPlayer {

    private final char aiMark;
    private final char humanMark;

    public AIPlayer(char aiMark, char humanMark) {
        this.aiMark = aiMark;
        this.humanMark = humanMark;
    }

    /**
     * Calculates the best move for the AI.
     *
     * @param board the current game board
     * @return int[] {row, col} of the best move
     */
    public int[] getBestMove(Board board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (board.isValidMove(r, c)) {
                    board.makeMove(r, c, aiMark);
                    int score = minimax(board, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    board.makeMove(r, c, Board.EMPTY); // undo (direct access via package)
                    // We undo by resetting manually — see UndoableBoard below
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{r, c};
                    }
                }
            }
        }
        return bestMove;
    }

    /**
     * Minimax with alpha-beta pruning.
     *
     * @param board      the board state
     * @param depth      recursion depth
     * @param isMaximizing true if it's the AI's turn
     * @param alpha      best score for maximizer so far
     * @param beta       best score for minimizer so far
     * @return the heuristic score of the board
     */
    private int minimax(Board board, int depth, boolean isMaximizing, int alpha, int beta) {
        if (board.checkWin(aiMark))    return 10 - depth;
        if (board.checkWin(humanMark)) return depth - 10;
        if (board.isFull())            return 0;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            outer:
            for (int r = 0; r < Board.SIZE; r++) {
                for (int c = 0; c < Board.SIZE; c++) {
                    if (board.isValidMove(r, c)) {
                        board.makeMove(r, c, aiMark);
                        best = Math.max(best, minimax(board, depth + 1, false, alpha, beta));
                        undoMove(board, r, c);
                        alpha = Math.max(alpha, best);
                        if (beta <= alpha) break outer;
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            outer:
            for (int r = 0; r < Board.SIZE; r++) {
                for (int c = 0; c < Board.SIZE; c++) {
                    if (board.isValidMove(r, c)) {
                        board.makeMove(r, c, humanMark);
                        best = Math.min(best, minimax(board, depth + 1, true, alpha, beta));
                        undoMove(board, r, c);
                        beta = Math.min(beta, best);
                        if (beta <= alpha) break outer;
                    }
                }
            }
            return best;
        }
    }

    /**
     * Undoes a move by placing EMPTY — works because Board.makeMove
     * accepts any char, and EMPTY is a valid value within the package.
     */
    private void undoMove(Board board, int row, int col) {
        board.makeMove(row, col, Board.EMPTY);
    }
}
