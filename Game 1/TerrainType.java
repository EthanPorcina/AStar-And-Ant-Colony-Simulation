/**
 * Enum of different terrain types and their associated movement costs
 */
public enum TerrainType {
    OPEN(1),
    GRASSLAND(3),
    SWAMPLAND(4),
    OBSTACLE(Integer.MAX_VALUE); // YOU SHALL NOT PASSSSSSSSSSS

    public final int cost;

    TerrainType(int cost) {
        this.cost = cost;
    }
}