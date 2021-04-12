package santa;

public class Position {
    private int x, y;

    public Position() {
        this(0, 0);
    }
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) { this.x = x; }
    public int getX() { return x; }
    public void setY(int y) { this.y = y; }
    public int getY() { return y; }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof  Position)) return false;
        Position p = (Position) obj;
        return p.x == x && p.y == y;
    }
}
