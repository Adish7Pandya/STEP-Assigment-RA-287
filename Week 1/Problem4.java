import java.util.*;

class PlagiarismDetector {

    private Map<String, Set<String>> index = new HashMap<>();
    private int n = 5;

    public void addDocument(String docId, String text) {
        List<String> grams = ngrams(text);
        for (String g : grams)
            index.computeIfAbsent(g, k -> new HashSet<>()).add(docId);
    }

    public Map<String, Double> analyze(String docId, String text) {
        List<String> grams = ngrams(text);
        Map<String, Integer> count = new HashMap<>();
        for (String g : grams)
            if (index.containsKey(g))
                for (String d : index.get(g))
                    if (!d.equals(docId))
                        count.put(d, count.getOrDefault(d, 0) + 1);

        Map<String, Double> similarity = new HashMap<>();
        for (String d : count.keySet())
            similarity.put(d, (count.get(d) * 100.0) / grams.size());
        return similarity;
    }

    private List<String> ngrams(String text) {
        String[] words = text.split("\\s+");
        List<String> grams = new ArrayList<>();
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++)
                sb.append(words[i + j]).append(" ");
            grams.add(sb.toString().trim());
        }
        return grams;
    }

    public static void main(String[] args) {
        PlagiarismDetector p = new PlagiarismDetector();
        p.addDocument("essay_092", "this is a sample document for plagiarism detection system");
        System.out.println(p.analyze("essay_123", "this is a sample document for plagiarism detection"));
    }
}
