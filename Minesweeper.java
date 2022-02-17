import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;


public class Minesweeper extends JFrame {
    public static final int SCREEN_SIZE = 800;
    public static final int SIZE = 15;

    private int flags = 0;
    private JLabel flagLabel = new JLabel();

    private boolean isFirstClick = true;

    private JPanel main;

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
        main = new JPanel();
        main.setLayout(new GridLayout(SIZE, SIZE));

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Button button = new Button(this, (int) (Math.random() * 6) == 0, row, col);
                grid[row][col] = button;
                main.add(button);
            }
        }

        JPanel flagPanel = new JPanel();
        add(flagPanel, BorderLayout.NORTH);

        flagPanel.add(new JLabel() {
            private final int SIZE = 40;
            public Dimension getPreferredSize() {
                return new Dimension(SIZE, SIZE);
            }
            public void paintComponent(Graphics g) {
                g.setColor(Color.RED);
                g.fillPolygon(new int[] {SIZE / 4, SIZE, SIZE * 3 / 8, SIZE * 3 / 8, SIZE / 4}, new int[] {0, SIZE / 4, SIZE / 2, SIZE, SIZE}, 5);
            }
        });
        flagLabel.setText("" + flags);
        flagPanel.add(flagLabel);

        add(main);
    }

    public void buttonClicked(Button button, boolean isRightClick, boolean isDoubleClick) {
        if (isFirstClick) {
            isFirstClick = false;
            for (int r = -2; r <= 2; r++) {
                for (int c = -2; c <= 2; c++) {
                    grid[button.getRow() + r][button.getCol() + c].neutralize();
                }
            }
            
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    Button b = grid[row][col];
                    if (!b.isMine()) {
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
                        b.setNeighborMines(neighborMines);
                    } else {
                        flags += 1;
                    }
                }
            }
            flagLabel.setText("" + flags);
        }

        if (isRightClick) {
            button.flag();
        } else if (isDoubleClick) {
            for (int row = button.getRow() - 1; row <= button.getRow() + 1; row++) {
                for (int col = button.getCol() - 1; col <= button.getCol() + 1; col++) {
                    if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
                        if (!grid[row][col].isRevealed() && !grid[row][col].isFlagged()) {
                            buttonClicked(grid[row][col], false, false);
                        }
                    }
                }
            }
        } else {
            button.reveal();
            if (button.isMine()) {
                gameOver();
            } else if (button.getNeighborMines() == 0) {
                ArrayList<Button> safeNeighbors = getSafeNeighbors(button.getRow(), button.getCol(), new ArrayList<Button>());
                for (Button neighbor : safeNeighbors) {
                    neighbor.reveal();
                }
            }
        }

        boolean isDone = true;
        if (flags != 0) {
            isDone = false;
        }

        for (int row = 0; row < SIZE && isDone; row++) {
            for (int col = 0; col < SIZE && isDone; col++) {
                if (!grid[row][col].isRevealed() && !grid[row][col].isMine()) {
                    isDone = false;
                }
            }
        }

        if (isDone) {
            JOptionPane.showMessageDialog(null, "You won", "Noice", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    public void changeFlags(int change) {
        flags += change;
        flagLabel.setText("" + flags);
    }

    private void gameOver() {
        JFrame frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setVisible(true);
        try {
            frame.add(new JLabel(new ImageIcon(ImageIO.read(new File("explosion.png")))));
        } catch(Exception e) {
            e.printStackTrace();
        }
        frame.setResizable(false);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        // JOptionPane.showMessageDialog(null, "You messed up", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        // System.exit(0);
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
