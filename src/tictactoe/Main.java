package tictactoe;

import tictactoe.controller.GameController;
import tictactoe.view.MainWindow;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the Tic Tac Toe application.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            new MainWindow(controller);
        });
    }
}
