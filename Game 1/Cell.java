/**
 * Represents a single cell in the grid
 * Stores coordinates, terrain type, and A* pathfinding metadata
 */
public class Cell {
    public int x, y; // Grid coordinates
    public TerrainType type; // Terrain type (affects movement cost)
    public boolean isObstacle; // True if impassable
    public int gCost, hCost, fCost; // A* cost metrics
    public Cell parent; // For path reconstruction

    public Cell(int x, int y, TerrainType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.isObstacle = (type == TerrainType.OBSTACLE);
    }

    public void calculateFCost() {
        this.fCost = gCost + hCost;
    }
}