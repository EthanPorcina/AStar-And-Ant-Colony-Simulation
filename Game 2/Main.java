import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Entry point for the FSM Ant Simulation
 * Prompts the user for the starting number of ants,
 * builds the window, and drives the simulation with a Swing Timer.
 * Also wires a Game Over callback to stop when all ants are dead
 */

public class Main {
    public static void main(String[] args) {

        // ask user for starting ants
        int startingAnts = 5; // default if user cancels or fat fingers, I did

        String input = JOptionPane.showInputDialog(
                null,
                "Enter starting number of ants:",
                "Ant Simulation Setup",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input != null) {
            try {
                int val = Integer.parseInt(input.trim());
                if (val > 0 && val <= 1000) {
                    startingAnts = val;
                } else {
                    JOptionPane.showMessageDialog(null, "Using default 5. Enter a positive number next time");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid number. Using default 5");
            }
        }

        JFrame frame = new JFrame("Ant Simulation - FSM");
        GamePanel panel = new GamePanel(16, 16, startingAnts); // width, height of ants
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        frame.add(panel);
        frame.setVisible(true);

        // game loop
        Timer timer = new Timer(300, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.updateSimulation();
                panel.repaint();
            }
        });

        // Game Over: stop timer, notify user, close window
        panel.setOnGameOver(() -> {
            timer.stop();
            JOptionPane.showMessageDialog(frame,
                    "All ants are dead! The colony has perished!",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });

        timer.start();
    }
}
