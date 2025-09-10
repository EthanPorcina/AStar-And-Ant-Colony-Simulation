import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.Timer;
import java.util.List;

/**
 * Main GUI class for the A* pathfinding ant game
 * Allows users to place terrain, set start/goal points, and visualize A* pathfinding
 */

public class Game1GUI extends JFrame {
    private static final int CELL_SIZE = 40; // Size of each button in pixels
    private static final int GRID_SIZE = 16; // 16x16 grid

    private Cell[][] grid = new Cell[GRID_SIZE][GRID_SIZE]; // The terrain grid
    private Cell startCell, goalCell; // Track user-defined start and goal
    private JPanel gridPanel; // The visual grid container


    // builds window, initializes grid, and sets up controls
    public Game1GUI() {
        setTitle("A* Pathfinding - Ant Search");
        setSize(GRID_SIZE * CELL_SIZE + 200, GRID_SIZE * CELL_SIZE + 50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeGrid();
        addGridPanel();
        addControlPanel();
    }

    // Initialize the logical grid with default terrain
    private void initializeGrid() {
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                grid[x][y] = new Cell(x, y, TerrainType.OPEN);
            }
        }
    }


    // Creates the interactive grid of buttons and assigns click behavior so the user can set start, goal, and terrain type
    private void addGridPanel() {
        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                JButton btn = new JButton();
                Cell cell = grid[x][y];
                btn.setBackground(getTerrainColor(cell.type));
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                // When clicked, prompt the user to assign a type to this cell
                btn.addActionListener(e -> {
                    String[] options = {"Start", "Goal", "Open", "Grass", "Swamp", "Obstacle"};
                    String choice = (String) JOptionPane.showInputDialog(
                            this, "Select Cell Type:", "Cell Config",
                            JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                    if (choice == null) return; // User cancelled. Fixes the crash

                    switch (choice) {
                        case "Start" -> {
                            if (startCell != null) updateCellColor(startCell.x, startCell.y);
                            startCell = cell;
                            btn.setBackground(Color.BLUE);
                        }
                        case "Goal" -> {
                            if (goalCell != null) updateCellColor(goalCell.x, goalCell.y);
                            goalCell = cell;
                            btn.setBackground(Color.RED);
                        }
                        case "Open" -> setCellType(cell, btn, TerrainType.OPEN);
                        case "Grass" -> setCellType(cell, btn, TerrainType.GRASSLAND);
                        case "Swamp" -> setCellType(cell, btn, TerrainType.SWAMPLAND);
                        case "Obstacle" -> setCellType(cell, btn, TerrainType.OBSTACLE);
                    }
                });

                gridPanel.add(btn);
            }
        }

        add(gridPanel, BorderLayout.CENTER);
    }

    // Updates a single cell's color based on its current terrain type
    private void updateCellColor(int x, int y) {
        JButton btn = (JButton) gridPanel.getComponent(y * GRID_SIZE + x);
        btn.setBackground(getTerrainColor(grid[x][y].type));
    }

    // Sets the terrain type of a cell and updates its appearance
    private void setCellType(Cell cell, JButton btn, TerrainType type) {
        cell.type = type;
        cell.isObstacle = (type == TerrainType.OBSTACLE);
        btn.setBackground(getTerrainColor(type));
    }

    // Adds the control buttons at the bottom: Run A*, Reset, and Randomize
    private void addControlPanel() {
        JPanel controlPanel = new JPanel();

        JButton runButton = new JButton("Run A*");
        JButton resetButton = new JButton("Reset");
        JButton randomizeButton = new JButton("Randomize");

        runButton.addActionListener(e -> runPathfinding());
        resetButton.addActionListener(e -> resetGrid());
        randomizeButton.addActionListener(e -> randomizeTerrain());

        controlPanel.add(runButton);
        controlPanel.add(resetButton);
        controlPanel.add(randomizeButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    // Triggers A* pathfinding algorithm and visually animates the result path (if found)
    private void runPathfinding() {
        if (startCell == null || goalCell == null) {
            JOptionPane.showMessageDialog(this, "Please set both Start and Goal cells.");
            return;
        }

        List<Cell> path = AStar.findPath(grid, startCell, goalCell);

        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No path found.");
            return;
        }

        // Animate the path step-by-step using a Swing Timer
        Timer timer = new Timer(100, null);
        final int[] index = {0};

        timer.addActionListener(e -> {
            if (index[0] < path.size()) {
                Cell c = path.get(index[0]);
                if (c != startCell && c != goalCell) {
                    JButton btn = (JButton) gridPanel.getComponent(c.y * GRID_SIZE + c.x);
                    btn.setBackground(Color.YELLOW);
                }
                index[0]++;
            } else {
                ((Timer) e.getSource()).stop();
            }
        });

        timer.start();
    }

    // Resets all terrain back to OPEN while preserving the start and goal markers
    private void resetGrid() {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                Cell cell = grid[x][y];
                JButton btn = (JButton) gridPanel.getComponent(y * GRID_SIZE + x);

                if (cell == startCell) {
                    cell.type = TerrainType.OPEN;
                    cell.isObstacle = false;
                    btn.setBackground(Color.BLUE);
                    continue;
                }

                if (cell == goalCell) {
                    cell.type = TerrainType.OPEN;
                    cell.isObstacle = false;
                    btn.setBackground(Color.RED);
                    continue;
                }

                cell.type = TerrainType.OPEN;
                cell.isObstacle = false;
                btn.setBackground(getTerrainColor(TerrainType.OPEN));
            }
        }
    }

    // Randomly assigns terrain types to all grid cells, but keeps the Start and Goal positions untouched
    private void randomizeTerrain() {
        Random rand = new Random();

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                if ((startCell != null && startCell.x == x && startCell.y == y) ||
                    (goalCell != null && goalCell.x == x && goalCell.y == y)) {
                    continue; // Preserve start/goal
                }

                int roll = rand.nextInt(100);
                TerrainType type;

                if (roll < 50) type = TerrainType.OPEN;
                else if (roll < 70) type = TerrainType.GRASSLAND;
                else if (roll < 90) type = TerrainType.SWAMPLAND;
                else type = TerrainType.OBSTACLE;

                Cell cell = grid[x][y];
                cell.type = type;
                cell.isObstacle = (type == TerrainType.OBSTACLE);
                JButton btn = (JButton) gridPanel.getComponent(y * GRID_SIZE + x);
                btn.setBackground(getTerrainColor(type));
            }
        }
    }

    // Returns a color that visually represents each terrain type
    private Color getTerrainColor(TerrainType type) {
        return switch (type) {
            case OPEN -> Color.LIGHT_GRAY;
            case GRASSLAND -> new Color(102, 204, 0);     // Greenish
            case SWAMPLAND -> new Color(153, 102, 0);     // Brownish
            case OBSTACLE -> Color.DARK_GRAY;
        };
    }

    // Launches the game
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Game1GUI().setVisible(true));
    }
}