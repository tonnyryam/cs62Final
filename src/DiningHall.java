import java.util.*;
import java.util.stream.Collectors;

public class DiningHall {
    private final String name;
    private final int maxSize;
    private final long mealLength;
    private final int popularity;
    private String status;
    private Queue<User> queue;


    public DiningHall(String name, int maxSize, long mealLength, int popularity) {
        this.name = name;
        this.maxSize = maxSize;
        this.mealLength = mealLength;
        this.popularity = popularity;
        this.status = "closed";
        this.queue = new PriorityQueue<>();
    }


    public void enqueue(User user, long simTime) {
        user.setEntryTime(simTime);
        queue.add(user);
    }


    public void dequeueUsers(long simTime) {
        while (!queue.isEmpty()) {
            User user = queue.peek();
            if (user.getEntryTime() + this.mealLength <= simTime) {
                queue.poll();
            } else {
                break;
            }
        }
    }


    public String updateStatus(String statusFlag) {
        if (statusFlag.equalsIgnoreCase("closed")) {
            this.status = "closed";
            return "closed";
        }

        if (this.status.equals("closed") && statusFlag.equalsIgnoreCase("open")) {
            this.status = "empty";
        }

        if (!this.status.equals("closed")) {
            double density = (double) queue.size() / maxSize;
            if (density > 1.0) this.status = "full";
            else if (density >= 0.95) this.status = "packed";
            else if (density >= 0.7) this.status = "busy";
            else if (density >= 0.4) this.status = "moderate";
            else if (density >= 0.1) this.status = "light";
            else this.status = "empty";
        }

        return this.status;
    }


    public long getWaitTime(long simTime) {
        if (queue.isEmpty()) return 0;

        int inHall = Math.min(queue.size(), maxSize);
        int waitingOutside = queue.size() - inHall;

        List<Long> departureTimes = new ArrayList<>();
        int count = 0;
        for (User user : queue) {
            if (count >= inHall) break;
            long leaveTime = user.getEntryTime() + mealLength;
            if (leaveTime > simTime) {
                departureTimes.add(leaveTime - simTime);
            }
            count++;
        }

        if (waitingOutside <= 0) return 0;

        Collections.sort(departureTimes);
        int index = Math.min(waitingOutside - 1, departureTimes.size() - 1);
        return index >= 0 ? departureTimes.get(index) : 0;
    }


    public List<Map.Entry<String, Double>> topDishes(int topN, HashMap<Integer, User> users) {
        HashMap<String, Integer> totalRatings = new HashMap<>();
        HashMap<String, Integer> ratingCounts = new HashMap<>();
        String hallKey = this.name.toLowerCase().trim();

        for (User user : users.values()) {
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

        HashMap<String, Double> averageRatings = new HashMap<>();
        for (String dish : totalRatings.keySet()) {
            double avg = (double) totalRatings.get(dish) / ratingCounts.get(dish);
            averageRatings.put(dish, avg);
        }

        return averageRatings.entrySet()
            .stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(topN)
            .collect(Collectors.toList());
    }


    public String getName() { return this.name; }
    public String getStatus() { return this.status; }
    public int getMaxSize() { return this.maxSize; }
    public int getPopularity() { return this.popularity; }
    public PriorityQueue<User> getQueue() { return (PriorityQueue<User>) this.queue; }
}
