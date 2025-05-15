import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tommy Ryan & Miles Chiang
 * 
 * Represents a simulated Dining Hall with queueing and dish rating features.
 */
public class DiningHall implements InterfaceDiningHall {
    private final String name;
    private final int maxSize;
    private final long mealLength;  // how long a user stays in the hall (in ms)
    private final int popularity;
    private String status;
    private Queue<User> queue;

    public DiningHall(String name, int maxSize, long mealLength, int popularity) {
        this.name = name;
        this.maxSize = maxSize;
        this.mealLength = mealLength;
        this.popularity = popularity;
        this.status = "closed";
        this.queue = new PriorityQueue<>(); // Users are stored in a time-priority queue by entry time
    }

    /**
     * Enqueues a user into the dining hall and records their entry time.
     */
    @Override
    public void enqueue(User user, long simTime) {
        user.setEntryTime(simTime); // Mark when they entered
        queue.add(user);    // Add to the queue
    }

    /**
     * Dequeues users who have finished their meals.
     */
    @Override
    public void dequeueUsers(long simTime) {
        while (!queue.isEmpty()) {   // Check each user in the queue
            User user = queue.peek();   // Look at first in line
            if (user.getEntryTime() + this.mealLength <= simTime) {
                queue.poll();   // Remove if finished eating
            } else {
                break;  // Stop checking once someone isn’t done
            }
        }
    }

    /**
     * Updates the current status of the dining hall based on flag and load.
     */
    @Override
    public String updateStatus(String statusFlag) {
        if (statusFlag.equalsIgnoreCase("closed")) {
            this.status = "closed"; // Mark as closed for new entries
            return "closed";
        }

        if (this.status.equals("closed") && statusFlag.equalsIgnoreCase("open")) {
            this.status = "empty";  // Reopen if transitioning
        }

        if (!this.status.equals("closed")) {
            double density = (double) queue.size() / this.maxSize;   // % fullness
            if (density > 1.0) this.status = "full";
            else if (density >= 0.95) this.status = "packed";
            else if (density >= 0.7) this.status = "busy";
            else if (density >= 0.4) this.status = "moderate";
            else if (density >= 0.1) this.status = "light";
            else this.status = "empty";
        }

        return this.status;
    }

    /**
     * Calculates the estimated wait time for someone just arriving.
     */
    @Override
    public long getWaitTime(long simTime) {
        if (queue.isEmpty()) return 0;  // No line = no wait

        int inHall = Math.min(queue.size(), this.maxSize);   // People currently seated
        int waitingOutside = queue.size() - inHall; // Overflow in line

        List<Long> departureTimes = new ArrayList<>();
        int count = 0;
        for (User user : this.queue) {   // Compute when users inside will leave
            if (count >= inHall) break;
            long leaveTime = user.getEntryTime() + this.mealLength;
            if (leaveTime > simTime) {
                departureTimes.add(leaveTime - simTime);    // Time until this person leaves
            }
            count++;
        }

        if (waitingOutside <= 0) return 0;  // No overflow, no wait

        Collections.sort(departureTimes);   // Soonest to latest departures
        int index = Math.min(waitingOutside - 1, departureTimes.size() - 1);    // Person just outside
        if (index >= 0) {
            return departureTimes.get(index);   // Estimate: when they get a seat
        } else {
            return 0;
        }
    }

    /**
     * Returns the top N dishes by average rating from all users.
     */
    @Override
    public List<Map.Entry<String, Double>> topDishes(int topN, HashMap<Integer, User> users) {
        HashMap<String, Integer> totalRatings = new HashMap<>();    // Dish -> sum of ratings
        HashMap<String, Integer> ratingCounts = new HashMap<>();    // Dish -> count of ratings
        String hallKey = this.name.toLowerCase().trim();

        for (User user : users.values()) {  // Loop through all user ratings
            HashMap<String, HashMap<String, Integer>> userRatings = user.getRatings();
            if (userRatings.containsKey(hallKey)) {
                for (Map.Entry<String, Integer> entry : userRatings.get(hallKey).entrySet()) {
                    String dish = entry.getKey();
                    int rating = entry.getValue();
                    totalRatings.put(dish, totalRatings.getOrDefault(dish, 0) + rating);
                    ratingCounts.put(dish, ratingCounts.getOrDefault(dish, 0) + 1);
                }
            }
        }

        HashMap<String, Double> averageRatings = new HashMap<>();   // Compute average for each dish
        for (String dish : totalRatings.keySet()) {
            double avg = (double) totalRatings.get(dish) / ratingCounts.get(dish);
            averageRatings.put(dish, avg);
        }

        return averageRatings.entrySet()    // Return top N dishes sorted by average rating
            .stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))   // highest to lowest
            .limit(topN)
            .collect(Collectors.toList());
    }

    @Override public String getName() { return this.name; } // Get the name of the dining hall
    @Override public String getStatus() { return this.status; } // Get the current status (e.g., open, busy)
    @Override public int getMaxSize() { return this.maxSize; }  // Get the hall's seating capacity
    @Override public int getPopularity() { return this.popularity; }    // Get the popularity score of the hall
    @Override public PriorityQueue<User> getQueue() { return (PriorityQueue<User>) this.queue; }    // Access the user queue
}
