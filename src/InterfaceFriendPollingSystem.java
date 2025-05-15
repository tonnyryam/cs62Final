import java.util.Map;

/**
 * @author Tommy Ryan & Miles Chiang
 * 
 * Interface defining the core behaviors of a Friend Polling System.
 * It allows users to add friends, set their dining hall status, and check
 * which friends are currently at a dining hall.
 */
public interface InterfaceFriendPollingSystem {
    
    /**
     * Adds a user to the system. If the user already exists, it does nothing.
     * @param userName the name of the user to add
     */
    void addUser(String userName);  

    /**
     * Adds a friendship between two users. Both users must already exist in the system.
     * If either user does not exist, nothing happens.
     * @param user1 the first user's name
     * @param user2 the second user's name
     */
    void addFriendship(String user1, String user2);

    /**
     * Sets the dining hall status for a user.
     * If the dining hall is null, the user is marked as not at any dining hall.
     * If the dining hall is not valid, prints an error and does not update.
     * @param userName the user's name
     * @param diningHall the dining hall name (or null)
     */
    void setDiningHall(String userName, String diningHall);

    /**
     * Returns a map of friends who are currently at a dining hall, along with their dining hall names.
     * @param userName the name of the user to check
     * @return a map of friends and their dining halls
     */
    Map<String, String> getFriendsAtDiningHalls(String userName);
}