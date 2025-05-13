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
    private final String name;
    private final int maxSize;
    private final int mealTime;
    private Queue<User> queue;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public DiningHall(String name, int maxSize, int mealTime) {
        this.name = name;
        this.maxSize = maxSize;
        this.mealTime = mealTime;
        this.queue = new PriorityQueue<>();
    }

    public void enqueue(User user) {
        user.setEntryTime(System.currentTimeMillis());
        queue.add(user);
    }

    public void startDequeueing() {
        scheduler.scheduleAtFixedRate(this::dequeueExpiredUsers, 0, 1, TimeUnit.SECONDS);
    } 

    private void dequeueExpiredUsers() {
        long currentTime = System.currentTimeMillis();
        while (!queue.isEmpty()) {
            User user = queue.peek();
            if (user.getEntryTime() + mealTime <= currentTime) {
                System.out.println("Dequeued user: " + user.getUserID());
                user.setEntryTime(-1);
                queue.poll();
            } else {
                break;
            }
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

    public int getMaxSize() {
        return this.maxSize;
    }

    public int getWaitTime(){
        int waitTime = 0;
        int peopleAhead = Math.max(0, queue.size() - getMaxSize());
        int count = 0;
        for (User user : queue) {
            if (count >= peopleAhead) {
                break;
            }
            waitTime += mealTime - (System.currentTimeMillis() - user.getEntryTime());
            count++;
        }
        return waitTime;
    }

    public double getDensity() {
        return queue.size() / getMaxSize();
    }

    public static void main(String[] args) throws InterruptedException {
        DiningHall diningHall = new DiningHall("Fdady", 1, 3000);
        diningHall.startDequeueing();
        diningHall.enqueue(new User(1));
        diningHall.enqueue(new User(2));
        diningHall.enqueue(new User(3));
        diningHall.enqueue(new User(4));
        diningHall.enqueue(new User(5));
        diningHall.enqueue(new User(6));


        for (int i = 0; i < 10; i++) {
            System.out.println("Other task running: " + i);
            System.out.println(diningHall.getWaitTime());
            Thread.sleep(1000);
        }

        diningHall.stopDequeueing();
    }
}
