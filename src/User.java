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
    
    
    public void rate(String diningHall, String dish, int rating) {
        String hallKey = diningHall.toLowerCase().trim();
        if (!ratings.containsKey(hallKey)) {
            ratings.put(hallKey, new HashMap<>());
        }
        ratings.get(hallKey).put(dish.trim(), rating);
    }


    @Override
    public int compareTo(User other) {
        return Long.compare(this.entryTime, other.entryTime);
    }


    public HashMap<String, HashMap<String, Integer>> getRatings() { return this.ratings;}
    public void setEntryTime(long entryTime) { this.entryTime = entryTime; }
    public long getEntryTime() { return this.entryTime; }
    public int getUserID() { return this.userID; }
}
