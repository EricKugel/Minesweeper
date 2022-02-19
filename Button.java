import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Button extends JButton {
    private Minesweeper minesweeper;

    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;

    private static Button lastClicked = null;

    private int row;
    private int col;

    private int neighborMines = 0;

    private int size;
    private final Color[] textColors = {Color.GREEN, Color.BLUE, Color.RED, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.YELLOW};
    private Font font;

    public Button(Minesweeper minesweeper, boolean isMine, int row, int col) {
        this.minesweeper = minesweeper;
        this.size = Minesweeper.SCREEN_SIZE / minesweeper.getGridSize();
        this.font = new Font(Font.MONOSPACED, Font.BOLD, size * 3 / 4);
        this.isMine = isMine;
        this.isRevealed = false;
        this.isFlagged = false;
        this.row = row;
        this.col = col;
        setFocusable(false);
        setPreferredSize(new Dimension(size, size));
        setBackground(new Color(150, 150, 150));

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                buttonClicked(e);
            }
        });
    }

    public void buttonClicked(MouseEvent e) {
        boolean doubleClick = isRevealed && e.getClickCount() == 2;
        if (lastClicked == null || !this.equals(lastClicked)) {
            doubleClick = false;
        }
        boolean rightClick = SwingUtilities.isRightMouseButton(e);
        minesweeper.buttonClicked(this, rightClick, doubleClick);
        lastClicked = this;
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setFont(font);
        if (isRevealed) {
            if (isMine) {
                // do nothing
            } else {
                g.setColor(new Color(50, 50, 50));
                g.fillRect(0, 0, size, size);
                if (neighborMines > 0) {
                    g.setColor(textColors[neighborMines - 1]);
                    g.drawString("" + neighborMines, size / 4, size * 3 / 4);
                }
            }
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, size, size);
        }

        if (isFlagged) {
            g.setColor(Color.ORANGE);
            g.fillPolygon(new int[] {size / 4, size, size * 3 / 8, size * 3 / 8, size / 4}, new int[] {0, size / 4, size / 2, size, size}, 5);
        }
    }

    public void flag() {
        if (isFlagged) {
            isFlagged = false;
            minesweeper.changeFlags(1);
        } else if (!isRevealed) {
           isFlagged = true;
           minesweeper.changeFlags(-1);
        }
        repaint();
    }

    public void reveal() {
        isRevealed = true;
        if (isFlagged) {
            isFlagged = false;
            minesweeper.changeFlags(1);
        }
        repaint();
    }

    public boolean equals(Button other) {
        return other.getCol() == col && other.getRow() == row;
    }

    public void neutralize() {
        isMine = false;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setNeighborMines(int neighborMines) {
        this.neighborMines = neighborMines;
    }

    public int getNeighborMines() {
        return neighborMines;
    }
}