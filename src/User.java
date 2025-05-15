import java.util.HashMap;

/**
 * @author Tommy Ryan & Miles Chiang
 * 
 * Represents a user in the system, implementing the InterfaceUser.
 * Each user has a unique ID, a set of ratings for dishes in various dining halls,
 * and an entry time for tracking their status in the system.
 */
public class User implements InterfaceUser {
    private final int userID;
    private HashMap<String, HashMap<String, Integer>> ratings;
    private long entryTime;

    /**
     * Constructor initializes the user with a unique ID and an empty ratings map.
     * @param userID the unique ID for the user
     */
    public User(int userID) {
        this.userID = userID;
        this.ratings = new HashMap<>();
        this.entryTime = -1;
    }
    
    /**
     * Adds a rating for a dish in a specific dining hall.
     * If the dining hall does not exist in the ratings map, it creates a new entry.
     * @param diningHall the name of the dining hall
     * @param dish the name of the dish
     * @param rating the rating for the dish
     */
    public void rate(String diningHall, String dish, int rating) {
        String hallKey = diningHall.toLowerCase().trim();   // Normalize the dining hall name
        if (!ratings.containsKey(hallKey)) {    // Check if the dining hall exists
            ratings.put(hallKey, new HashMap<>());
        }
        ratings.get(hallKey).put(dish.trim(), rating);  // Add the rating for the dish
    }

    /**
     * Retrieves the rating for a specific dish in a dining hall.
     * @param diningHall the name of the dining hall
     * @param dish the name of the dish
     * @return the rating for the dish, or -1 if not found
     */
    @Override
    public int compareTo(InterfaceUser other) {
        return Long.compare(this.entryTime, other.getEntryTime());
    }

    @Override public HashMap<String, HashMap<String, Integer>> getRatings() { return this.ratings;} // Getter for ratings
    @Override public void setEntryTime(long entryTime) { this.entryTime = entryTime; }  // Setter for entry time
    @Override public long getEntryTime() { return this.entryTime; } // Getter for entry time
    @Override public int getUserID() { return this.userID; }    // Getter for user ID
}
