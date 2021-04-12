package santa;

import java.util.ArrayList;

public class Board {
    private static final int MIN_ROWS  = 10;
    private static final int MIN_COLS  = 10;
    private int ROWS;
    private int COLS;
    private boolean santaCaught = false;

    private Field[][] board;

    public Board(int ROWS, int COLS) {
        if(ROWS < MIN_ROWS || COLS < MIN_COLS) {
            throw new IllegalArgumentException("Rows and cols size"+
                "should be 10 or more");
        }

        this.ROWS = ROWS;
        this.COLS = COLS;

        board = new Field[ROWS][COLS];

        for(int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLS; ++j) {
                board[i][j] = new Field(false, false, null);
            }
        }
    }

    public boolean isSantaCaught() {
        return santaCaught;
    }

    public void setSantaCaught(boolean value) { santaCaught =  value; }

    public int getMAX_X() {return COLS-1; }
    public int getMAX_Y() { return ROWS-1; }

    public Position getPositionOfANeighbour(Position p, Direction d) {
        int x = p.getX(), y = p.getY();
        switch(d) {
            case UP: {
                y = Math.floorMod(y-1, getMAX_Y()+1);
                break;
            }
            case LEFT: {
                x = Math.floorMod(x-1, getMAX_X()+1);
                break;
            }
            case DOWN: {
                y = Math.floorMod(y+1, getMAX_Y()+1);
                break;
            }
            case RIGHT: {
                x = Math.floorMod(x+1, getMAX_X()+1);
                break;
            }
        }
        return new Position(x, y);
    }

    public Field getField(Position p) { return board[p.getY()][p.getX()]; }

    public ArrayList<Position> getNeighboursPositions(Position p, int distanceInFields) {
        ArrayList<Position> result = new ArrayList<>();
        Position currentPos =
                new Position(p.getX(), Math.floorMod(p.getY()-distanceInFields, getMAX_Y()+1));
        int howManySideways = 0;
        for(int i = 0; i <= distanceInFields;  ++i) {
            addPositionsFromGivenDirection(result, currentPos, Direction.LEFT, howManySideways);
            result.add(currentPos);
            addPositionsFromGivenDirection(result, currentPos, Direction.RIGHT, howManySideways);
            currentPos = getPositionOfANeighbour(currentPos, Direction.DOWN);
            howManySideways++;
        }
        howManySideways = distanceInFields-1;
        for(int i = 0; i < distanceInFields;  ++i) {
            addPositionsFromGivenDirection(result, currentPos, Direction.LEFT, howManySideways);
            result.add(currentPos);
            addPositionsFromGivenDirection(result, currentPos, Direction.RIGHT, howManySideways);
            currentPos = getPositionOfANeighbour(currentPos, Direction.DOWN);
            howManySideways--;
        }
        return result;
    }

    private void addPositionsFromGivenDirection(ArrayList<Position> positions,
                                                Position currentPos,
                                                Direction dir,
                                                int howMany) {
        while(howMany-- > 0) {
            currentPos = getPositionOfANeighbour(currentPos, dir);
            positions.add(currentPos);
        }
    }
}
