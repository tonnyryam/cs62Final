import java.util.*;

public class FriendPollingSystem {
    // Stores each user and the set of their friends
    private Map<String, Set<String>> friendGraph;

    // Stores whether each user is at the dining hall
    private Map<String, Boolean> diningStatus;

    public FriendPollingSystem() {
        friendGraph = new HashMap<>();
        diningStatus = new HashMap<>();
    }

    // Add a new user
    public void addUser(String userName) {
        if (!friendGraph.containsKey(userName)) {
            friendGraph.put(userName, new HashSet<>());
            diningStatus.put(userName, false); // default: not at dining hall
        }
    }

    // Add a two-way friendship
    public void addFriendship(String user1, String user2) {
        if (friendGraph.containsKey(user1) && friendGraph.containsKey(user2)) {
            friendGraph.get(user1).add(user2);
            friendGraph.get(user2).add(user1);
        }
    }

    // Set whether a user is at the dining hall
    public void setDiningStatus(String userName, boolean isAtDiningHall) {
        if (diningStatus.containsKey(userName)) {
            diningStatus.put(userName, isAtDiningHall);
        }
    }

    // Get friends currently at the dining hall
    public Set<String> getFriendsAtDiningHall(String userName) {
        Set<String> result = new HashSet<>();
        if (!friendGraph.containsKey(userName)) return result;

        for (String friend : friendGraph.get(userName)) {
            if (diningStatus.getOrDefault(friend, false)) {
                result.add(friend);
            }
        }
        return result;
    }

    // Main method to demonstrate functionality
    public static void main(String[] args) {
        FriendPollingSystem system = new FriendPollingSystem();

        // Add users
        system.addUser("Alice");
        system.addUser("Bob");
        system.addUser("Charlie");
        system.addUser("Diana");

        // Add friendships
        system.addFriendship("Alice", "Bob");
        system.addFriendship("Alice", "Charlie");
        system.addFriendship("Bob", "Diana");

        // Set dining statuses
        system.setDiningStatus("Charlie", true);
        system.setDiningStatus("Diana", true);

        // Print results
        System.out.println("Alice's friends at the dining hall: " + system.getFriendsAtDiningHall("Alice"));
        System.out.println("Bob's friends at the dining hall: " + system.getFriendsAtDiningHall("Bob"));
    }
}
