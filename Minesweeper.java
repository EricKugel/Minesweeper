import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

public class Minesweeper extends JFrame {
    public static final int SCREEN_SIZE = 500;
    public static final int SIZE = 20;

    private Button[][] grid = new Button[SIZE][SIZE];
    
    public Minesweeper() {
        setTitle("Minesweeper");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initGUI();
        pack();
    }

    private void initGUI() {
        JPanel main = new JPanel();
        main.setLayout(new GridLayout(SIZE, SIZE));

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Button button = new Button(this, (int) (Math.random() * 6) == 0, row, col);
                grid[row][col] = button;
                main.add(button);
            }
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Button button = grid[row][col];
                if (!button.isMine()) {
                    int neighborMines = 0;
                    for (int r = -1; r < 2; r++) {
                        for (int c = -1; c < 2; c++) {
                            if (row + r > -1 && row + r < SIZE && col + c > -1 && col + c < SIZE && !(r == 0 && c == 0)) {
                                if (grid[row + r][col + c].isMine()) {
                                    neighborMines++;
                                }
                            }
                        }
                    }
                    button.setNeighborMines(neighborMines);
                }
            }
        }

        add(main);
    }

    public void buttonClicked(Button button, boolean isRightClick) {
        if (isRightClick) {
            button.flag();
        } else {
            button.reveal();
            if (button.isMine()) {
                gameOver();
            } else if (button.getNeighborMines() == 0) {
                int row = button.getRow();
                int col = button.getCol();
                ArrayList<Button> safeNeighbors = getSafeNeighbors(row, col, new ArrayList<Button>());
                for (Button neighbor : safeNeighbors) {
                    neighbor.reveal();
                }
            }
        }
    }

    private void gameOver() {
        JOptionPane.showMessageDialog(this, "Game Over", "You messed up", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private ArrayList<Button> getSafeNeighbors(int row, int col, ArrayList<Button> safeNeighbors) {
        for (int r = -1; r < 2; r++) {
            for (int c = -1; c < 2; c++) {
                if (row + r > -1 && row + r < SIZE && col + c > -1 && col + c < SIZE && !(r == 0 && c == 0)) {
                    boolean isCopy = false;
                    for (Button button : safeNeighbors) {
                        if (button.getRow() == row + r && button.getCol() == col + c) {
                            isCopy = true;
                            break;
                        }
                    }
                    if (!isCopy && !grid[row + r][col + c].isMine()) {
                        safeNeighbors.add(grid[row + r][col + c]);
                        if (grid[row + r][col + c].getNeighborMines() == 0) {
                            getSafeNeighbors(row + r, col + c, safeNeighbors);
                        }
                    }
                }
            }
        }
        return safeNeighbors;
    }

    public static void main(String[] arg0) {
        new Minesweeper();
    }
}
