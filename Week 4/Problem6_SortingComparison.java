import java.util.ArrayList;
import java.util.List;

/**
 * Problem 6: Sorting Algorithm Performance Comparison
 *
 * Scenario: The engineering team at a fintech firm is benchmarking five sorting
 * algorithms across three real-world dataset profiles to select the best algorithm
 * for each operational context.
 *
 * Problem Statement:
 *   - Implement Bubble Sort, Insertion Sort, Selection Sort, Merge Sort, and Quick Sort.
 *   - Run each algorithm on four data profiles:
 *       (a) Random order   — typical daily transaction batch
 *       (b) Nearly sorted  — incremental intraday feed (95% sorted)
 *       (c) Reverse sorted — stress test
 *       (d) Many duplicates — fee-code data with limited distinct values
 *   - Measure comparisons, swaps/shifts, and wall-clock time for each.
 *   - Print a summary table showing which algorithm wins each profile.
 *   - Recommend the best algorithm per use-case with justification.
 *
 * Concepts Covered:
 *   - Time complexity: O(n²) vs O(n log n) with empirical evidence.
 *   - Best-case behaviour: Bubble Sort and Insertion Sort shine on sorted data.
 *   - Stability comparison across algorithms.
 *   - Practical trade-offs: in-place vs auxiliary space.
 *   - Algorithm selection guide for banking workloads.
 *
 * Use Cases:
 *   - Algorithm selection for trade settlement engines.
 *   - Regulatory reporting pipeline optimisation.
 *   - Database index rebuild strategy.
 */
public class Problem6_SortingComparison {

    // ─── Metrics container ────────────────────────────────────────────────────

    static class Metrics {
        String algorithmName;
        long comparisons;
        long swapsOrShifts;
        long elapsedNs;

        Metrics(String algorithmName) {
            this.algorithmName = algorithmName;
        }

        @Override
        public String toString() {
            return String.format("%-16s | comparisons=%7d | swaps/shifts=%7d | time=%7d µs",
                    algorithmName, comparisons, swapsOrShifts, elapsedNs / 1_000);
        }
    }

    // ─── 1. Bubble Sort ───────────────────────────────────────────────────────

    public static Metrics bubbleSort(int[] arr) {
        Metrics m = new Metrics("Bubble Sort");
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                m.comparisons++;
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = tmp;
                    m.swapsOrShifts++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return m;
    }

    // ─── 2. Insertion Sort ────────────────────────────────────────────────────

