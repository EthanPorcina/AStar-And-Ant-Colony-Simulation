// A single tile on the world grid used by the FSM

public class AntCell {
    public int x, y;
    public CellType type;

    public AntCell(int x, int y, CellType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
