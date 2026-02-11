import java.util.*;

class AnalyticsDashboard {

    private Map<String, Integer> pageViews = new HashMap<>();
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();
    private Map<String, Integer> sources = new HashMap<>();

    public void processEvent(String url, String userId, String source) {
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);
        uniqueVisitors.computeIfAbsent(url, k -> new HashSet<>()).add(userId);
        sources.put(source, sources.getOrDefault(source, 0) + 1);
    }

    public List<String> getTopPages() {
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());
        pq.addAll(pageViews.entrySet());
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 10 && !pq.isEmpty(); i++) {
            Map.Entry<String, Integer> e = pq.poll();
            result.add(e.getKey() + " - " + e.getValue() +
                    " views (" + uniqueVisitors.get(e.getKey()).size() + " unique)");
        }
        return result;
    }

    public static void main(String[] args) {
        AnalyticsDashboard a = new AnalyticsDashboard();
        a.processEvent("/article/breaking-news", "user1", "google");
        a.processEvent("/article/breaking-news", "user2", "facebook");
        System.out.println(a.getTopPages());
    }
}
