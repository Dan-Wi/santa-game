package santa;

public class SantaClaus {
    private Board board;
    private Position position;
    private int numberOfPresents;
    public SantaClaus(Position position,  Board board, int numberOfPresents) {
        this.position = position;
        this.board = board;
        board.getField(position).setHasSanta(true);
        this.numberOfPresents = numberOfPresents;
        board.getField(position).setHasSanta(true);
    }

    public void move(Direction direction) {
        board.getField(position).setHasSanta(false);
        position = board.getPositionOfANeighbour(position, direction);
        board.getField(position).setHasSanta(true);
        if(board.getField(position).hasChild() &&
                board.getField(position).getChild().getActivity() != Child.Activity.SLEEPING) {
            board.setSantaCaught(true);
        }
    }

    public void dropOrPickupGift() {
        Field f = board.getField(position);
        if(f.hasGift()) {
            f.setHasGift(false);
            ++numberOfPresents;
        }
        else if(numberOfPresents > 0) {
            f.setHasGift(true);
            --numberOfPresents;
        }

    }

    public Position getPosition() {
        return position;
    }
}
