package tictactoe.view;

import tictactoe.model.GameState;
import tictactoe.model.ScoreTracker;

/**
 * Contract between the controller and the visual layer.
 * Allows alternative view implementations (e.g., console, web).
 */
public interface GameView {
    void onGameReset(char firstPlayer);
    void onCellPlaced(int row, int col, char marker);
    void onTurnChanged(char player);
    void onGameOver(GameState state, int[] winningLine);
    void onScoreUpdated(ScoreTracker score);
    void onAiThinking(boolean thinking);
}
