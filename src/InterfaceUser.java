import java.util.HashMap;

public interface InterfaceUser extends Comparable<InterfaceUser> {
    void rate(String diningHall, String dish, int rating);
    HashMap<String, HashMap<String, Integer>> getRatings();
    void setEntryTime(long entryTime);
    long getEntryTime();
    int getUserID();
}
