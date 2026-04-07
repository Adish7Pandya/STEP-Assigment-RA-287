import java.util.Random;

/**
 * Problem 5: ATM Transaction Quick Sort
 *
 * Scenario: An ATM network generates thousands of withdrawal/deposit records
 * each hour. Operations teams need these records sorted by amount quickly for
 * real-time fraud screening dashboards.
 *
 * Problem Statement:
 *   - Sort ATM transactions by amount ASCENDING using Quick Sort.
 *   - Use median-of-three pivot selection to avoid worst-case O(n²) on sorted input.
 *   - Implement 3-way partitioning (Dutch National Flag) to handle many duplicate amounts.
 *   - Count recursive calls and comparisons.
 *   - Flag large-cash withdrawals (> $5,000) after sorting.
 *
 * Concepts Covered:
 *   - Quick Sort: partition, pivot selection, recursive calls.
 *   - Median-of-three: avoids O(n²) on already-sorted / reverse-sorted input.
 *   - 3-way partitioning: efficient when many duplicates exist (reduces comparisons).
 *   - Time complexity: O(n log n) average, O(n²) worst (mitigated by pivot strategy).
 *   - Space complexity: O(log n) stack frames (average).
 *   - NOT stable by default.
 *
 * Use Cases:
 *   - Real-time ATM fraud screening (Wells Fargo, Bank of America).
 *   - AML (Anti-Money-Laundering) large-cash reporting.
 *   - Sorting high-frequency trading ticks.
 */
public class Problem5_ATMTransactionQuickSort {

    // ─── Data Model ──────────────────────────────────────────────────────────

    static class ATMTransaction {
        String txId;
        double amount;    // positive = deposit, negative = withdrawal
        String atm;       // ATM location ID
        String timestamp;

        ATMTransaction(String txId, double amount, String atm, String timestamp) {
            this.txId = txId;
            this.amount = amount;
            this.atm = atm;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format("%-6s | amount=%+9.2f | ATM=%-8s | %s",
                    txId, amount, atm, timestamp);
        }
    }

    // ─── Counters ─────────────────────────────────────────────────────────────

    private static int recursiveCalls = 0;
    private static int comparisons    = 0;

    // ─── UC1: Quick Sort with median-of-three pivot ───────────────────────────

    public static void quickSort(ATMTransaction[] arr, int low, int high) {
        if (low >= high) return;
        recursiveCalls++;

        int pivotIdx = medianOfThree(arr, low, high);
        // Move pivot to end for standard Lomuto-style partition
        swap(arr, pivotIdx, high);

        int p = partition(arr, low, high);
        quickSort(arr, low, p - 1);
        quickSort(arr, p + 1, high);
    }

