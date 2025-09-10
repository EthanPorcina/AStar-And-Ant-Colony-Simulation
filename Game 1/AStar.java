import java.util.*;

/**
 * Implements the A* pathfinding algorithm for navigating a grid
 * Finds the shortest path from start to goal using terrain costs and heuristics
 */
public class AStar {
    public static List<Cell> findPath(Cell[][] grid, Cell start, Cell goal) {
        List<Cell> openList = new ArrayList<>();  // Nodes to be evaluated
        Set<Cell> closedList = new HashSet<>();   // Already evaluated nodes

        // Reset all cell costs and parents before starting
        for (Cell[] row : grid)
            for (Cell cell : row) {
                cell.gCost = Integer.MAX_VALUE;
                cell.hCost = 0;
                cell.fCost = 0;
                cell.parent = null;
            }

        // Start node setup
        start.gCost = 0;
        start.hCost = manhattan(start, goal);
        start.calculateFCost();
        openList.add(start);

        // A* main loop
        while (!openList.isEmpty()) {
            // Get the cell with lowest fCost from the open list
            Cell current = openList.stream().min(Comparator.comparingInt(c -> c.fCost)).orElse(null);
            if (current == goal) return reconstructPath(goal);

            openList.remove(current);
            closedList.add(current);

            // Examine all walkable neighbors
            for (Cell neighbor : getNeighbors(grid, current)) {
                if (closedList.contains(neighbor) || neighbor.isObstacle)
                    continue;

                int tentativeG = current.gCost + neighbor.type.cost;

                // Only update neighbor if a better path is found
                if (tentativeG < neighbor.gCost) {
                    neighbor.gCost = tentativeG;
                    neighbor.hCost = manhattan(neighbor, goal);
                    neighbor.calculateFCost();
                    neighbor.parent = current;

                    if (!openList.contains(neighbor))
                        openList.add(neighbor);
                }
            }
        }

        return Collections.emptyList();  // No path found
    }

    // Constructs path by walking backward from the goal using parent references
    private static List<Cell> reconstructPath(Cell goal) {
        List<Cell> path = new ArrayList<>();
        for (Cell c = goal; c != null; c = c.parent)
            path.add(c);
        Collections.reverse(path);
        return path;
    }

    // Heuristic function using Manhattan distance
    private static int manhattan(Cell a, Cell b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    // Returns 4-directional adjacent cells
    private static List<Cell> getNeighbors(Cell[][] grid, Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int nx = cell.x + dx[i];
            int ny = cell.y + dy[i];
            if (nx >= 0 && ny >= 0 && nx < grid.length && ny < grid[0].length) {
                neighbors.add(grid[nx][ny]);
            }
        }

        return neighbors;
    }
}