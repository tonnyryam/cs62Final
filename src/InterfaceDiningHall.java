import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public interface InterfaceDiningHall {
    void enqueue(User user, long simTime);
    void dequeueUsers(long simTime);
    String updateStatus(String statusFlag);
    long getWaitTime(long simTime);
    List<Map.Entry<String, Double>> topDishes(int topN, HashMap<Integer, User> users);

    String getName();
    String getStatus();
    int getMaxSize();
    int getPopularity();
    PriorityQueue<User> getQueue();
}
