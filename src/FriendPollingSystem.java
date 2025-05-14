import java.util.*;

public class FriendPollingSystem {
    // Stores each user and their set of friends
    private Map<String, Set<String>> friendGraph;

    // Stores which dining hall each user is at (null if not at one)
    private Map<String, String> diningHallStatus;

    // Set of valid dining halls
    private static final Set<String> VALID_DINING_HALLS = Set.of(
        "Frank", "Frary", "Collins", "Oldenborg", "Mallot", "Hoch", "McConnel"
    );

    public FriendPollingSystem() {
        friendGraph = new HashMap<>();
        diningHallStatus = new HashMap<>();
    }

    // Add a new user
    public void addUser(String userName) {
        if (!friendGraph.containsKey(userName)) {
            friendGraph.put(userName, new HashSet<>());
            diningHallStatus.put(userName, null); // null means not at a dining hall
        }
    }

    // Add a mutual friendship
    public void addFriendship(String user1, String user2) {
        if (friendGraph.containsKey(user1) && friendGraph.containsKey(user2)) {
            friendGraph.get(user1).add(user2);
            friendGraph.get(user2).add(user1);
        }
    }

    // Set which dining hall a user is at
    public void setDiningHall(String userName, String diningHall) {
        if (!diningHallStatus.containsKey(userName)) return;
        if (diningHall == null || VALID_DINING_HALLS.contains(diningHall)) {
            diningHallStatus.put(userName, diningHall);
        } else {
            System.out.println("Invalid dining hall: " + diningHall);
        }
    }

    // Get friends who are currently at a dining hall
    public Map<String, String> getFriendsAtDiningHalls(String userName) {
        Map<String, String> result = new HashMap<>();
        if (!friendGraph.containsKey(userName)) return result;

        for (String friend : friendGraph.get(userName)) {
            String hall = diningHallStatus.get(friend);
            if (hall != null) {
                result.put(friend, hall);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        FriendPollingSystem system = new FriendPollingSystem();

        // Add users
        system.addUser("Tommy");
        system.addUser("Miles");
        system.addUser("John");
        system.addUser("Bob");

        // Add friendships
        system.addFriendship("Tommy", "Miles");
        system.addFriendship("Miles", "John");
        system.addFriendship("Bob", "John");

        // Set dining hall status
        system.setDiningHall("Miles", "Frary");
        system.setDiningHall("John", "Collins");

        // Print results
        System.out.println("Tommy's friends at dining halls: " + system.getFriendsAtDiningHalls("Tommy"));
        System.out.println("Bob's friends at dining halls: " + system.getFriendsAtDiningHalls("Bob"));
    }
}
