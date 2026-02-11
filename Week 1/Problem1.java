import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class UsernameChecker {

    private ConcurrentHashMap<String, Integer> userMap;
    private ConcurrentHashMap<String, AtomicInteger> attemptFrequency;

    public UsernameChecker() {
        userMap = new ConcurrentHashMap<>();
        attemptFrequency = new ConcurrentHashMap<>();

        userMap.put("john_doe", 101);
        userMap.put("admin", 1);
        userMap.put("root", 2);
    }

    public boolean checkAvailability(String username) {
        attemptFrequency
                .computeIfAbsent(username, k -> new AtomicInteger(0))
                .incrementAndGet();
        return !userMap.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            String alt = username + i;
            if (!userMap.containsKey(alt)) {
                suggestions.add(alt);
            }
        }

        String modified = username.replace("_", ".");
        if (!userMap.containsKey(modified)) {
            suggestions.add(modified);
        }

        return suggestions;
    }

    public String getMostAttempted() {
        String result = null;
        int maxAttempts = 0;

        for (Map.Entry<String, AtomicInteger> entry : attemptFrequency.entrySet()) {
            int count = entry.getValue().get();
            if (count > maxAttempts) {
                maxAttempts = count;
                result = entry.getKey();
            }
        }
        return result;
    }

    public boolean registerUser(String username, int userId) {
        if (checkAvailability(username)) {
            userMap.put(username, userId);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        UsernameChecker checker = new UsernameChecker();

        System.out.println(checker.checkAvailability("john_doe"));
        System.out.println(checker.checkAvailability("jane_smith"));

        System.out.println(checker.suggestAlternatives("john_doe"));

        for (int i = 0; i < 10543; i++) {
            checker.checkAvailability("admin");
        }

        System.out.println(checker.getMostAttempted());
    }
}
