import javax.swing.*;
import java.awt.*;

/**
 * Swing panel that renders the grid, home, and ants
 * Keeps rendering logic separate from simulation state
 * Also detects when all ants are dead and triggers a game-over callback
 */

public class GamePanel extends JPanel {
    private final World world;
    private final int cellSize = 40; // pixels per grid cell

    // game over callback provided by Main
    private Runnable onGameOver;

    public GamePanel(int width, int height, int startingAnts) {
        this.world = new World(width, height, startingAnts);
    }

    // Called by Main's timer each tick to advance the sim and detect end state
    public void updateSimulation() {
        world.update();

        // all ants are dead
        boolean allDead = true;
        for (Ant ant : world.ants) {
            if (ant.state != AntState.DEAD) {
                allDead = false;
                break;
            }
        }
        if (allDead && onGameOver != null) onGameOver.run();
    }

    // allows Main to attach a callback that runs when the game ends
    public void setOnGameOver(Runnable r) {
        this.onGameOver = r;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw terrain grid
        AntCell[][] grid = world.grid;
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                switch (grid[x][y].type) {
                    case EMPTY -> g.setColor(Color.WHITE);
                    case FOOD  -> g.setColor(Color.GREEN);
                    case WATER -> g.setColor(Color.CYAN);
                    case POISON-> g.setColor(Color.RED);
                }
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                g.setColor(Color.GRAY);
                g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }

        // draw the home, blue outline wasn't good enough so it's filled deep blue and white "H"
        int hx = world.homeX * cellSize;
        int hy = world.homeY * cellSize;

        g.setColor(new Color(0, 90, 255)); // deep blue
        g.fillRect(hx, hy, cellSize, cellSize);

        g.setColor(Color.WHITE); // white H
        g.setFont(g.getFont().deriveFont(Font.BOLD, 18f));
        g.drawString("H", hx + cellSize/2 - 6, hy + cellSize/2 + 7);

        g.setColor(Color.BLACK); // bold outline
        ((Graphics2D) g).setStroke(new BasicStroke(2));
        g.drawRect(hx, hy, cellSize, cellSize);

        // draw ants
        for (Ant ant : world.ants) {
            switch (ant.state) {
                case SEARCHING_FOOD  -> g.setColor(Color.ORANGE);
                case RETURNING_HOME  -> g.setColor(new Color(0, 90, 255)); // deep blue
                case SEARCHING_WATER -> g.setColor(Color.MAGENTA);
                case DEAD            -> g.setColor(Color.BLACK);
            }

            // slight inset circle so it looks centered inside the tile
            g.fillOval(ant.x * cellSize + 10, ant.y * cellSize + 10, 20, 20);
        }
    }
}
