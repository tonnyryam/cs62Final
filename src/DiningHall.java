
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DiningHall {
    private String name;
    private Queue<User> queue;
    private ScheduledExecutorService scheduler;

    public DiningHall(String name) {
        this.name = name;
        this.queue = new PriorityQueue<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void enqueue(User user) {
        queue.add(user);
    }

    public void startDequeueing() {
        scheduler.scheduleAtFixedRate(this::dequeueExpiredUsers, 0, 1, TimeUnit.SECONDS);
    } 

    private void dequeueExpiredUsers() {
        long currentTime = System.currentTimeMillis();
        while (!queue.isEmpty() && (queue.peek().getMealTime() < currentTime)) {
            User expiredUser = queue.poll();
            System.out.println("User " + expiredUser.getUserID() + " has expired and is removed from the queue.");
        }
    }

    public void stopDequeueing() {
        scheduler.shutdown();
    }

    public List<Map.Entry<String,Integer>> topDishes(int topN, ArrayList<User> users) {
        HashMap<String, Integer> dishRatings = new HashMap<>();
        for (User user : users) {
            HashMap<String, Integer> userRatings = user.getRatings().get(this.name);
            if (userRatings != null) {
                for (String dish : userRatings.keySet()) {
                    dishRatings.put(dish, dishRatings.getOrDefault(dish, 0) + userRatings.get(dish));
                }
            }
        }
        return dishRatings.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        DiningHall diningHall = new DiningHall("Fdady");
        diningHall.enqueue(new User(1, 5000));
        diningHall.enqueue(new User(2, 3000));
        diningHall.enqueue(new User(3, 7000));
        diningHall.startDequeueing();

        // Simulate some time passing
        try {
            Thread.sleep(10000); // Sleep for 10 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
