package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Main GUI window for the Tic-Tac-Toe game.
 * Uses Java Swing with custom painting for a polished look.
 */
public class GameWindow extends JFrame {

    // ── Colour palette ───────────────────────────────────────────────────────
    private static final Color BG          = new Color(15, 15, 25);
    private static final Color PANEL_BG    = new Color(22, 22, 38);
    private static final Color CELL_IDLE   = new Color(30, 30, 50);
    private static final Color CELL_HOVER  = new Color(40, 40, 68);
    private static final Color ACCENT_X    = new Color(255, 99, 132);
    private static final Color ACCENT_O    = new Color(54, 162, 235);
    private static final Color ACCENT_WIN  = new Color(75, 220, 150);
    private static final Color TEXT_MAIN   = new Color(220, 220, 240);
    private static final Color TEXT_DIM    = new Color(120, 120, 150);

    private final GameController controller;
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JButton newGameBtn;
    private JToggleButton modeToggle;

    public GameWindow() {
        controller = new GameController();
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        setTitle("Tic-Tac-Toe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    // ── Header ───────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 14, 24));

        JLabel title = new JLabel("TIC-TAC-TOE");
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setForeground(TEXT_MAIN);
        header.add(title, BorderLayout.WEST);

        scoreLabel = new JLabel(getScoreText());
        scoreLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
        scoreLabel.setForeground(TEXT_DIM);
        header.add(scoreLabel, BorderLayout.EAST);

        return header;
    }

    // ── Footer ───────────────────────────────────────────────────────────────

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setBackground(PANEL_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(14, 24, 18, 24));

        statusLabel = new JLabel(getStatusText(), SwingConstants.CENTER);
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        statusLabel.setForeground(TEXT_MAIN);
        footer.add(statusLabel, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        modeToggle = new JToggleButton("vs CPU");
        modeToggle.setSelected(true);
        styleButton(modeToggle, TEXT_DIM);
        modeToggle.addActionListener(e -> {
            GameController.GameMode m = modeToggle.isSelected()
                ? GameController.GameMode.PVC
                : GameController.GameMode.PVP;
            modeToggle.setText(modeToggle.isSelected() ? "vs CPU" : "2 Player");
            controller.startGame(m);
            refresh();
        });

        newGameBtn = new JButton("New Game");
        styleButton(newGameBtn, ACCENT_X);
        newGameBtn.addActionListener(e -> {
            controller.newRound();
            refresh();
        });

        JButton resetBtn = new JButton("Reset Scores");
        styleButton(resetBtn, TEXT_DIM);
        resetBtn.addActionListener(e -> {
            controller.resetScores();
            refresh();
        });

        buttons.add(resetBtn);
        buttons.add(modeToggle);
        buttons.add(newGameBtn);
        footer.add(buttons, BorderLayout.EAST);

        return footer;
    }

    private void styleButton(AbstractButton btn, Color fg) {
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(new Color(35, 35, 58));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 55, 85), 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ── Refresh ──────────────────────────────────────────────────────────────

    private void refresh() {
        statusLabel.setText(getStatusText());
        scoreLabel.setText(getScoreText());
        boardPanel.repaint();
    }

    private String getStatusText() {
        return switch (controller.getState()) {
            case WIN_X -> "✦ Player X wins!";
            case WIN_O -> controller.getMode() == GameController.GameMode.PVC
                          ? "✦ CPU wins!" : "✦ Player O wins!";
            case DRAW  -> "— Draw —";
            default    -> controller.getCurrentPlayer() == Board.PLAYER_X
                          ? "Player X's turn" : (controller.getMode() == GameController.GameMode.PVC
                                                  ? "CPU thinking…" : "Player O's turn");
        };
    }

    private String getScoreText() {
        return String.format("X  %d  ·  Draw  %d  ·  O  %d",
            controller.getScoreX(), controller.getScoreDraw(), controller.getScoreO());
    }

    // ── Board Panel ──────────────────────────────────────────────────────────

    private class BoardPanel extends JPanel {

        private static final int CELL  = 120;
        private static final int GAP   = 4;
        private static final int SIDE  = CELL * Board.SIZE + GAP * (Board.SIZE + 1);
        private static final int PAD   = 24;

        private int hoverRow = -1, hoverCol = -1;

        BoardPanel() {
            setPreferredSize(new Dimension(SIDE + PAD * 2, SIDE + PAD * 2));
            setBackground(BG);

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int[] rc = cellAt(e.getX(), e.getY());
                    if (rc != null) { hoverRow = rc[0]; hoverCol = rc[1]; }
                    else             { hoverRow = -1;   hoverCol = -1;   }
                    repaint();
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int[] rc = cellAt(e.getX(), e.getY());
                    if (rc != null && controller.getState() == GameController.GameState.PLAYING) {
                        controller.handleMove(rc[0], rc[1]);
                        refresh();
                    }
                }
                @Override public void mouseExited(MouseEvent e) {
                    hoverRow = -1; hoverCol = -1; repaint();
                }
            });
        }

        private int[] cellAt(int px, int py) {
            int ox = PAD + GAP, oy = PAD + GAP;
            int r = (py - oy) / (CELL + GAP);
            int c = (px - ox) / (CELL + GAP);
            if (r < 0 || r >= Board.SIZE || c < 0 || c >= Board.SIZE) return null;
            int x0 = ox + c * (CELL + GAP);
            int y0 = oy + r * (CELL + GAP);
            if (px >= x0 && px < x0 + CELL && py >= y0 && py < y0 + CELL)
                return new int[]{r, c};
            return null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Board board = controller.getBoard();
            int ox = PAD + GAP, oy = PAD + GAP;

            // ── Draw cells ──────────────────────────────────────────────────
            for (int r = 0; r < Board.SIZE; r++) {
                for (int c = 0; c < Board.SIZE; c++) {
                    int x = ox + c * (CELL + GAP);
                    int y = oy + r * (CELL + GAP);
                    boolean hover = (r == hoverRow && c == hoverCol);

                    // Cell background
                    Color bg = hover && board.getCell(r, c) == Board.EMPTY
                               ? CELL_HOVER : CELL_IDLE;
                    g2.setColor(bg);
                    g2.fillRoundRect(x, y, CELL, CELL, 12, 12);

                    // Mark
                    char mark = board.getCell(r, c);
                    if (mark == Board.PLAYER_X) drawX(g2, x, y, CELL, false);
                    else if (mark == Board.PLAYER_O) drawO(g2, x, y, CELL, false);
                }
            }

            // ── Win highlight ────────────────────────────────────────────────
            GameController.GameState state = controller.getState();
            if (state == GameController.GameState.WIN_X || state == GameController.GameState.WIN_O) {
                char winner = (state == GameController.GameState.WIN_X) ? Board.PLAYER_X : Board.PLAYER_O;
                int[][] line = board.getWinLine(winner);
                if (line != null) {
                    highlightWinCells(g2, board, winner, ox, oy);
                    drawWinLine(g2, line, ox, oy);
                }
            }
        }

        private void highlightWinCells(Graphics2D g2, Board board, char winner, int ox, int oy) {
            int[][] line = board.getWinLine(winner);
            if (line == null) return;
            // Collect winning cells
            java.util.List<int[]> cells = new java.util.ArrayList<>();
            int dr = Integer.compare(line[1][0], line[0][0]);
            int dc = Integer.compare(line[1][1], line[0][1]);
            int r = line[0][0], c = line[0][1];
            for (int i = 0; i < Board.SIZE; i++) {
                cells.add(new int[]{r + i * dr, c + i * dc});
            }
            for (int[] cell : cells) {
                int x = ox + cell[1] * (CELL + GAP);
                int y = oy + cell[0] * (CELL + GAP);
                g2.setColor(new Color(75, 220, 150, 40));
                g2.fillRoundRect(x, y, CELL, CELL, 12, 12);
                if (winner == Board.PLAYER_X) drawX(g2, x, y, CELL, true);
                else drawO(g2, x, y, CELL, true);
            }
        }

        private void drawX(Graphics2D g2, int x, int y, int size, boolean win) {
            int pad = size / 4;
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(win ? ACCENT_WIN : ACCENT_X);
            g2.draw(new Line2D.Float(x + pad, y + pad, x + size - pad, y + size - pad));
            g2.draw(new Line2D.Float(x + size - pad, y + pad, x + pad, y + size - pad));
        }

        private void drawO(Graphics2D g2, int x, int y, int size, boolean win) {
            int pad = size / 4;
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(win ? ACCENT_WIN : ACCENT_O);
            g2.draw(new Ellipse2D.Float(x + pad, y + pad, size - pad * 2, size - pad * 2));
        }

        private void drawWinLine(Graphics2D g2, int[][] line, int ox, int oy) {
            float cx1 = ox + line[0][1] * (CELL + GAP) + CELL / 2f;
            float cy1 = oy + line[0][0] * (CELL + GAP) + CELL / 2f;
            float cx2 = ox + line[1][1] * (CELL + GAP) + CELL / 2f;
            float cy2 = oy + line[1][0] * (CELL + GAP) + CELL / 2f;
            g2.setColor(new Color(ACCENT_WIN.getRed(), ACCENT_WIN.getGreen(), ACCENT_WIN.getBlue(), 180));
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                         0, new float[]{10, 6}, 0));
            g2.draw(new Line2D.Float(cx1, cy1, cx2, cy2));
        }
    }

    // ── Entry point ──────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new GameWindow();
        });
    }
}
