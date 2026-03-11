package tictactoe.controller;

import tictactoe.ai.MinimaxAI;
import tictactoe.model.*;
import tictactoe.view.GameView;

import javax.swing.SwingWorker;

/**
 * Controller that connects the Board model, MinimaxAI, and GameView.
 * Handles turn logic, AI scheduling, and score tracking.
 */
public class GameController {

    private final Board board;
    private final ScoreTracker scoreTracker;
    private final MinimaxAI ai;
    private GameView view;

    private char currentPlayer;
    private GameMode gameMode;
    private GameState gameState;
    private boolean aiThinking;

    public GameController() {
        board        = new Board();
        scoreTracker = new ScoreTracker();
        ai           = new MinimaxAI();
        gameMode     = GameMode.PLAYER_VS_AI;
        newGame();
    }

    public void setView(GameView view) {
        this.view = view;
    }

    // ── Public actions ────────────────────────────────────────────────

    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
        newGame();
    }

    public void newGame() {
        board.reset();
        currentPlayer = Board.PLAYER_X;
        gameState     = GameState.IN_PROGRESS;
        aiThinking    = false;
        if (view != null) view.onGameReset(currentPlayer);
    }

    public void resetScores() {
        scoreTracker.reset();
        newGame();
    }

    /** Called when a human clicks cell (row, col). */
    public void handleCellClick(int row, int col) {
        if (gameState != GameState.IN_PROGRESS) return;
        if (aiThinking) return;
        if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Board.PLAYER_O) return;

        makeMove(row, col);
    }

    // ── Private helpers ───────────────────────────────────────────────

    private void makeMove(int row, int col) {
        if (!board.placeMarker(row, col, currentPlayer)) return;

        view.onCellPlaced(row, col, currentPlayer);

        char winner = board.checkWinner();
        if (winner != Board.EMPTY) {
            gameState = (winner == Board.PLAYER_X) ? GameState.X_WINS : GameState.O_WINS;
            scoreTracker.recordResult(gameState);
            view.onGameOver(gameState, board.getWinningLine());
            view.onScoreUpdated(scoreTracker);
            return;
        }
        if (board.isFull()) {
            gameState = GameState.DRAW;
            scoreTracker.recordResult(gameState);
            view.onGameOver(gameState, null);
            view.onScoreUpdated(scoreTracker);
            return;
        }

        // Switch turn
        currentPlayer = (currentPlayer == Board.PLAYER_X) ? Board.PLAYER_O : Board.PLAYER_X;
        view.onTurnChanged(currentPlayer);

        if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Board.PLAYER_O) {
            scheduleAiMove();
        }
    }

    /** Runs the AI on a background thread to keep the UI responsive. */
    private void scheduleAiMove() {
        aiThinking = true;
        view.onAiThinking(true);

        SwingWorker<int[], Void> worker = new SwingWorker<>() {
            @Override
            protected int[] doInBackground() throws Exception {
                Thread.sleep(350); // brief pause so AI doesn't feel instant
                return ai.getBestMove(board);
            }

            @Override
            protected void done() {
                try {
                    int[] move = get();
                    aiThinking = false;
                    view.onAiThinking(false);
                    makeMove(move[0], move[1]);
                } catch (Exception e) {
                    aiThinking = false;
                    view.onAiThinking(false);
                }
            }
        };
        worker.execute();
    }

    // ── Getters ───────────────────────────────────────────────────────

    public GameMode getGameMode()         { return gameMode; }
    public char     getCurrentPlayer()    { return currentPlayer; }
    public ScoreTracker getScoreTracker() { return scoreTracker; }
}
