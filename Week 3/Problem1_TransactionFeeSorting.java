import java.util.ArrayList;
import java.util.List;

/**
 * Problem 1: Transaction Fee Sorting for Audit Compliance
 *
 * Scenario: A banking application processes daily transactions.
 * Auditors require transactions sorted by fee amount for compliance reviews.
 *
 * Concepts Covered:
 *   - Bubble Sort: adjacent swaps, early termination, stability
 *   - Insertion Sort: building sorted subarray, shift operations
 *   - Time complexity: O(n²) analysis, best/worst cases
 *   - Stability in sorting
 *   - High-fee outlier detection (> $50)
 */
public class Problem1_TransactionFeeSorting {

    // ─── Data Model ──────────────────────────────────────────────────────────

    static class Transaction {
        String id;
        double fee;
        String timestamp;

        Transaction(String id, double fee, String timestamp) {
            this.id = id;
            this.fee = fee;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return id + ":fee=" + fee + "@" + timestamp;
        }
    }

    // ─── UC1: Bubble Sort by fee (ascending) — for batches ≤ 100 ─────────────

    /**
     * Stable Bubble Sort on fee (ascending).
     * Adjacent elements are swapped only when strictly out of order, preserving
     * the relative order of equal-fee transactions (stability guaranteed).
     *
     * Time Complexity:
     *   Best  : O(n)  — already sorted (early-termination flag)
     *   Worst : O(n²) — reverse sorted
     * Space : O(1) in-place
     */
    public static void bubbleSortByFee(List<Transaction> list) {
        int n = list.size();
        int totalPasses = 0;
        int totalSwaps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            totalPasses++;

            for (int j = 0; j < n - 1 - i; j++) {
                if (list.get(j).fee > list.get(j + 1).fee) {   // strict > keeps stability
                    Transaction temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swapped = true;
                    totalSwaps++;
                }
            }

            if (!swapped) break; // Early termination: already sorted
        }

        System.out.println("BubbleSort passes: " + totalPasses + ", swaps: " + totalSwaps);
    }

    // ─── UC2: Insertion Sort by (fee + timestamp) — for batches 100-1000 ─────

    /**
     * Stable Insertion Sort on fee, with timestamp as tie-breaker.
     *
     * Time Complexity:
     *   Best  : O(n)  — nearly sorted input
     *   Worst : O(n²) — reverse sorted
     * Space : O(1) in-place
     */
    public static void insertionSortByFeeAndTimestamp(List<Transaction> list) {
        int n = list.size();
        int totalShifts = 0;

        for (int i = 1; i < n; i++) {
            Transaction key = list.get(i);
            int j = i - 1;

            // Compare by fee first; use timestamp as tie-breaker
            while (j >= 0 && compareByFeeAndTimestamp(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
                totalShifts++;
            }
            list.set(j + 1, key);
        }

        System.out.println("InsertionSort shifts: " + totalShifts);
    }

    /** Returns positive if a should come AFTER b (i.e., a > b in sort order). */
    private static int compareByFeeAndTimestamp(Transaction a, Transaction b) {
        if (Double.compare(a.fee, b.fee) != 0) {
            return Double.compare(a.fee, b.fee);
        }
        return a.timestamp.compareTo(b.timestamp);
    }

    // ─── UC3: Flag high-fee outliers (> $50) ────────────────────────────────

    public static void flagHighFeeOutliers(List<Transaction> list) {
        List<Transaction> outliers = new ArrayList<>();
        for (Transaction t : list) {
            if (t.fee > 50.0) {
                outliers.add(t);
            }
        }

        if (outliers.isEmpty()) {
            System.out.println("High-fee outliers (>$50): none");
        } else {
            System.out.println("High-fee outliers (>$50):");
            for (Transaction t : outliers) {
                System.out.println("  *** HIGH FEE *** " + t);
            }
        }
    }

    // ─── Helper: deep-copy list ──────────────────────────────────────────────

    private static List<Transaction> copyList(List<Transaction> original) {
        List<Transaction> copy = new ArrayList<>();
        for (Transaction t : original) {
            copy.add(new Transaction(t.id, t.fee, t.timestamp));
        }
        return copy;
    }

    // ─── Main ────────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        System.out.println("=== Problem 1: Transaction Fee Sorting for Audit Compliance ===\n");

        // ── Sample dataset ──
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("id1", 10.5,  "10:00"));
        transactions.add(new Transaction("id2", 25.0,  "09:30"));
        transactions.add(new Transaction("id3",  5.0,  "10:15"));
        transactions.add(new Transaction("id4", 25.0,  "08:45")); // duplicate fee — tests stability
        transactions.add(new Transaction("id5", 75.0,  "11:00")); // high-fee outlier
        transactions.add(new Transaction("id6", 15.0,  "09:00"));

        System.out.println("Input transactions:");
        for (Transaction t : transactions) {
            System.out.println("  " + t);
        }
        System.out.println();

        // ── UC1: Bubble Sort (small batch ≤ 100) ──
        List<Transaction> bubbleList = copyList(transactions);
        System.out.println("--- UC1: Bubble Sort by fee (ascending) ---");
        bubbleSortByFee(bubbleList);
        System.out.print("Result: [");
        for (int i = 0; i < bubbleList.size(); i++) {
            System.out.print(bubbleList.get(i).id + ":" + bubbleList.get(i).fee);
            if (i < bubbleList.size() - 1) System.out.print(", ");
        }
        System.out.println("]");
        System.out.println("Stability check — id2 and id4 both fee=25.0; id2 (ts=09:30) appears before id4 (ts=08:45)? "
                + (bubbleList.indexOf(bubbleList.stream().filter(t -> t.id.equals("id2")).findFirst().get())
                <  bubbleList.indexOf(bubbleList.stream().filter(t -> t.id.equals("id4")).findFirst().get())));
        System.out.println();

        // ── UC2: Insertion Sort by (fee + timestamp) ──
        List<Transaction> insertList = copyList(transactions);
        System.out.println("--- UC2: Insertion Sort by fee + timestamp ---");
        insertionSortByFeeAndTimestamp(insertList);
        System.out.print("Result: [");
        for (int i = 0; i < insertList.size(); i++) {
            System.out.print(insertList.get(i).id + ":" + insertList.get(i).fee + "@" + insertList.get(i).timestamp);
            if (i < insertList.size() - 1) System.out.print(", ");
        }
        System.out.println("]");
        System.out.println();

        // ── UC3: Flag high-fee outliers ──
        System.out.println("--- UC3: High-fee outlier detection ---");
        flagHighFeeOutliers(bubbleList);
        System.out.println();

        // ── Time-complexity summary ──
        System.out.println("--- Time Complexity Summary ---");
        System.out.println("Bubble Sort  | Best: O(n) [early exit] | Worst: O(n²) | Space: O(1) | Stable: YES");
        System.out.println("Insertion Srt| Best: O(n) [nearly srtd] | Worst: O(n²) | Space: O(1) | Stable: YES");

        // ── Use-case note ──
        System.out.println("\n--- Use-Case Note ---");
        System.out.println("Batch <= 100       → Bubble Sort  (simple, good for small compliance batches)");
        System.out.println("Batch 100-1000     → Insertion Sort (adaptive, excels on nearly-sorted data)");
        System.out.println("Batch > 1000       → Upgrade to Merge Sort / TimSort for O(n log n)");
    }
}
