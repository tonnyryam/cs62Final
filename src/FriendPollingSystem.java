import java.util.*;

/**
 * @author Tommy Ryan & Miles Chiang
 * 
 * Represents a system for polling friends about their dining hall status.
 * It allows users to add friends, set their dining hall status, and check
 * which friends are currently at a dining hall.
 */
public class FriendPollingSystem implements InterfaceFriendPollingSystem {
    private Map<String, Set<String>> friendGraph;   // Stores each user and their set of friends
    private Map<String, String> diningHallStatus;   // Stores which dining hall each user is at (null if not at one)
    private static final Set<String> VALID_DINING_HALLS = Set.of(   // Set of valid dining halls
        "Frank", "Frary", "Collins", "Oldenborg", "Malott", "Hoch", "McConnel"
    );

    /**
     * Constructor initializes the friend graph and dining hall status maps.
     */
    public FriendPollingSystem() {
        this.friendGraph = new HashMap<>();
        this.diningHallStatus = new HashMap<>();
    }

    /**
     * Adds a user to the system. If the user already exists, it does nothing.
     * @param userName the name of the user to add
     */
    public void addUser(String userName) {
        if (!friendGraph.containsKey(userName)) {   // Check if user already exists
            friendGraph.put(userName, new HashSet<>()); // Initialize with an empty set of friends
            diningHallStatus.put(userName, null); // null means not at a dining hall
        }
    }

    /**
     * Adds a friendship between two users. Both users must already exist in the system.
     * If either user does not exist, nothing happens.
     * @param user1 the first user's name
     * @param user2 the second user's name
     */
    public void addFriendship(String user1, String user2) {
        if (friendGraph.containsKey(user1) && friendGraph.containsKey(user2)) { // Check if both users exist
            friendGraph.get(user1).add(user2);
            friendGraph.get(user2).add(user1);
        }
    }

    /**
     * Sets the dining hall status for a user.
     * If the dining hall is null, the user is marked as not at any dining hall.
     * If the dining hall is not valid, prints an error and does not update.
     * @param userName the user's name
     * @param diningHall the dining hall name (or null)
     */
    public void setDiningHall(String userName, String diningHall) {
        if (!diningHallStatus.containsKey(userName)) return;    // Check if user exists
        if (diningHall == null || VALID_DINING_HALLS.contains(diningHall)) {    // Check if dining hall is valid
            diningHallStatus.put(userName, diningHall);
        } else {
            System.out.println("Invalid dining hall: " + diningHall);   // Print error message
        }
    }

    /**
     * Returns a map of friends who are currently at a dining hall, along with their dining hall names.
     * @param userName the name of the user to check
     * @return a map of friends and their dining halls
     */
    public Map<String, String> getFriendsAtDiningHalls(String userName) {
        Map<String, String> result = new HashMap<>();
        if (!friendGraph.containsKey(userName)) return result;   // Check if user exists

        for (String friend : friendGraph.get(userName)) {   // Iterate through friends
            String hall = diningHallStatus.get(friend);
            if (hall != null) {
                result.put(friend, hall);
            }
        }
        return result;  // Return map of friends at dining halls
    }
}
