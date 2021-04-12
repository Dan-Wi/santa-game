package santa;

import javax.swing.*;
import java.awt.*;


public class GameWindow extends JFrame {
    static final int BOARD_ROWS = 12;
    static final int BOARD_COLS = 14;
    static final int BOARD_FIELD_WIDTH = 60;
    static final int WINDOW_WIDTH = (BOARD_COLS+2) * BOARD_FIELD_WIDTH;
    static final int WINDOW_HEIGHT = (BOARD_ROWS+2) * BOARD_FIELD_WIDTH;

    gamePanel panel;

    public GameWindow() throws InterruptedException {
        super("Santa");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocation(500, 100);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new gamePanel(this);
        panel.setLayout(new BorderLayout());
        setContentPane(panel);

        setVisible(true);
    }
}
