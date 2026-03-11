package tictactoe.view;

import tictactoe.controller.GameController;
import tictactoe.model.GameMode;
import tictactoe.model.GameState;
import tictactoe.model.ScoreTracker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Main Swing window for Tic Tac Toe.
 * Dark, minimal aesthetic with smooth cell animations.
 */
public class MainWindow extends JFrame implements GameView {

    // ── Palette ──────────────────────────────────────────────────────
    private static final Color BG          = new Color(0x0F0F13);
    private static final Color SURFACE     = new Color(0x1A1A24);
    private static final Color BORDER_CLR  = new Color(0x2A2A3A);
    private static final Color X_COLOR     = new Color(0xFF6B6B);
    private static final Color O_COLOR     = new Color(0x4ECDC4);
    private static final Color ACCENT      = new Color(0xFFE66D);
    private static final Color TEXT_DIM    = new Color(0x6B6B8A);
    private static final Color TEXT_LIGHT  = new Color(0xE8E8F0);
    private static final Color WIN_FLASH   = new Color(0xFFE66D, true);

    // ── UI components ─────────────────────────────────────────────────
    private CellButton[][] cells;
    private JLabel statusLabel;
    private JLabel xScoreLabel, oScoreLabel, drawLabel;
    private JButton newGameBtn, resetScoresBtn;
    private JToggleButton pvpToggle, pvaiToggle;
    private JLabel aiIndicator;

    private final GameController controller;

    // Winning line overlay
    private int[] winningLine = null;

    public MainWindow(GameController controller) {
        this.controller = controller;
        controller.setView(this);
        buildUI();
        setVisible(true);
    }

    // ── UI construction ───────────────────────────────────────────────

    private void buildUI() {
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBoard(),   BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        updateStatus('X', false);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(BG);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(28, 32, 12, 32));

        // Title
        JLabel title = new JLabel("TIC TAC TOE");
        title.setFont(loadFont(22f, Font.BOLD));
        title.setForeground(TEXT_LIGHT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(title, BorderLayout.CENTER);

        // Mode toggle
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        modePanel.setBackground(BG);
        modePanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        pvpToggle  = createToggleBtn("2 Players");
        pvaiToggle = createToggleBtn("vs AI");
        pvaiToggle.setSelected(true);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(pvpToggle);
        modeGroup.add(pvaiToggle);

        pvpToggle.addActionListener(e -> controller.setGameMode(GameMode.PLAYER_VS_PLAYER));
        pvaiToggle.addActionListener(e -> controller.setGameMode(GameMode.PLAYER_VS_AI));

        modePanel.add(pvpToggle);
        modePanel.add(pvaiToggle);
        header.add(modePanel, BorderLayout.SOUTH);

        return header;
    }

