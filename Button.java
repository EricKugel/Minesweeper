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

    private static final int SIZE = Minesweeper.SCREEN_SIZE / Minesweeper.SIZE;
    private static final Color[] textColors = {Color.GREEN, Color.BLUE, Color.RED, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.YELLOW};
    private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, SIZE * 3 / 4);

    public Button(Minesweeper minesweeper, boolean isMine, int row, int col) {
        this.minesweeper = minesweeper;
        this.isMine = isMine;
        this.isRevealed = false;
        this.isFlagged = false;
        this.row = row;
        this.col = col;
        setFocusable(false);
        setPreferredSize(new Dimension(SIZE, SIZE));
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

    public boolean equals(Button other) {
        return other.getCol() == col && other.getRow() == row;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void neutralize() {
        isMine = false;
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

    public void setNeighborMines(int neighborMines) {
        this.neighborMines = neighborMines;
    }

    public int getNeighborMines() {
        return neighborMines;
    }

    public void reveal() {
        isRevealed = true;
        if (isFlagged) {
            isFlagged = false;
            minesweeper.changeFlags(1);
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setFont(FONT);
        if (isRevealed) {
            if (isMine) {
                // do nothing
            } else {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, SIZE, SIZE);
                if (neighborMines > 0) {
                    g.setColor(textColors[neighborMines - 1]);
                    g.drawString("" + neighborMines, SIZE / 4, SIZE * 3 / 4);
                }
            }
        } else {
            g.setColor(new Color(220, 220, 220));
            g.fillRect(0, 0, SIZE, SIZE);
        }

        if (isFlagged) {
            g.setColor(Color.ORANGE);
            g.fillPolygon(new int[] {SIZE / 4, SIZE, SIZE * 3 / 8, SIZE * 3 / 8, SIZE / 4}, new int[] {0, SIZE / 4, SIZE / 2, SIZE, SIZE}, 5);
        }
    }
}
