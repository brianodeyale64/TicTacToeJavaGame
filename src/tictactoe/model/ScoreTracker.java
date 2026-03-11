package tictactoe.model;

/**
 * Tracks wins, losses, and draws across multiple rounds.
 */
public class ScoreTracker {

    private int xWins;
    private int oWins;
    private int draws;

    public void recordResult(GameState state) {
        switch (state) {
            case X_WINS -> xWins++;
            case O_WINS -> oWins++;
            case DRAW   -> draws++;
            default -> {}
        }
    }

    public void reset() {
        xWins = 0;
        oWins = 0;
        draws = 0;
    }

    public int getXWins() { return xWins; }
    public int getOWins() { return oWins; }
    public int getDraws() { return draws; }
}
