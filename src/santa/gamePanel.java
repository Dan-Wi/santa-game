package santa;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class gamePanel extends JPanel {
    Board board;
    ArrayList<Child> children;
    SantaClaus santaCharacter;
    GameWindow window;

    private class Repaint extends Thread {
        public static final int MILLISECONDS_PER_REPAINT = 50;
        @Override
        public void run() {
            while(true){
                repaint();
                if(isGameOver()) {
                    int response = JOptionPane.showConfirmDialog(window,
                            (doesEveryChildHaveAGift() ? "YOU WIN!" : "YOU LOSE!"),
                    "Game over", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE);
                    window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
                }
                try {Thread.currentThread().sleep(MILLISECONDS_PER_REPAINT);}
                catch(InterruptedException ex) { }
            }
        }
    }
    private Repaint repainter;

    static final int NUMBER_OF_CHILDREN = 12;

    private Shape[][] grid;
    Color backgroundColor = Color.LIGHT_GRAY;
    Color gridColor = Color.BLACK;
    BufferedImage santaImg;
    BufferedImage giftImg;
    BufferedImage sleepingChildImg;
    BufferedImage chasingChildImg;
    BufferedImage childImg;

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private boolean isGameOver() {
        return  board.isSantaCaught() || doesEveryChildHaveAGift();
    }

    private boolean doesEveryChildHaveAGift() {
        for(Child c : children) {
            if(c.getActivity() != Child.Activity.ENJOYING_THE_GIFT) return false;
        }
        return true;
    }

    public gamePanel(GameWindow window) throws InterruptedException {
        repainter = new Repaint();
        this.window = window;
        setFocusable(true);
        board = new Board(window.BOARD_ROWS, window.BOARD_COLS);

        try {
            santaImg = ImageIO.read(new File("img/santa.png"));
            santaImg = resizeImage(santaImg, window.BOARD_FIELD_WIDTH, window.BOARD_FIELD_WIDTH);
            sleepingChildImg = ImageIO.read(new File("img/sleepingChild.png"));
            sleepingChildImg = resizeImage(sleepingChildImg, window.BOARD_FIELD_WIDTH, window.BOARD_FIELD_WIDTH);
            childImg = ImageIO.read(new File("img/child.png"));
            childImg = resizeImage(childImg, window.BOARD_FIELD_WIDTH, window.BOARD_FIELD_WIDTH);
            chasingChildImg = ImageIO.read(new File("img/chasingChild.png"));
            chasingChildImg = resizeImage(chasingChildImg, window.BOARD_FIELD_WIDTH, window.BOARD_FIELD_WIDTH);
            giftImg = ImageIO.read(new File("img/gift.png"));
            giftImg = resizeImage(giftImg, window.BOARD_FIELD_WIDTH/3, window.BOARD_FIELD_WIDTH/3);
        } catch(IOException ex) { System.out.println(ex);}

        // init grid
        grid = new Shape[window.BOARD_ROWS][window.BOARD_COLS];
        for (int row = 0; row < window.BOARD_ROWS; row++) {
            for (int col = 0; col < window.BOARD_COLS; col++) {
                grid[row][col] = new Rectangle(
                        window.BOARD_FIELD_WIDTH * col,
                        window.BOARD_FIELD_WIDTH * row,
                        window.BOARD_FIELD_WIDTH, window.BOARD_FIELD_WIDTH);
            }
        }

        // init santa and children
        santaCharacter = new SantaClaus(new Position(0,0), board, NUMBER_OF_CHILDREN);
        children = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_CHILDREN; ++i) {
            Random gen = new Random();
            int x, y;
            do {
                x = gen.nextInt(board.getMAX_X())+1;
                y = gen.nextInt(board.getMAX_Y())+1;
            } while(board.getField(new Position(x, y)).hasChild());

            children.add(new Child(new Position(x, y), board, santaCharacter));
        }

        // santa movement
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {
                if(!isGameOver()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP -> santaCharacter.move(Direction.UP);
                        case KeyEvent.VK_LEFT -> santaCharacter.move(Direction.LEFT);
                        case KeyEvent.VK_DOWN -> santaCharacter.move(Direction.DOWN);
                        case KeyEvent.VK_RIGHT -> santaCharacter.move(Direction.RIGHT);
                        case KeyEvent.VK_SPACE -> santaCharacter.dropOrPickupGift();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) { }
        });

        for(Child c : children) {
            new Thread(c).start();
        }
        repainter.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (int row = 0; row < window.BOARD_ROWS; row++) {
            for (int col = 0; col < window.BOARD_COLS; col++) {
                if(col == santaCharacter.getPosition().getX() && row == santaCharacter.getPosition().getY() ) {
                    g2d.drawImage(santaImg, grid[row][col].getBounds().x,
                            grid[row][col].getBounds().y, Color.white,null);
                } else if(board.getField(new Position(col, row)).hasChild()) {
                    if(board.getField(new Position(col, row)).getChild().getActivity() == Child.Activity.SLEEPING) {
                        g2d.drawImage(sleepingChildImg, grid[row][col].getBounds().x,
                                grid[row][col].getBounds().y, null);
                    } else if (board.getField(new Position(col, row)).getChild().getActivity() == Child.Activity.CHASING)  {
                        g2d.drawImage(chasingChildImg, grid[row][col].getBounds().x,
                                grid[row][col].getBounds().y, null);
                    } else {
                        g2d.drawImage(childImg, grid[row][col].getBounds().x,
                                grid[row][col].getBounds().y, null);
                    }
                } else {
                    g2d.setColor(backgroundColor);
                    g.fillRect(grid[row][col].getBounds().x,
                            grid[row][col].getBounds().y,
                            grid[row][col].getBounds().width,
                            grid[row][col].getBounds().height
                    );
                }
                if(board.getField(new Position(col, row)).hasGift()) {
                    g2d.drawImage(giftImg, grid[row][col].getBounds().x,
                            grid[row][col].getBounds().y, null);
                }
                g.setColor(gridColor);
                g2d.draw(grid[row][col]);
            }
        }
    }
}
