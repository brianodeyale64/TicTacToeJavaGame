package tictactoe;

/**
 * Controls game flow and state transitions.
 * Decoupled from both UI and board logic.
 */
public class GameController {

    public enum GameMode { PVP, PVC }
    public enum GameState { PLAYING, WIN_X, WIN_O, DRAW }

    private final Board board;
    private AIPlayer ai;
    private GameMode mode;
    private char currentPlayer;
    private GameState state;

    // Score tracking
    private int scoreX;
    private int scoreO;
    private int scoreDraw;

    public GameController() {
        board = new Board();
        scoreX = scoreO = scoreDraw = 0;
        startGame(GameMode.PVC);
    }

    /** Starts or restarts a game in the given mode. */
    public void startGame(GameMode mode) {
        this.mode = mode;
        if (mode == GameMode.PVC) {
            ai = new AIPlayer(Board.PLAYER_O, Board.PLAYER_X);
        } else {
            ai = null;
        }
        board.reset();
        currentPlayer = Board.PLAYER_X;
        state = GameState.PLAYING;
    }

    /** Resets the board without changing mode or scores. */
    public void newRound() {
        board.reset();
        currentPlayer = Board.PLAYER_X;
        state = GameState.PLAYING;
    }

    /**
     * Attempts a move at (row, col) for the current player.
     *
     * @return true if the move was accepted
     */
    public boolean handleMove(int row, int col) {
        if (state != GameState.PLAYING) return false;
        if (!board.makeMove(row, col, currentPlayer)) return false;

        updateState();
        if (state == GameState.PLAYING) {
            switchPlayer();
            // If vs CPU and it's O's turn, do AI move
            if (mode == GameMode.PVC && currentPlayer == Board.PLAYER_O) {
                int[] move = ai.getBestMove(board);
                board.makeMove(move[0], move[1], Board.PLAYER_O);
                updateState();
                if (state == GameState.PLAYING) switchPlayer();
            }
        }
        return true;
    }

    private void updateState() {
        if (board.checkWin(Board.PLAYER_X)) {
            state = GameState.WIN_X;
            scoreX++;
        } else if (board.checkWin(Board.PLAYER_O)) {
            state = GameState.WIN_O;
            scoreO++;
        } else if (board.isFull()) {
            state = GameState.DRAW;
            scoreDraw++;
        }
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == Board.PLAYER_X) ? Board.PLAYER_O : Board.PLAYER_X;
    }

    public void resetScores() {
        scoreX = scoreO = scoreDraw = 0;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public Board getBoard()           { return board; }
    public GameState getState()       { return state; }
    public GameMode getMode()         { return mode; }
    public char getCurrentPlayer()    { return currentPlayer; }
    public int getScoreX()            { return scoreX; }
    public int getScoreO()            { return scoreO; }
    public int getScoreDraw()         { return scoreDraw; }
}