    private JPanel buildBoard() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(12, 32, 12, 32));

        // Score row
        JPanel scoreRow = new JPanel(new GridLayout(1, 3, 8, 0));
        scoreRow.setBackground(BG);
        scoreRow.setBorder(new EmptyBorder(0, 0, 16, 0));

        xScoreLabel = buildScoreCard("X", "0", X_COLOR);
        drawLabel   = buildScoreCard("Draw", "0", TEXT_DIM);
        oScoreLabel = buildScoreCard("O", "0", O_COLOR);

        scoreRow.add(xScoreLabel.getParent());
        scoreRow.add(drawLabel.getParent());
        scoreRow.add(oScoreLabel.getParent());
        wrapper.add(scoreRow, BorderLayout.NORTH);

        // 3×3 grid
        JPanel grid = new JPanel(new GridLayout(3, 3, 6, 6));
        grid.setBackground(BG);
        cells = new CellButton[3][3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                final int row = r, col = c;
                CellButton btn = new CellButton();
                btn.addActionListener(e -> controller.handleCellClick(row, col));
                cells[r][c] = btn;
                grid.add(btn);
            }
        }
        wrapper.add(grid, BorderLayout.CENTER);

        // Status + ai indicator
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(BG);
        statusPanel.setBorder(new EmptyBorder(14, 0, 0, 0));

        statusLabel = new JLabel("X's turn", SwingConstants.CENTER);
        statusLabel.setFont(loadFont(15f, Font.PLAIN));
        statusLabel.setForeground(TEXT_DIM);
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        aiIndicator = new JLabel("AI is thinking…", SwingConstants.CENTER);
        aiIndicator.setFont(loadFont(12f, Font.ITALIC));
        aiIndicator.setForeground(O_COLOR);
        aiIndicator.setVisible(false);
        statusPanel.add(aiIndicator, BorderLayout.SOUTH);

        wrapper.add(statusPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 14));
        footer.setBackground(BG);

        newGameBtn    = createActionBtn("New Game", ACCENT);
        resetScoresBtn = createActionBtn("Reset Scores", BORDER_CLR);

        newGameBtn.addActionListener(e -> controller.newGame());
        resetScoresBtn.addActionListener(e -> controller.resetScores());

        footer.add(newGameBtn);
        footer.add(resetScoresBtn);
        return footer;
    }

    // ── Helper builders ───────────────────────────────────────────────

    private JLabel buildScoreCard(String titleText, String valueText, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel titleLbl = new JLabel(titleText, SwingConstants.CENTER);
        titleLbl.setFont(loadFont(11f, Font.BOLD));
        titleLbl.setForeground(accent);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLbl = new JLabel(valueText, SwingConstants.CENTER);
        valueLbl.setFont(loadFont(24f, Font.BOLD));
        valueLbl.setForeground(TEXT_LIGHT);
        valueLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLbl);
        card.add(valueLbl);

        // Wrap so we can return the label for later updates
        // Hack: store valueLbl reference via name
        valueLbl.setName(titleText);
        return valueLbl;
    }

    private JToggleButton createToggleBtn(String text) {
        JToggleButton btn = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = isSelected() ? ACCENT : SURFACE;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(isSelected() ? BG : TEXT_DIM);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(loadFont(12f, Font.BOLD));
        btn.setPreferredSize(new Dimension(100, 30));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createActionBtn(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(bg.equals(ACCENT) ? BG : TEXT_LIGHT);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(loadFont(12f, Font.BOLD));
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private Font loadFont(float size, int style) {
        return new Font("SansSerif", style, (int) size);
    }

    // ── GameView callbacks ────────────────────────────────────────────

    @Override
    public void onGameReset(char firstPlayer) {
        winningLine = null;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                cells[r][c].reset();
        updateStatus(firstPlayer, false);
        aiIndicator.setVisible(false);
        pvpToggle.setSelected(controller.getGameMode() == GameMode.PLAYER_VS_PLAYER);
        pvaiToggle.setSelected(controller.getGameMode() == GameMode.PLAYER_VS_AI);
    }

    @Override
    public void onCellPlaced(int row, int col, char marker) {
        cells[row][col].setMarker(marker);
    }

    @Override
    public void onTurnChanged(char player) {
        updateStatus(player, false);
    }

    @Override
    public void onGameOver(GameState state, int[] line) {
        winningLine = line;
        if (line != null) {
            // Highlight winning cells
            cells[line[0]][line[1]].setWinner(true);
            cells[line[2]][line[3]].setWinner(true);
            cells[line[4]][line[5]].setWinner(true);
        }
        String msg = switch (state) {
            case X_WINS -> "✕  Wins!";
            case O_WINS -> "◯  Wins!";
            case DRAW   -> "It's a draw";
            default     -> "";
        };
        statusLabel.setText(msg);
        statusLabel.setForeground(state == GameState.X_WINS ? X_COLOR
                                : state == GameState.O_WINS ? O_COLOR : ACCENT);
    }

    @Override
    public void onScoreUpdated(ScoreTracker score) {
        xScoreLabel.setText(String.valueOf(score.getXWins()));
        oScoreLabel.setText(String.valueOf(score.getOWins()));
        drawLabel.setText(String.valueOf(score.getDraws()));
    }

    @Override
    public void onAiThinking(boolean thinking) {
        aiIndicator.setVisible(thinking);
    }

    private void updateStatus(char player, boolean gameOver) {
        String turn = player == 'X' ? "X's turn" : "O's turn";
        statusLabel.setText(turn);
        statusLabel.setForeground(TEXT_DIM);
    }

    // ── Inner: CellButton ─────────────────────────────────────────────

    /**
     * A single board cell that draws X or O with smooth appearance.
     */
    class CellButton extends JButton {

        private char marker = Board.EMPTY;
        private boolean winner = false;
        private float alpha = 0f;
        private Timer fadeTimer;

        CellButton() {
            setPreferredSize(new Dimension(110, 110));
            setBackground(SURFACE);
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { repaint(); }
                @Override public void mouseExited(MouseEvent e)  { repaint(); }
            });
        }

        void setMarker(char m) {
            this.marker = m;
            this.alpha  = 0f;
            setEnabled(false);

            fadeTimer = new Timer(16, null);
            fadeTimer.addActionListener(e -> {
                alpha = Math.min(1f, alpha + 0.08f);
                repaint();
                if (alpha >= 1f) fadeTimer.stop();
            });
            fadeTimer.start();
        }

        void setWinner(boolean w) {
            this.winner = w;
            repaint();
        }

        void reset() {
            if (fadeTimer != null) fadeTimer.stop();
            marker = Board.EMPTY;
            winner = false;
            alpha  = 0f;
            setEnabled(true);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // Background
            Color cellBg = winner ? new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 40)
                                  : (getModel().isRollover() && marker == Board.EMPTY)
                                      ? BORDER_CLR : SURFACE;
            g2.setColor(cellBg);
            g2.fillRoundRect(0, 0, w, h, 12, 12);

            // Border
            g2.setColor(winner ? ACCENT : BORDER_CLR);
            g2.setStroke(new BasicStroke(winner ? 2f : 1f));
            g2.drawRoundRect(0, 0, w-1, h-1, 12, 12);

            // Marker
            if (marker == Board.PLAYER_X) drawX(g2, w, h);
            else if (marker == Board.PLAYER_O) drawO(g2, w, h);

            g2.dispose();
        }

        private void drawX(Graphics2D g2, int w, int h) {
            int pad = 24;
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Color c = new Color(X_COLOR.getRed(), X_COLOR.getGreen(), X_COLOR.getBlue(),
                                (int)(alpha * 255));
            g2.setColor(c);
            g2.drawLine(pad, pad, w-pad, h-pad);
            g2.drawLine(w-pad, pad, pad, h-pad);
        }

        private void drawO(Graphics2D g2, int w, int h) {
            int pad = 22;
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Color c = new Color(O_COLOR.getRed(), O_COLOR.getGreen(), O_COLOR.getBlue(),
                                (int)(alpha * 255));
            g2.setColor(c);
            g2.drawOval(pad, pad, w-2*pad, h-2*pad);
        }
    }

    // Keep reference to Board.EMPTY accessible inside inner class
    private static final char Board_EMPTY = tictactoe.model.Board.EMPTY;
}
