// Ant finite states
public enum AntState {
    SEARCHING_FOOD,   // random roam, looking for food tiles
    RETURNING_HOME,   // step-by-step toward home while carrying food
    SEARCHING_WATER,  // random roam, looking for water tiles after drop-off
    DEAD              // stepped on poison, no updates performed
}
