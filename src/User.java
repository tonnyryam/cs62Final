import java.util.HashMap;

public class User implements Comparable<User> {
    private final int userID;
    private HashMap<String, HashMap<String, Integer>> ratings;
    private long entryTime;

    public User(int userID) {
        this.userID = userID;
        this.ratings = new HashMap<>();
        this.entryTime = -1;
    }

    public int getUserID() {
        return userID;
    }

    public void rate(String diningHall, String dish, int rating) {
        if (!ratings.containsKey(diningHall)) {
            ratings.put(diningHall, new HashMap<>());
        }
        ratings.get(diningHall).put(dish, rating);
    }

    public HashMap<String, HashMap<String, Integer>> getRatings() {
        return ratings;
    }

    public void setEntryTime(long entryTime){
        this.entryTime = entryTime;
    }

    public long getEntryTime() {
        return entryTime;
    }

    @Override
    public int compareTo(User other) {
        return Long.compare(this.entryTime, other.entryTime);
    }
}