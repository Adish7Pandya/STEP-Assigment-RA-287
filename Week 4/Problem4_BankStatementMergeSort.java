import java.util.ArrayList;
import java.util.List;

/**
 * Problem 4: Bank Statement Merge Sort
 *
 * Scenario: A bank's reconciliation engine needs to merge and sort millions of
 * account statement entries by date (and amount as a tie-breaker) before
 * generating monthly PDF reports. The dataset is too large for O(n²) algorithms.
 *
 * Problem Statement:
 *   - Sort statement entries by transactionDate ASCENDING using Merge Sort.
 *   - Use amount as a secondary key when dates match.
 *   - Demonstrate the divide-and-conquer splitting and merging steps.
 *   - Show merge count and comparison count for performance analysis.
 *   - Detect chronological anomalies (entries dated in the future).
 *
 * Concepts Covered:
 *   - Merge Sort: divide-and-conquer, recursive splitting, ordered merging.
 *   - Time complexity: O(n log n) in all cases (best, average, worst).
 *   - Space complexity: O(n) auxiliary — temporary arrays during merge.
 *   - Stability: Merge Sort is inherently stable.
 *   - Practical use: Java's Arrays.sort() for objects uses a Merge Sort variant (TimSort).
 *
 * Use Cases:
 *   - Bank statement generation (Chase, Wells Fargo monthly reports).
 *   - Transaction ledger reconciliation.
 *   - SWIFT/ACH file processing pipelines.
 */
public class Problem4_BankStatementMergeSort {

    // ─── Data Model ──────────────────────────────────────────────────────────

    static class StatementEntry {
        String entryId;
        String transactionDate; // "YYYY-MM-DD" — lexicographic comparison works correctly
        double amount;          // positive = credit, negative = debit
        String description;

        StatementEntry(String entryId, String transactionDate, double amount, String description) {
            this.entryId = entryId;
            this.transactionDate = transactionDate;
            this.amount = amount;
            this.description = description;
        }

        @Override
        public String toString() {
            return String.format("%-6s | %s | amount=%+9.2f | %s",
                    entryId, transactionDate, amount, description);
        }
    }

    // ─── Counters (static so recursive calls can update them) ─────────────────

    private static int mergeCount = 0;
    private static int comparisonCount = 0;

    // ─── UC1: Merge Sort — ascending date, then amount ────────────────────────

    /**
     * Recursive Merge Sort entry point.
     * Sorts arr[left..right] in-place (using auxiliary space during merge).
     *
     * Time  : O(n log n) — all cases
     * Space : O(n)  auxiliary for temporary arrays
     * Stable: YES
     */
    public static void mergeSort(StatementEntry[] arr, int left, int right) {
        if (left >= right) return; // Base case: single element is already sorted

        int mid = left + (right - left) / 2; // Avoids integer overflow

        mergeSort(arr, left, mid);        // Sort left half
        mergeSort(arr, mid + 1, right);   // Sort right half
        merge(arr, left, mid, right);     // Merge sorted halves
    }

    /**
     * Merges arr[left..mid] and arr[mid+1..right] into sorted order.
     * Uses temporary arrays to hold the two halves before merging back.
     */
    private static void merge(StatementEntry[] arr, int left, int mid, int right) {
        mergeCount++;

        int leftSize  = mid - left + 1;
        int rightSize = right - mid;

        // Temporary arrays
        StatementEntry[] L = new StatementEntry[leftSize];
        StatementEntry[] R = new StatementEntry[rightSize];

        System.arraycopy(arr, left,      L, 0, leftSize);
        System.arraycopy(arr, mid + 1,   R, 0, rightSize);

        int i = 0, j = 0, k = left;

        while (i < leftSize && j < rightSize) {
            comparisonCount++;
            if (compare(L[i], R[j]) <= 0) {
                arr[k++] = L[i++]; // Take from left (≤ keeps stability)
            } else {
                arr[k++] = R[j++];
            }
        }

        // Copy any remaining elements
        while (i < leftSize)  arr[k++] = L[i++];
        while (j < rightSize) arr[k++] = R[j++];
    }

    /** Compare by date asc, then amount asc as tie-breaker. */
    private static int compare(StatementEntry a, StatementEntry b) {
        int dateCmp = a.transactionDate.compareTo(b.transactionDate);
        if (dateCmp != 0) return dateCmp;
        return Double.compare(a.amount, b.amount);
    }

    // ─── UC2: Chronological anomaly detection ────────────────────────────────