    public static Metrics insertionSort(int[] arr) {
        Metrics m = new Metrics("Insertion Sort");
        int n = arr.length;

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0) {
                m.comparisons++;
                if (arr[j] > key) {
                    arr[j + 1] = arr[j];
                    j--;
                    m.swapsOrShifts++;
                } else break;
            }
            arr[j + 1] = key;
        }
        return m;
    }

    // ─── 3. Selection Sort ────────────────────────────────────────────────────

    public static Metrics selectionSort(int[] arr) {
        Metrics m = new Metrics("Selection Sort");
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                m.comparisons++;
                if (arr[j] < arr[minIdx]) minIdx = j;
            }
            if (minIdx != i) {
                int tmp = arr[i]; arr[i] = arr[minIdx]; arr[minIdx] = tmp;
                m.swapsOrShifts++;
            }
        }
        return m;
    }

    // ─── 4. Merge Sort ────────────────────────────────────────────────────────

    private static Metrics mergeMetrics;

    public static Metrics mergeSortDriver(int[] arr) {
        mergeMetrics = new Metrics("Merge Sort");
        mergeSort(arr, 0, arr.length - 1);
        return mergeMetrics;
    }

    private static void mergeSort(int[] arr, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private static void merge(int[] arr, int left, int mid, int right) {
        int[] L = new int[mid - left + 1];
        int[] R = new int[right - mid];
        System.arraycopy(arr, left,    L, 0, L.length);
        System.arraycopy(arr, mid + 1, R, 0, R.length);

        int i = 0, j = 0, k = left;
        while (i < L.length && j < R.length) {
            mergeMetrics.comparisons++;
            if (L[i] <= R[j]) arr[k++] = L[i++];
            else               arr[k++] = R[j++];
            mergeMetrics.swapsOrShifts++;
        }
        while (i < L.length)  { arr[k++] = L[i++]; mergeMetrics.swapsOrShifts++; }
        while (j < R.length)  { arr[k++] = R[j++]; mergeMetrics.swapsOrShifts++; }
    }

    // ─── 5. Quick Sort (median-of-three) ─────────────────────────────────────

    private static Metrics qsMetrics;

    public static Metrics quickSortDriver(int[] arr) {
        qsMetrics = new Metrics("Quick Sort");
        quickSort(arr, 0, arr.length - 1);
        return qsMetrics;
    }

    private static void quickSort(int[] arr, int low, int high) {
        if (low >= high) return;
        int pivotIdx = medianOfThree(arr, low, high);
        swap(arr, pivotIdx, high);

        int p = partition(arr, low, high);
        quickSort(arr, low, p - 1);
        quickSort(arr, p + 1, high);
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high], i = low - 1;
        for (int j = low; j < high; j++) {
            qsMetrics.comparisons++;
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
                qsMetrics.swapsOrShifts++;
            }
        }
        swap(arr, i + 1, high);
        qsMetrics.swapsOrShifts++;
        return i + 1;
    }

    private static int medianOfThree(int[] arr, int low, int high) {
        int mid = low + (high - low) / 2;
        int a = arr[low], b = arr[mid], c = arr[high];
        if ((a <= b && b <= c) || (c <= b && b <= a)) return mid;
        if ((b <= a && a <= c) || (c <= a && a <= b)) return low;
        return high;
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }

    // ─── Dataset generators ───────────────────────────────────────────────────

    private static int[] randomArray(int n) {
        int[] arr = new int[n];
        java.util.Random rnd = new java.util.Random(42);
        for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(10000);
        return arr;
    }

    private static int[] nearlySortedArray(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;
        // Shuffle 5% of elements
        java.util.Random rnd = new java.util.Random(42);
        int swapsToMake = n / 20;
        for (int k = 0; k < swapsToMake; k++) {
            int i = rnd.nextInt(n), j = rnd.nextInt(n);
            int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
        }
        return arr;
    }

    private static int[] reverseArray(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = n - i;
        return arr;
    }

    private static int[] duplicatesArray(int n) {
        int[] arr = new int[n];
        java.util.Random rnd = new java.util.Random(42);
        // Only 10 distinct values — simulates fee-code data
        for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(10) * 100;
        return arr;
    }

    // ─── Benchmark runner ─────────────────────────────────────────────────────

    private interface SortFn { Metrics sort(int[] arr); }

    private static Metrics benchmark(String algo, int[] original, SortFn fn) {
        int[] arr = java.util.Arrays.copyOf(original, original.length);
        long t0 = System.nanoTime();
        Metrics m = fn.sort(arr);
        m.elapsedNs = System.nanoTime() - t0;
        m.algorithmName = algo;
        return m;
    }

    private static void runProfile(String profileName, int[] original) {
        System.out.println("\n=== Profile: " + profileName + " (n=" + original.length + ") ===");

        List<Metrics> results = new ArrayList<>();
        results.add(benchmark("Bubble Sort",    original, arr -> bubbleSort(arr)));
        results.add(benchmark("Insertion Sort", original, arr -> insertionSort(arr)));
        results.add(benchmark("Selection Sort", original, arr -> selectionSort(arr)));
        results.add(benchmark("Merge Sort",     original, arr -> mergeSortDriver(arr)));
        results.add(benchmark("Quick Sort",     original, arr -> quickSortDriver(arr)));

        for (Metrics m : results) System.out.println("  " + m);

        // Find winner
        Metrics winner = results.get(0);
        for (Metrics m : results) {
            if (m.elapsedNs < winner.elapsedNs) winner = m;
        }
        System.out.println("  → Fastest: " + winner.algorithmName);
    }

    // ─── Main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        System.out.println("=== Problem 6: Sorting Algorithm Performance Comparison ===");

        int n = 2000; // Meaningful size to show O(n²) vs O(n log n) gap

        runProfile("Random Order",     randomArray(n));
        runProfile("Nearly Sorted",    nearlySortedArray(n));
        runProfile("Reverse Sorted",   reverseArray(n));
        runProfile("Many Duplicates",  duplicatesArray(n));

        // ── Algorithm Selection Guide ──
        System.out.println("\n=== Algorithm Selection Guide for Banking Workloads ===");
        System.out.printf("%-20s %-12s %-12s %-12s %-8s %-8s  %s%n",
                "Algorithm", "Best", "Average", "Worst", "Space", "Stable", "Best For");
        System.out.println("-".repeat(100));
        System.out.printf("%-20s %-12s %-12s %-12s %-8s %-8s  %s%n",
                "Bubble Sort",    "O(n)",      "O(n²)",     "O(n²)",     "O(1)", "YES",
                "Tiny batches ≤100, nearly sorted, demos");
        System.out.printf("%-20s %-12s %-12s %-12s %-8s %-8s  %s%n",
                "Insertion Sort", "O(n)",      "O(n²)",     "O(n²)",     "O(1)", "YES",
                "Small/medium 100-1000, nearly sorted, incremental feeds");
        System.out.printf("%-20s %-12s %-12s %-12s %-8s %-8s  %s%n",
                "Selection Sort", "O(n²)",     "O(n²)",     "O(n²)",     "O(1)", "NO",
                "Minimise writes (flash/EEPROM storage)");
        System.out.printf("%-20s %-12s %-12s %-12s %-8s %-8s  %s%n",
                "Merge Sort",     "O(n log n)","O(n log n)","O(n log n)","O(n)", "YES",
                "Large batches, stable sort required, external sort (disk)");
        System.out.printf("%-20s %-12s %-12s %-12s %-8s %-8s  %s%n",
                "Quick Sort",     "O(n log n)","O(n log n)","O(n²)*",    "O(log n)","NO",
                "General-purpose in-memory sort (fastest in practice)");

        System.out.println("\n* Quick Sort worst case O(n²) is mitigated by median-of-three pivot selection.");
        System.out.println("\nKey Takeaways:");
        System.out.println("  1. For compliance audit batches ≤100          → Bubble Sort (simple, stable)");
        System.out.println("  2. For incremental risk score updates          → Insertion Sort (adaptive)");
        System.out.println("  3. For portfolio rebalancing on low-write media→ Selection Sort (min writes)");
        System.out.println("  4. For monthly statement generation (millions) → Merge Sort (guaranteed O(n log n))");
        System.out.println("  5. For real-time ATM fraud feed sorting        → Quick Sort (fastest average)");
        System.out.println("  6. Java's Arrays.sort() uses TimSort (Merge+Insertion) — best of both worlds.");
    }
}
