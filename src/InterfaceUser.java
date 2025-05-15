import java.util.HashMap;

/**
 * @author Tommy Ryan & Miles Chiang
 * 
 * Represents a user in the system, implementing the InterfaceUser.
 * Each user has a unique ID, a set of ratings for dishes in various dining halls,
 * and an entry time for tracking their status in the system.
 */
public interface InterfaceUser extends Comparable<InterfaceUser> {
    
    /**
     * Adds a rating for a dish in a specific dining hall.
     * If the dining hall does not exist in the ratings map, it creates a new entry.
     * @param diningHall the name of the dining hall
     * @param dish the name of the dish
     * @param rating the rating for the dish
     */
    void rate(String diningHall, String dish, int rating);
    
    /**
     * Retrieves the rating for a specific dish in a dining hall.
     * @param diningHall the name of the dining hall
     * @param dish the name of the dish
     * @return the rating for the dish, or -1 if not found
     */
    HashMap<String, HashMap<String, Integer>> getRatings();
    
    /**
     * Compares this user with another user based on their entry time.
     * @param other the other user to compare with
     * @return a negative integer, zero, or a positive integer as this user is less than,
     *         equal to, or greater than the specified user
     */
    void setEntryTime(long entryTime);
    
    /**
     * Gets the entry time of the user.
     * @return the entry time in milliseconds
     */
    long getEntryTime();
    
    /**
     * Gets the unique ID of the user.
     * @return the user ID
     */
    int getUserID();
}
