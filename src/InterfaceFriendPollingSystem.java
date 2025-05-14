import java.util.Map;

public interface InterfaceFriendPollingSystem {
    void addUser(String userName);
    void addFriendship(String user1, String user2);
    void setDiningHall(String userName, String diningHall);
    Map<String, String> getFriendsAtDiningHalls(String userName);
}