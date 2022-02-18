import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
public class Minesweeper extends JFrame {
    public static final int SCREEN_SIZE = 800;
    private int size;
    private int flags = 0;
    private JLabel flagLabel = new JLabel();
    private boolean isFirstClick = true;
    private JPanel main;
    private Button[][] grid;
    
    public Minesweeper(int size) {
        this.size = size;
        this.grid = new Button[size][size];
        setTitle("Minesweeper");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initGUI();
        pack();
    }

    private void initGUI() {
        ImageIcon img = null;
        try {
            img = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("icon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setIconImage(img.getImage());
        
        // Main Panel
        main = new JPanel();
        main.setLayout(new GridLayout(size, size));
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Button button = new Button(this, (int) (Math.random() * 6) == 0, row, col);
                grid[row][col] = button;
                main.add(button);
            }
        }

        // Flag Panel
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

        // Difficulty Menu
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu difficultyMenu = new JMenu("Difficulty");
        menuBar.add(difficultyMenu);
        JMenuItem[] difficultyMenuItems = {new JMenuItem("Easy"), new JMenuItem("Normal"), new JMenuItem("Hard"), new JMenuItem("Insane")};
        
        for (JMenuItem menuItem : difficultyMenuItems) {
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int newSize = 0;
                    switch(menuItem.getText()) {
                        case "Easy":
                            newSize = 10;
                            break;
                        case "Normal":
                            newSize = 15;
                            break;
                        case "Hard":
                            newSize = 20;
                            break;
                        case "Insane":
                            newSize = 40;
                    }
                    endGame();
                    new Minesweeper(newSize);
                }
            });
            difficultyMenu.add(menuItem);
        }

        add(main);
    }

    public void buttonClicked(Button button, boolean isRightClick, boolean isDoubleClick) {
        // Initialize board (count mines)
        if (isFirstClick) {
            isFirstClick = false;
            // To make sure the player gets something to start with, remove the mines two out in every direction
            for (int r = -2; r <= 2; r++) {
                for (int c = -2; c <= 2; c++) {
                    grid[button.getRow() + r][button.getCol() + c].neutralize();
                }
            }
            
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    Button b = grid[row][col];
                    if (!b.isMine()) {
                        int neighborMines = 0;
                        for (int r = -1; r < 2; r++) {
                            for (int c = -1; c < 2; c++) {
                                if (row + r > -1 && row + r < size && col + c > -1 && col + c < size && !(r == 0 && c == 0)) {
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
            // click all surrounding boxes
            for (int row = button.getRow() - 1; row <= button.getRow() + 1; row++) {
                for (int col = button.getCol() - 1; col <= button.getCol() + 1; col++) {
                    if (row >= 0 && row < size && col >= 0 && col < size) {
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

        for (int row = 0; row < size && isDone; row++) {
            for (int col = 0; col < size && isDone; col++) {
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

    // This is a recursive function that continues adding neighbors to the same ArrayList object, safeNeighbors
    private ArrayList<Button> getSafeNeighbors(int row, int col, ArrayList<Button> safeNeighbors) {
        for (int r = -1; r < 2; r++) {
            for (int c = -1; c < 2; c++) {
                if (row + r > -1 && row + r < size && col + c > -1 && col + c < size && !(r == 0 && c == 0)) {
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

    private void gameOver() {
        // boom
        JFrame frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setVisible(true);
        try {
            frame.add(new JLabel(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("explosion.png")))));
        } catch(Exception e) {
            e.printStackTrace();
        }
        frame.setResizable(false);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    private void endGame() {
        this.dispose();
    }

    public void changeFlags(int change) {
        flags += change;
        flagLabel.setText("" + flags);
    }

    public int getGridSize() {
        return size;
    }

    public static void main(String[] arg0) {
        new Minesweeper(15);
    }
}
