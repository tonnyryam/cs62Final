import java.util.HashMap;

public class User {
    private final int userID;
    private HashMap<String, HashMap<String, Integer>> ratings;
    private long mealTime;

    public User(int userID, long mealTime) {
        this.userID = userID;
        this.ratings = new HashMap<>();
        this.mealTime = System.currentTimeMillis() + mealTime;
    }

    public int getUserID() {
        return userID;
    }

    public long getMealTime() {
        return mealTime;
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

    public int compareMealTimeTo(User other) {
        return Long.compare(this.mealTime, other.mealTime);
    }
}