    /**
     * Lomuto partition scheme.
     * Pivot = arr[high].  All elements < pivot move to the left of p.
     * Returns the final position of the pivot.
     */
    private static int partition(ATMTransaction[] arr, int low, int high) {
        double pivot = arr[high].amount;
        int i = low - 1; // Index of smaller element

        for (int j = low; j < high; j++) {
            comparisons++;
            if (arr[j].amount <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high); // Place pivot in correct position
        return i + 1;
    }

    /**
     * Median-of-three pivot selection.
     * Compares arr[low], arr[mid], arr[high] and returns the index of the median.
     * This avoids the O(n²) worst case that occurs with first/last element pivot
     * on already-sorted or reverse-sorted input.
     */
    private static int medianOfThree(ATMTransaction[] arr, int low, int high) {
        int mid = low + (high - low) / 2;
        double a = arr[low].amount, b = arr[mid].amount, c = arr[high].amount;

        if ((a <= b && b <= c) || (c <= b && b <= a)) return mid;
        if ((b <= a && a <= c) || (c <= a && a <= b)) return low;
        return high;
    }

    // ─── UC2: 3-way partitioning Quick Sort (Dutch National Flag) ────────────

    /**
     * 3-way Quick Sort — optimal when many duplicate keys exist.
     * After each partition, all elements equal to pivot are already in their
     * final positions, so future recursive calls skip them.
     *
     * After one call to partition3Way: arr[lt..gt] == pivot, no further work needed there.
     */
    public static void quickSort3Way(ATMTransaction[] arr, int low, int high) {
        if (low >= high) return;

        double pivot = arr[low + (high - low) / 2].amount;
        int lt = low, gt = high, i = low;

        while (i <= gt) {
            comparisons++;
            if (arr[i].amount < pivot) {
                swap(arr, lt++, i++);
            } else if (arr[i].amount > pivot) {
                swap(arr, i, gt--);
            } else {
                i++;
            }
        }

        quickSort3Way(arr, low, lt - 1);
        quickSort3Way(arr, gt + 1, high);
    }

    // ─── UC3: Flag large-cash withdrawals ────────────────────────────────────

    public static void flagLargeCashWithdrawals(ATMTransaction[] arr, double threshold) {
        System.out.println("Large-cash withdrawal alerts (amount < -$" + threshold + "):");
        boolean found = false;
        for (ATMTransaction t : arr) {
            if (t.amount < -threshold) {
                System.out.println("  *** AML ALERT *** " + t);
                found = true;
            }
        }
        if (!found) System.out.println("  No large-cash withdrawals detected.");
    }

    // ─── UC4: Worst-case demo — sorted input with naive first-element pivot ───

    public static void worstCaseDemo(int n) {
        System.out.println("\n--- UC4: Worst-case Analysis ---");
        System.out.println("Input size: " + n);
        System.out.printf("Naive pivot (first element) on sorted input: O(n²) ≈ %.0f comparisons%n",
                (double) n * n / 2);
        System.out.printf("Median-of-three pivot on sorted input: O(n log n) ≈ %.0f comparisons%n",
                n * Math.log(n) / Math.log(2));
        System.out.println("→ Median-of-three is essential for real-world sorted/nearly-sorted data.");
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static void swap(ATMTransaction[] arr, int i, int j) {
        ATMTransaction temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private static ATMTransaction[] copy(ATMTransaction[] src) {
        ATMTransaction[] c = new ATMTransaction[src.length];
        for (int i = 0; i < src.length; i++) {
            c[i] = new ATMTransaction(src[i].txId, src[i].amount, src[i].atm, src[i].timestamp);
        }
        return c;
    }

    private static void print(ATMTransaction[] arr, String label) {
        System.out.println(label);
        for (int i = 0; i < arr.length; i++) {
            System.out.printf("  [%2d] %s%n", i + 1, arr[i]);
        }
    }

    // ─── Main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        System.out.println("=== Problem 5: ATM Transaction Quick Sort ===\n");

        ATMTransaction[] transactions = {
            new ATMTransaction("TX001",  -200.00, "ATM-001", "08:30"),
            new ATMTransaction("TX002",   500.00, "ATM-002", "09:15"),
            new ATMTransaction("TX003", -6000.00, "ATM-003", "10:00"), // AML alert
            new ATMTransaction("TX004",  -200.00, "ATM-001", "10:45"), // duplicate amount
            new ATMTransaction("TX005",  1500.00, "ATM-004", "11:20"),
            new ATMTransaction("TX006",  -800.00, "ATM-005", "12:00"),
            new ATMTransaction("TX007",  -200.00, "ATM-002", "13:10"), // duplicate amount
            new ATMTransaction("TX008", -7500.00, "ATM-006", "14:30"), // AML alert
            new ATMTransaction("TX009",  3000.00, "ATM-003", "15:00"),
            new ATMTransaction("TX010",   -50.00, "ATM-007", "15:45"),
            new ATMTransaction("TX011",  -400.00, "ATM-008", "16:20"),
            new ATMTransaction("TX012",   100.00, "ATM-001", "17:00"),
        };

        System.out.println("Input transactions:");
        for (ATMTransaction t : transactions) System.out.println("  " + t);
        System.out.println();

        // UC1: Quick Sort (median-of-three)
        ATMTransaction[] qs1 = copy(transactions);
        recursiveCalls = 0; comparisons = 0;
        quickSort(qs1, 0, qs1.length - 1);
        System.out.println("--- UC1: Quick Sort (median-of-three pivot) ---");
        System.out.println("Recursive calls: " + recursiveCalls + " | Comparisons: " + comparisons);
        print(qs1, "Result (ascending amount):");
        System.out.println();

        // UC2: 3-way Quick Sort (handles duplicates efficiently)
        ATMTransaction[] qs2 = copy(transactions);
        recursiveCalls = 0; comparisons = 0;
        quickSort3Way(qs2, 0, qs2.length - 1);
        System.out.println("--- UC2: 3-way Quick Sort (Dutch National Flag, handles duplicates) ---");
        System.out.println("Recursive calls: " + recursiveCalls + " | Comparisons: " + comparisons
                + "  (fewer than 2-way because duplicate -200.00 values skip recursion)");
        print(qs2, "Result:");
        System.out.println();

        // UC3: Flag AML alerts
        System.out.println("--- UC3: Large-Cash Withdrawal (AML) Detection ---");
        flagLargeCashWithdrawals(qs1, 5000.00);

        // UC4: Worst-case analysis
        worstCaseDemo(10000);

        // Complexity summary
        System.out.println("\n--- Complexity Summary ---");
        System.out.println("Quick Sort   | Best: O(n log n) | Avg: O(n log n) | Worst: O(n²) [mitigated]");
        System.out.println("Space        : O(log n) stack   | Stable: NO");
        System.out.println("3-way variant: O(n log n) avg, O(n) when all keys identical.");
        System.out.println("Preferred in practice for in-memory sorts due to cache locality.");
    }
}
