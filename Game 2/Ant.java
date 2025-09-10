/**
 * A single ant with a very small brain, finite state machine
 * States:
 *  - SEARCHING_FOOD: random walk to locate food
 *  - RETURNING_HOME: greedy step toward home
 *  - SEARCHING_WATER: random walk to locate water after drop-off
 *  - DEAD: stepped on poison; no further updates
 */

public class Ant {
    public int x, y;
    public AntState state = AntState.SEARCHING_FOOD;
    public boolean carryingFood = false;

    public Ant(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    // Update one ant for one tick based on its state and the world
    public void update(World world) {
        if (state == AntState.DEAD) return;

        // Movement policy per state
        switch (state) {
            case SEARCHING_FOOD  -> moveRandom(world);
            case RETURNING_HOME  -> moveTo(world.homeX, world.homeY);
            case SEARCHING_WATER -> moveRandom(world);
            case DEAD            -> {}
        }

        // resolve tile effects after the move
        AntCell current = world.grid[x][y];

        switch (current.type) {
            case FOOD -> {

                // Found food only matters if we were looking for food
                if (state == AntState.SEARCHING_FOOD) {
                    carryingFood = true;
                    state = AntState.RETURNING_HOME;
                }
            }
            case WATER -> {

                // Drink only matters if we were looking for water
                if (state == AntState.SEARCHING_WATER) {
                    state = AntState.SEARCHING_FOOD;
                }
            }
            case POISON -> state = AntState.DEAD; // rip
            case EMPTY  -> {}
        }

        // deliver food at home, drop food, become thirsty, spawn new ant
        if (state == AntState.RETURNING_HOME && x == world.homeX && y == world.homeY) {
            carryingFood = false;
            state = AntState.SEARCHING_WATER;
            world.spawnNewAnt(); // births happen immediately
        }
    }

    // random direction walk, clamped to world bounds
    private void moveRandom(World world) {
        int dx = (int)(Math.random() * 3) - 1;
        int dy = (int)(Math.random() * 3) - 1;
        x = Math.max(0, Math.min(world.width  - 1, x + dx));
        y = Math.max(0, Math.min(world.height - 1, y + dy));
    }

    // One step greedily toward target
    private void moveTo(int tx, int ty) {
        if (x < tx) x++; else if (x > tx) x--;
        if (y < ty) y++; else if (y > ty) y--;
    }
}
