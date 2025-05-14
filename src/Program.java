import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Program {
    private HashMap<Integer, User> users = new HashMap<>();
    private List<DiningHall> diningHalls;
    private HashMap<DiningHall, Long> openingTimes = new HashMap<>();
    private HashMap<DiningHall, Long> closingTimes = new HashMap<>();
    private FriendPollingSystem friendSystem = new FriendPollingSystem();
    public User currentUser = null;
    public String currentUserName = null;

    public Program() {
        this.users = new HashMap<>();
        this.diningHalls = List.of(
            new DiningHall("Hoch", 500, 2_700_000, 4),
            new DiningHall("McConnel", 500, 2_700_000, 4),
            new DiningHall("Collins", 400, 2_400_000, 3),
            new DiningHall("Malott", 300, 3_000_000, 5),
            new DiningHall("Frank", 400, 2_400_000, 2),
            new DiningHall("Frary", 400, 2_400_000, 3),
            new DiningHall("Oldenborg", 100, 1_800_000, 1)
        );

        openingTimes.put(diningHalls.get(0), 30000L);
        openingTimes.put(diningHalls.get(1), 15000L);
        openingTimes.put(diningHalls.get(2), 15000L);
        openingTimes.put(diningHalls.get(3), 15000L);
        openingTimes.put(diningHalls.get(4), 15000L);
        openingTimes.put(diningHalls.get(5), 15000L);
        openingTimes.put(diningHalls.get(6), 15000L);

        closingTimes.put(diningHalls.get(0), 8_100_000L);
        closingTimes.put(diningHalls.get(2), 8_100_000L);
        closingTimes.put(diningHalls.get(1), 9_900_000L);
        closingTimes.put(diningHalls.get(3), 11_700_000L);
        closingTimes.put(diningHalls.get(4), 11_700_000L);
        closingTimes.put(diningHalls.get(5), 11_700_000L);
        closingTimes.put(diningHalls.get(6), 11_700_000L);
    }


    public DiningHall getPopularDiningHall() {
        int totalPopularity = 0;
        for (DiningHall diningHall : this.diningHalls) {
            totalPopularity += diningHall.getPopularity();
        }
        Random rand = new Random();
        int randomNum = rand.nextInt(totalPopularity);
        int cumulativePopularity = 0;
        for (DiningHall diningHall : this.diningHalls) {
            cumulativePopularity += diningHall.getPopularity();
            if (randomNum < cumulativePopularity) {
                return diningHall;
            }
        }
        return null;
    }


    public static String convertTo12hrClock(long elapsedMillis) {
        int totalMinutesElapsed = (int) (elapsedMillis / 60000);
        int startMinutes = 645;
        int currentMinutes = startMinutes + totalMinutesElapsed;

        int hour24 = (currentMinutes / 60) % 24;
        int minute = currentMinutes % 60;

        int hour12 = hour24 % 12;
        if (hour12 == 0) hour12 = 12;

        String amPm = hour24 < 12 ? "AM" : "PM";

        return String.format("%02d:%02d %s", hour12, minute, amPm);
    }


    public static void printStatus(List<DiningHall> halls, String currentTime, long simTime) {
        StringBuilder sb = new StringBuilder();

        sb.append("\033[H\033[2J");
        sb.append("Dining Hall Dashboard\n");
        sb.append("----------------------\n");
        sb.append("Current Time: ").append(currentTime).append("\n\n");

        sb.append(String.format("%-12s | %-10s | %-11s | %s\n", "Hall", "Status", "Diners", "Wait Time"));
        sb.append("-------------|------------|-------------|-----------------\n");

        for (DiningHall dh : halls) {
            String status = dh.getStatus();
            if (!status.equals("closed")) {
                status = dh.updateStatus("open");
            }

            long wait = status.equals("closed") ? 0 : dh.getWaitTime(simTime);
            String formattedWait = formatMillis(wait);

            String line = String.format("%-12s | %-10s | %4d/%-5d  | %s\n",
                    dh.getName(),
                    status,
                    dh.getQueue().size(),
                    dh.getMaxSize(),
                    formattedWait);

            sb.append(line);
        }

        System.out.print(sb);
    }


    public static String formatMillis(long millis) {
        if (millis <= 0) return "0 sec";

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append(" hr ");
        if (minutes > 0) sb.append(minutes).append(" min ");
        if (seconds > 0 || sb.length() == 0) sb.append(seconds).append(" sec");

        return sb.toString().trim();
    }


    public void featureDemonstration() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Dish Dining Hall Dashboard ===");
            System.out.println("You are logged in as: " + this.currentUserName + " (ID: " + this.currentUser.getUserID() + ")");
            System.out.println("Choose an option:");
            System.out.println("1. View top dishes at each dining hall");
            System.out.println("2. Add or update a dish rating");
            System.out.println("3. Delete a dish rating");
            System.out.println("4. View all available dishes at each dining hall");
            System.out.println("5. Set your current dining hall");
            System.out.println("6. Add a friend");
            System.out.println("7. Check where your friends are");
            System.out.println("8. Exit and start dining hall simulation");

            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n=== Top Dishes by Dining Hall (Average Rating) ===");
                    for (DiningHall dh : this.diningHalls) {
                        System.out.println("\n" + dh.getName() + ":");
                        List<Map.Entry<String, Double>> topDishes = dh.topDishes(3, this.users);
                        if (topDishes.isEmpty()) {
                            System.out.println("  No ratings yet.");
                        } else {
                            for (Map.Entry<String, Double> entry : topDishes) {
                                System.out.printf("  %-20s  Avg: %.2f\n", entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    break;

                case "2":
                    System.out.println("\n=== Add/Edit Dish Rating ===");
                    while (true) {
                        System.out.print("Enter dining hall name: ");
                        String hall = scanner.nextLine().trim();
                        if (hall.isEmpty()) break;

                        boolean validHall = this.diningHalls.stream()
                                .anyMatch(dh -> dh.getName().equalsIgnoreCase(hall));
                        if (!validHall) {
                            System.out.println("Invalid dining hall. Try again.");
                            continue;
                        }

                        String dish = "";
                        while (true) {
                            System.out.print("Enter dish name: ");
                            dish = scanner.nextLine().trim();
                            if (dish.isEmpty()) break;
                            if (dish.length() < 2) {
                                System.out.println("Dish name too short.");
                                continue;
                            }

                            boolean exists = false;
                            for (User u : this.users.values()) {
                                Map<String, HashMap<String, Integer>> r = u.getRatings();
                                if (r.containsKey(hall.toLowerCase()) && r.get(hall.toLowerCase()).containsKey(dish)) {
                                    exists = true;
                                    break;
                                }
                            }

                            if (!exists) {
                                System.out.print("Dish not rated yet. Add anyway? (y/n): ");
                                if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                                    continue;
                                }
                            }
                            break;
                        }
                        if (dish.isEmpty()) break;

                        System.out.print("Enter rating (1-5): ");
                        String ratingInput = scanner.nextLine().trim();
                        if (ratingInput.isEmpty()) break;
                        try {
                            int rating = Integer.parseInt(ratingInput);
                            if (rating < 1 || rating > 5) {
                                System.out.println("Rating must be 1-5.");
                                continue;
                            }
                            this.currentUser.rate(hall, dish, rating);
                            this.friendSystem.setDiningHall(this.currentUserName, hall);
                            System.out.println("Rating saved.");
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number.");
                        }
                    }
                    break;

                case "3":
                    if (this.currentUser == null) {
                        System.out.println("No current user.");
                        break;
                    }
                    Map<String, HashMap<String, Integer>> allRatings = this.currentUser.getRatings();
                    if (allRatings.isEmpty()) {
                        System.out.println("No ratings yet.");
                        break;
                    }

                    for (Map.Entry<String, HashMap<String, Integer>> entry : allRatings.entrySet()) {
                        for (Map.Entry<String, Integer> d : entry.getValue().entrySet()) {
                            System.out.println("- " + entry.getKey() + ": " + d.getKey() + " → " + d.getValue());
                        }
                    }

                    System.out.print("Enter dining hall: ");
                    String hallToDelete = scanner.nextLine().trim().toLowerCase();
                    if (hallToDelete.isEmpty()) break;
                    System.out.print("Enter dish to delete: ");
                    String dishToDelete = scanner.nextLine().trim();
                    if (dishToDelete.isEmpty()) break;

                    if (allRatings.containsKey(hallToDelete)) {
                        allRatings.get(hallToDelete).remove(dishToDelete);
                        System.out.println("Rating removed.");
                    } else {
                        System.out.println("No such rating found.");
                    }
                    break;

                case "4":
                    System.out.println("\n=== Available Dishes ===");
                    for (DiningHall dh : this.diningHalls) {
                        Set<String> dishes = new HashSet<>();
                        String hallKey = dh.getName().toLowerCase();
                        for (User u : this.users.values()) {
                            if (u.getRatings().containsKey(hallKey)) {
                                dishes.addAll(u.getRatings().get(hallKey).keySet());
                            }
                        }
                        System.out.println(dh.getName() + ":");
                        if (dishes.isEmpty()) System.out.println("  No dishes rated yet.");
                        else dishes.forEach(d -> System.out.println("  - " + d));
                    }
                    break;

                 case "5":
                    System.out.println("\n=== Set Your Current Dining Hall ===");
                    System.out.print("Enter the dining hall you're currently at: ");
                    String hallSet = scanner.nextLine().trim();
                    if (hallSet.isEmpty()) break;

                    boolean valid = this.diningHalls.stream()
                            .anyMatch(dh -> dh.getName().equalsIgnoreCase(hallSet));
                    if (!valid) {
                        System.out.println("Invalid dining hall name.");
                        break;
                    }

                    this.friendSystem.setDiningHall(this.currentUserName, hallSet);
                    System.out.println(this.currentUserName + " is now marked as eating at " + hallSet);
                    break;

                case "6":
                    System.out.println("\n=== Add a Friend ===");
                    System.out.print("Enter your friend's name: ");
                    String friendName = scanner.nextLine().trim();
                    if (friendName.isEmpty()) break;

                    this.friendSystem.addUser(this.currentUserName);
                    this.friendSystem.addUser(friendName);
                    this.friendSystem.addFriendship(this.currentUserName, friendName);
                    System.out.println(friendName + " is now your friend.");
                    break;

                case "7":
                    System.out.println("\n=== Where Are Your Friends? ===");
                    Map<String, String> friends = this.friendSystem.getFriendsAtDiningHalls(this.currentUserName);
                    if (friends.isEmpty()) {
                        System.out.println("No friends are currently checked into dining halls.");
                    } else {
                        for (Map.Entry<String, String> entry : friends.entrySet()) {
                            System.out.println("- " + entry.getKey() + " is at " + entry.getValue());
                        }
                    }
                    break;

                case "8":
                    System.out.println("Goodbye, " + this.currentUserName + "!");
                    return;

                default:
                    System.out.println("Invalid input. Choose 1-8.");
            }
        }
    }


    public void runSimulation() {
        long[] simulatedTime = {0};
        int[] userCounter = {0};
        Set<DiningHall> started = new HashSet<>();

        ScheduledExecutorService enqueuer = Executors.newSingleThreadScheduledExecutor();
        enqueuer.scheduleAtFixedRate(() -> {
            for (DiningHall dh : diningHalls) {
                long openAt = openingTimes.getOrDefault(dh, Long.MAX_VALUE);
                long closeAt = closingTimes.getOrDefault(dh, Long.MAX_VALUE);

                // Always try to dequeue users every tick
                dh.dequeueUsers(simulatedTime[0]);

                // Mark as started when first opened
                if (!started.contains(dh) && simulatedTime[0] >= openAt) {
                    started.add(dh);
                }

                // Only accept new entries while open
                if (simulatedTime[0] >= openAt && simulatedTime[0] < closeAt) {
                    dh.updateStatus("open");
                } else {
                    dh.updateStatus("closed"); // closed for new users only
                }
            }

            // Enqueue 20 new users per tick
            for (int i = 0; i < 20; i++) {
                DiningHall hall = getPopularDiningHall();
                int tries = 10;
                while (hall.getStatus().equals("closed") && tries-- > 0) {
                    hall = getPopularDiningHall();
                }
                if (!hall.getStatus().equals("closed")) {
                    hall.enqueue(new User(userCounter[0]++), simulatedTime[0]);
                }
            }

            if (simulatedTime[0] >= 15_300_000L) {
                System.out.println("\nSimulation ended at 3:00 PM.");
                enqueuer.shutdown();
                return;
            }

            printStatus(diningHalls, convertTo12hrClock(simulatedTime[0]), simulatedTime[0]);
            simulatedTime[0] += 60000;

        }, 0, 100, TimeUnit.MILLISECONDS);
    }


    public static void main(String[] args) {
        Program program = new Program();

        // Load ratings from CSV
        String filePath = "C:\\Users\\tommy\\github-classroom\\pomonacs622025sp\\cs62Final\\id_dish_rating_diningHall.csv";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int userID = Integer.parseInt(parts[0]);
                String dish = parts[1].trim();
                int rating = Integer.parseInt(parts[2].trim());
                String diningHall = parts[3].trim().toLowerCase();

                program.users.putIfAbsent(userID, new User(userID));
                program.users.get(userID).rate(diningHall, dish, rating);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome! Please enter your name to start: ");
        String userName = scanner.nextLine().trim();
        while (userName.isEmpty()) {
            System.out.print("Name cannot be blank. Enter your name: ");
            userName = scanner.nextLine().trim();
        }

        int newId = program.users.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        User currentUser = new User(newId);
        program.users.put(newId, currentUser);
        program.currentUser = currentUser;
        program.currentUserName = userName;
        System.out.println("Assigned User ID: " + newId + " - Welcome, " + userName + "!");

        program.friendSystem.addUser(userName);

        String[] friends = {"Miles", "John", "Alice", "Bob"};
        String[] halls = {"Frary", "Collins", "Malott", "Hoch"};
        for (int i = 0; i < friends.length; i++) {
            program.friendSystem.addUser(friends[i]);
            program.friendSystem.setDiningHall(friends[i], halls[i]);
        }

        program.friendSystem.addFriendship(userName, "Miles");
        program.friendSystem.addFriendship(userName, "Alice");
        program.friendSystem.addFriendship("Miles", "John");
        program.friendSystem.addFriendship("Alice", "Bob");

        program.featureDemonstration();
        program.runSimulation();
    }
}