    /**
     * Scans the sorted array for entries whose date is after the report cutoff.
     * Assumes arr is already sorted ascending by date.
     */
    public static void detectFutureEntries(StatementEntry[] arr, String cutoffDate) {
        System.out.println("Anomaly check — entries dated after " + cutoffDate + ":");
        boolean found = false;
        for (StatementEntry e : arr) {
            if (e.transactionDate.compareTo(cutoffDate) > 0) {
                System.out.println("  *** FUTURE DATE *** " + e);
                found = true;
            }
        }
        if (!found) System.out.println("  None found.");
    }

    // ─── UC3: Running balance after sort ─────────────────────────────────────

    public static void printRunningBalance(StatementEntry[] arr, double openingBalance) {
        System.out.println("\nRunning balance (opening = $" + openingBalance + "):");
        double balance = openingBalance;
        for (StatementEntry e : arr) {
            balance += e.amount;
            System.out.printf("  %s → balance = $%.2f%n", e, balance);
        }
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private static StatementEntry[] copy(StatementEntry[] src) {
        StatementEntry[] c = new StatementEntry[src.length];
        for (int i = 0; i < src.length; i++) {
            c[i] = new StatementEntry(src[i].entryId, src[i].transactionDate,
                                      src[i].amount, src[i].description);
        }
        return c;
    }

    private static void print(StatementEntry[] arr, String label) {
        System.out.println(label);
        for (int i = 0; i < arr.length; i++) {
            System.out.printf("  [%2d] %s%n", i + 1, arr[i]);
        }
    }

    // ─── Main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        System.out.println("=== Problem 4: Bank Statement Merge Sort ===\n");

        StatementEntry[] entries = {
            new StatementEntry("E001", "2024-03-15", -250.00, "Grocery Store"),
            new StatementEntry("E002", "2024-03-01",  3000.00, "Salary Credit"),
            new StatementEntry("E003", "2024-03-20",  -89.99, "Streaming Service"),
            new StatementEntry("E004", "2024-03-10", -500.00, "Rent Payment"),
            new StatementEntry("E005", "2024-03-05",  -45.00, "Fuel"),
            new StatementEntry("E006", "2024-03-01",  200.00, "Freelance Income"),  // same date as E002
            new StatementEntry("E007", "2024-04-02",  -30.00, "Future Entry"),      // anomaly
            new StatementEntry("E008", "2024-03-25", -120.00, "Electricity Bill"),
            new StatementEntry("E009", "2024-03-12",  750.00, "Tax Refund"),
            new StatementEntry("E010", "2024-03-18",  -60.00, "Dining"),
            new StatementEntry("E011", "2024-03-01",  200.00, "Bonus"),             // same date+amount as E006
            new StatementEntry("E012", "2024-03-28",  -15.00, "ATM Fee"),
        };

        System.out.println("Input entries (unsorted):");
        for (StatementEntry e : entries) System.out.println("  " + e);
        System.out.println();

        // Reset counters
        mergeCount = 0;
        comparisonCount = 0;

        // UC1: Merge Sort
        StatementEntry[] sorted = copy(entries);
        mergeSort(sorted, 0, sorted.length - 1);

        System.out.println("--- UC1: Merge Sort Result ---");
        System.out.println("Merge operations : " + mergeCount);
        System.out.println("Comparisons      : " + comparisonCount
                + "  (expected ≈ n·log₂n = " + entries.length + "·"
                + (int)(Math.log(entries.length) / Math.log(2)) + " ≈ "
                + (int)(entries.length * Math.log(entries.length) / Math.log(2)) + ")");
        print(sorted, "Sorted by date asc + amount asc:");

        // Stability check: E002, E006, E011 all on 2024-03-01
        System.out.println("\nStability check — entries on 2024-03-01:");
        for (StatementEntry e : sorted) {
            if (e.transactionDate.equals("2024-03-01")) {
                System.out.println("  " + e);
            }
        }
        System.out.println("  → Relative order of equal-date entries preserved (stable).");

        // UC2: Anomaly detection
        System.out.println("\n--- UC2: Chronological Anomaly Detection ---");
        detectFutureEntries(sorted, "2024-03-31");

        // UC3: Running balance
        System.out.println("\n--- UC3: Running Balance ---");
        printRunningBalance(sorted, 5000.00);

        // Complexity summary
        System.out.println("\n--- Complexity Summary ---");
        System.out.println("Merge Sort | Best: O(n log n) | Avg: O(n log n) | Worst: O(n log n)");
        System.out.println("Space      : O(n) auxiliary  |  Stable: YES");
        System.out.println("Trade-off  : Uses more memory than Bubble/Insertion, but dramatically");
        System.out.println("             faster for large n (e.g., n=1,000,000: ~20M ops vs ~1T ops).");
    }
}
