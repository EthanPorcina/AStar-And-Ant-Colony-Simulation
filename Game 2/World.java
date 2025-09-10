import java.util.*;

/**
 * Holds the grid and all ants; creates the map and advances the simulation
 * Also handles spawning new ants when food is delivered
 */

public class World {
    public final int width, height;
    public final AntCell[][] grid;
    public final java.util.List<Ant> ants = new ArrayList<>();
    public final int homeX, homeY;

    public World(int width, int height, int startingAnts) {
        this.width = width;
        this.height = height;
        this.grid = new AntCell[width][height];
        this.homeX = width / 2;
        this.homeY = height / 2;

        generateMap();

        // spawn starting ants at home
        for (int i = 0; i < startingAnts; i++) {
            ants.add(new Ant(homeX, homeY));
        }
    }

    // fresh random map each run
    private void generateMap() {

        // Initialize to empty
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                grid[x][y] = new AntCell(x, y, CellType.EMPTY);

        // Random scatter; tweak counts as desired. Stick to lower poison for longer games
        placeRandom(CellType.FOOD, 10);
        placeRandom(CellType.WATER, 6);
        placeRandom(CellType.POISON, 5);

        // Ensure home is safe
        grid[homeX][homeY].type = CellType.EMPTY;
    }

    // Place count tiles of a type into random empty locations (never on home)
    private void placeRandom(CellType type, int count) {
        Random rand = new Random();
        while (count > 0) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);

            if (x == homeX && y == homeY) continue; // never overwrite home

            if (grid[x][y].type == CellType.EMPTY) {
                grid[x][y].type = type;
                count--;
            }
        }
    }

    /**
     * Advance the world by one tick: update every ant.
     * index-based loop so list can grow safely during iteration to fix ConcurrentModificationException crash
     */
    public void update() {
        for (int i = 0; i < ants.size(); i++) {
            ants.get(i).update(this);
        }
    }

    // Called when an ant delivers food to spawn another
    public void spawnNewAnt() {
        ants.add(new Ant(homeX, homeY));
    }
}
