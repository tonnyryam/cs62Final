import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author Tommy Ryan & Miles Chiang
 * 
 * Interface defining the core behaviors of a Dining Hall system.
 */
public interface InterfaceDiningHall {

    /**
     * Adds a user to the queue and sets their entry time.
     * @param user the user entering
     * @param simTime the current simulation time
     */
    void enqueue(User user, long simTime);

    /**
     * Removes users whose meal time has expired based on simulation time.
     * @param simTime the current simulation time
     */
    void dequeueUsers(long simTime);

    /**
     * Updates and returns the status of the dining hall (open/closed/full/etc).
     * @param statusFlag "open" or "closed"
     * @return the updated status string
     */
    String updateStatus(String statusFlag);

    /**
     * Computes estimated wait time for the current queue.
     * @param simTime current simulation time
     * @return wait time in milliseconds
     */
    long getWaitTime(long simTime);

    /**
     * Returns a list of top-rated dishes by average rating.
     * @param topN the number of top dishes to return
     * @param users a map of all users and their ratings
     * @return list of dish-rating pairs
     */
    List<Map.Entry<String, Double>> topDishes(int topN, HashMap<Integer, User> users);

    String getName();   // Getter for dining hall name
    String getStatus(); // Getter for current status
    int getMaxSize();   // Getter for max size
    int getPopularity();    // Getter for popularity
    PriorityQueue<User> getQueue(); // Getter for the queue
}
