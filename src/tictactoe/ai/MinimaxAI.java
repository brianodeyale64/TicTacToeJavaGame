package tictactoe.ai;

import tictactoe.model.Board;

/**
 * Unbeatable AI using the Minimax algorithm with Alpha-Beta pruning.
 * The AI always plays as 'O'.
 */
public class MinimaxAI {

    private static final int WIN_SCORE  =  10;
    private static final int LOSE_SCORE = -10;
    private static final int DRAW_SCORE =   0;

    /** Returns the best [row, col] move for the AI (O). */
    public int[] getBestMove(Board board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (board.isCellEmpty(r, c)) {
                    Board copy = new Board(board);
                    copy.placeMarker(r, c, Board.PLAYER_O);
                    int score = minimax(copy, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{r, c};
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(Board board, int depth, boolean isMaximizing, int alpha, int beta) {
        char winner = board.checkWinner();

        if (winner == Board.PLAYER_O) return WIN_SCORE  - depth;
        if (winner == Board.PLAYER_X) return LOSE_SCORE + depth;
        if (board.isFull())           return DRAW_SCORE;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            outer:
            for (int r = 0; r < Board.SIZE; r++) {
                for (int c = 0; c < Board.SIZE; c++) {
                    if (board.isCellEmpty(r, c)) {
                        Board copy = new Board(board);
                        copy.placeMarker(r, c, Board.PLAYER_O);
                        best = Math.max(best, minimax(copy, depth + 1, false, alpha, beta));
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
                    if (board.isCellEmpty(r, c)) {
                        Board copy = new Board(board);
                        copy.placeMarker(r, c, Board.PLAYER_X);
                        best = Math.min(best, minimax(copy, depth + 1, true, alpha, beta));
                        beta = Math.min(beta, best);
                        if (beta <= alpha) break outer;
                    }
                }
            }
            return best;
        }
    }
}
