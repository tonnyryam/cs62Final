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
        for (DiningHall diningHall : diningHalls) {
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


    public void dishDiningHallViewer() {
        Scanner scanner = new Scanner(System.in);
        User currentUser = null;  // tracks the current session user

        System.out.println("=== Dish Ratings & Dining Hall Viewer ===");

        boolean exit = false;
        while (!exit) {
            System.out.println("\nChoose an option:");
            System.out.println("1. View top dishes at each dining hall");
            System.out.println("2. Add or update a dish rating");
            System.out.println("3. Delete a dish rating");
            System.out.println("4. View all available dishes at each dining hall");
            System.out.println("5. Start simulation");
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
                    System.out.println("\n=== Start Session / Add/Edit Dish Ratings ===");
                    System.out.println("Press Enter at any time to return to the main menu.");

                    System.out.print("Enter your name (or just press Enter to cancel): ");
                    String userName = scanner.nextLine().trim();
                    if (userName.isEmpty()) break;

                    int newId = this.users.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
                    currentUser = new User(newId);
                    this.users.put(newId, currentUser);
                    System.out.println("Assigned User ID: " + newId + " — Welcome, " + userName + "!");

                    while (true) {
                        System.out.print("Enter dining hall name: ");
                        String hall = scanner.nextLine().trim();
                        if (hall.isEmpty()) break;

                        // Validate dining hall
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
                            System.out.println("Dish name is too short. Try again.");
                            continue;
                        }

                        // Check if dish already rated for this hall
                        boolean dishExists = false;
                        String hallKey = hall.toLowerCase();
                        for (User u : this.users.values()) {
                            Map<String, HashMap<String, Integer>> ratings = u.getRatings();
                            if (ratings.containsKey(hallKey) && ratings.get(hallKey).containsKey(dish)) {
                                dishExists = true;
                                break;
                            }
                        }

                        if (!dishExists) {
                            System.out.print("This dish hasn't been rated yet. Add it anyway? (y/n): ");
                            String confirm = scanner.nextLine().trim();
                            if (!confirm.equalsIgnoreCase("y")) {
                                System.out.println("Okay, enter a different dish.");
                                continue;
                            }
                        }
                        break; // valid dish name confirmed
                    }

                    if (dish.isEmpty()) break;

                    System.out.print("Enter rating (1–5): ");
                    String ratingInput = scanner.nextLine().trim();
                    if (ratingInput.isEmpty()) break;

                    try {
                        int rating = Integer.parseInt(ratingInput);
                        if (rating < 1 || rating > 5) {
                            System.out.println("Rating must be between 1 and 5. Try again.");
                            continue;
                        }

                        currentUser.rate(hall, dish, rating);
                        System.out.println("✅ Rating saved.\n");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number. Try again.");
                    }
                }
                    break;

                case "3":
                    if (currentUser == null) {
                        System.out.println("No active user. Please select option 2 first to start a session.");
                        break;
                    }

                    System.out.println("\n=== View or Delete Ratings for User ID: " + currentUser.getUserID() + " ===");

                    Map<String, HashMap<String, Integer>> allRatings = currentUser.getRatings();
                    if (allRatings.isEmpty()) {
                        System.out.println("No ratings to show.");
                        break;
                    }

                    System.out.println("Your current ratings:");
                    for (Map.Entry<String, HashMap<String, Integer>> entry : allRatings.entrySet()) {
                        String hallName = entry.getKey();
                        for (Map.Entry<String, Integer> dish : entry.getValue().entrySet()) {
                            System.out.println("- " + hallName + ": " + dish.getKey() + " → " + dish.getValue());
                        }
                    }

                    System.out.print("Enter dining hall name of the dish to delete (or press Enter): ");
                    String hallToDelete = scanner.nextLine().trim();
                    if (hallToDelete.isEmpty()) break;

                    System.out.print("Enter dish name to delete (or press Enter): ");
                    String dishToDelete = scanner.nextLine().trim();
                    if (dishToDelete.isEmpty()) break;

                    Map<String, Integer> hallMap = allRatings.get(hallToDelete.toLowerCase());
                    if (hallMap != null && hallMap.containsKey(dishToDelete)) {
                        hallMap.remove(dishToDelete);
                        System.out.println("Rating deleted.");
                    } else {
                        System.out.println("No rating found for that dish.");
                    }
                    break;

                case "4":
                    System.out.println("\n=== All Rated Dishes by Dining Hall (with Averages) ===");

                    for (DiningHall dh : this.diningHalls) {
                        String hallKey = dh.getName().toLowerCase().trim();
                        Map<String, Integer> totalRatings = new HashMap<>();
                        Map<String, Integer> counts = new HashMap<>();

                        for (User user : this.users.values()) {
                            Map<String, HashMap<String, Integer>> ratings = user.getRatings();
                            if (ratings.containsKey(hallKey)) {
                                for (Map.Entry<String, Integer> entry : ratings.get(hallKey).entrySet()) {
                                    String dish = entry.getKey();
                                    int rating = entry.getValue();
                                    totalRatings.put(dish, totalRatings.getOrDefault(dish, 0) + rating);
                                    counts.put(dish, counts.getOrDefault(dish, 0) + 1);
                                }
                            }
                        }

                        System.out.println("\n" + dh.getName() + ":");
                        if (totalRatings.isEmpty()) {
                            System.out.println("  No dishes rated yet.");
                        } else {
                            for (String dish : totalRatings.keySet()) {
                                double avg = (double) totalRatings.get(dish) / counts.get(dish);
                                System.out.printf("  %-20s  Avg: %.2f\n", dish, avg);
                            }
                        }
                    }
                    break;

                case "5":
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid choice.");
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
        String filePath = "C:\\Users\\tommy\\github-classroom\\pomonacs622025sp\\cs62Final\\id_dish_rating_diningHall.csv";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); 
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int userID = Integer.parseInt(parts[0]);
                String dish = parts[1].trim();
                int rating = Integer.parseInt(parts[2].trim());
                String diningHall = parts[3].trim().toLowerCase(); // normalize to lowercase

                program.users.putIfAbsent(userID, new User(userID));
                program.users.get(userID).rate(diningHall, dish, rating);
            }
        }
        catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        program.dishDiningHallViewer();
        program.runSimulation();
    }
}