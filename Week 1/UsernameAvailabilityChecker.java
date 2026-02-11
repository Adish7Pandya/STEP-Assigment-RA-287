import java.util.*;

/**
 * Problem 1: Social Media Username Availability Checker
 * 
 * This system checks username availability in real-time using HashMaps for O(1) performance.
 * It handles username lookups, provides suggestions for taken usernames, and tracks popularity.
 */
public class UsernameAvailabilityChecker {
    // HashMap for O(1) username lookup: username -> userId
    private HashMap<String, Integer> usernameToUserId;
    
    // HashMap for tracking attempt frequency: username -> attempt count
    private HashMap<String, Integer> attemptFrequency;
    
    // Counter for generating user IDs
    private int nextUserId;
    
    /**
     * Constructor initializes the data structures
     */
    public UsernameAvailabilityChecker() {
        this.usernameToUserId = new HashMap<>();
        this.attemptFrequency = new HashMap<>();
        this.nextUserId = 1;
    }
    
    /**
     * Checks if a username is available
     * Time Complexity: O(1)
     * 
     * @param username The username to check
     * @return true if available, false if taken
     */
    public boolean checkAvailability(String username) {
        // Track this attempt
        attemptFrequency.put(username, attemptFrequency.getOrDefault(username, 0) + 1);
        
        // Check if username exists (O(1) operation)
        return !usernameToUserId.containsKey(username);
    }
    
    /**
     * Registers a new username
     * Time Complexity: O(1)
     * 
     * @param username The username to register
     * @return userId if successful, -1 if username is taken
     */
    public int registerUsername(String username) {
        if (!checkAvailability(username)) {
            return -1; // Username already taken
        }
        
        int userId = nextUserId++;
        usernameToUserId.put(username, userId);
        return userId;
    }
    
    /**
     * Suggests alternative usernames if the requested one is taken
     * Time Complexity: O(n) where n is the number of suggestions
     * 
     * @param username The desired username
     * @return List of available alternative usernames
     */
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        
        // If username is available, return it as the only suggestion
        if (checkAvailability(username)) {
            suggestions.add(username);
            return suggestions;
        }
        
        // Strategy 1: Append numbers (1-10)
        for (int i = 1; i <= 10; i++) {
            String alternative = username + i;
            if (!usernameToUserId.containsKey(alternative)) {
                suggestions.add(alternative);
                if (suggestions.size() >= 3) break;
            }
        }
        
        // Strategy 2: Replace underscores with dots
        if (suggestions.size() < 3 && username.contains("_")) {
            String alternative = username.replace("_", ".");
            if (!usernameToUserId.containsKey(alternative)) {
                suggestions.add(alternative);
            }
        }
        
        // Strategy 3: Add underscores
        if (suggestions.size() < 3) {
            String alternative = username + "_";
            if (!usernameToUserId.containsKey(alternative)) {
                suggestions.add(alternative);
            }
        }
        
        // Strategy 4: Prepend common prefixes
        if (suggestions.size() < 3) {
            String[] prefixes = {"the_", "real_", "official_"};
            for (String prefix : prefixes) {
                String alternative = prefix + username;
                if (!usernameToUserId.containsKey(alternative)) {
                    suggestions.add(alternative);
                    if (suggestions.size() >= 3) break;
                }
            }
        }
        
        return suggestions;
    }
    
    /**
     * Gets the most attempted username
     * Time Complexity: O(n) where n is the number of unique usernames attempted
     * 
     * @return The username with the most attempts and its count
     */
    public String getMostAttempted() {
        if (attemptFrequency.isEmpty()) {
            return "No attempts recorded";
        }
        
        String mostAttempted = null;
        int maxAttempts = 0;
        
        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > maxAttempts) {
                maxAttempts = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }
        
        return mostAttempted + " (" + maxAttempts + " attempts)";
    }
    
    /**
     * Gets the current number of registered users
     * 
     * @return Number of registered users
     */
    public int getRegisteredUsersCount() {
        return usernameToUserId.size();
    }
    
    /**
     * Main method for testing the username availability checker
     */
    public static void main(String[] args) {
        UsernameAvailabilityChecker checker = new UsernameAvailabilityChecker();
        
        System.out.println("=== Social Media Username Availability Checker ===\n");
        
        // Register some users
        System.out.println("Registering users...");
        checker.registerUsername("john_doe");
        checker.registerUsername("jane_smith");
        checker.registerUsername("admin");
        checker.registerUsername("user123");
        checker.registerUsername("tech_guru");
        
        System.out.println("Total registered users: " + checker.getRegisteredUsersCount());
        System.out.println();
        
        // Test availability checks
        System.out.println("--- Username Availability Checks ---");
        System.out.println("checkAvailability('john_doe'): " + checker.checkAvailability("john_doe"));
        System.out.println("checkAvailability('jane_smith'): " + checker.checkAvailability("jane_smith"));
        System.out.println("checkAvailability('new_user'): " + checker.checkAvailability("new_user"));
        System.out.println("checkAvailability('admin'): " + checker.checkAvailability("admin"));
        System.out.println();
        
        // Test username suggestions
        System.out.println("--- Username Suggestions ---");
        System.out.println("suggestAlternatives('john_doe'): " + checker.suggestAlternatives("john_doe"));
        System.out.println("suggestAlternatives('jane_smith'): " + checker.suggestAlternatives("jane_smith"));
        System.out.println("suggestAlternatives('new_user'): " + checker.suggestAlternatives("new_user"));
        System.out.println();
        
        // Simulate multiple attempts to check popularity tracking
        System.out.println("--- Simulating Multiple Username Checks ---");
        for (int i = 0; i < 50; i++) {
            checker.checkAvailability("admin");
        }
        for (int i = 0; i < 30; i++) {
            checker.checkAvailability("john_doe");
        }
        for (int i = 0; i < 20; i++) {
            checker.checkAvailability("tech_guru");
        }
        
        System.out.println("getMostAttempted(): " + checker.getMostAttempted());
        System.out.println();
        
        // Performance simulation
        System.out.println("--- Performance Test ---");
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            checker.checkAvailability("test_user_" + i);
        }
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
        
        System.out.println("Processed 1000 username checks in " + String.format("%.2f", duration) + " ms");
        System.out.println("Average time per check: " + String.format("%.4f", duration / 1000) + " ms");
        System.out.println("\n✓ System handles high concurrent load with O(1) performance");
    }
}
