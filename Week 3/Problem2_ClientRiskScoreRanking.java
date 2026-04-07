import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Problem 2: Client Risk Score Ranking
 *
 * Scenario: Risk management team needs quick sorting of client risk scores
 * for priority review.
 *
 * Concepts Covered:
 *   - In-place sorting algorithms (O(1) extra space)
 *   - Bubble Sort: visualize swaps step-by-step (good for demos)
 *   - Insertion Sort: ascending + descending, adaptive on nearly-sorted data
 *   - Identifying top-N clients post-sort
 *   - Space complexity O(1)
 */
public class Problem2_ClientRiskScoreRanking {

    // ─── Data Model ──────────────────────────────────────────────────────────

    static class Client {
        String name;
        double riskScore;       // 0.0 – 100.0  (higher = more risky)
        double accountBalance;  // USD

        Client(String name, double riskScore, double accountBalance) {
            this.name = name;
            this.riskScore = riskScore;
            this.accountBalance = accountBalance;
        }

        @Override
        public String toString() {
            return String.format("%-12s | risk=%.1f | balance=$%.2f", name, riskScore, accountBalance);
        }
    }

    // ─── UC1: Bubble Sort – ascending riskScore (with swap visualisation) ────

    /**
     * In-place Bubble Sort (ascending riskScore).
     * Prints each swap so the movement of clients can be visualised — useful
     * for demos / classroom walkthroughs.
     *
     * Time Complexity:
     *   Best  : O(n)  — early-termination when no swap occurs in a pass
     *   Worst : O(n²) — reverse sorted
     * Space : O(1)
     * Stable: YES (strict > comparison)
     */
    public static void bubbleSortAscending(Client[] clients, boolean verbose) {
        int n = clients.length;
        int pass = 0;
        int totalSwaps = 0;

        System.out.println("Bubble Sort (ascending riskScore) — " + n + " clients");

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            pass++;

            for (int j = 0; j < n - 1 - i; j++) {
                if (clients[j].riskScore > clients[j + 1].riskScore) {
                    // Swap
                    Client temp = clients[j];
                    clients[j] = clients[j + 1];
                    clients[j + 1] = temp;
                    swapped = true;
                    totalSwaps++;

                    if (verbose) {
                        System.out.printf("  Pass %d | Swap: %s <-> %s%n",
                                pass, clients[j + 1].name, clients[j].name);
                    }
                }
            }
            if (!swapped) {
                System.out.println("  Early termination at pass " + pass + " (no swaps — array sorted)");
                break;
            }
        }
        System.out.println("Total passes: " + pass + " | Total swaps: " + totalSwaps);
    }

    // ─── UC2: Insertion Sort – DESC riskScore, tie-break by accountBalance DESC

    /**
     * In-place Insertion Sort:
     *   Primary   : riskScore DESCENDING  (highest risk first)
     *   Secondary : accountBalance DESCENDING (higher balance first on tie)
     *
     * Adaptive: performs very well (near O(n)) when the input is already
     * nearly sorted — typical in daily incremental risk-score updates.
     *
     * Time Complexity:
     *   Best  : O(n)  — nearly sorted
     *   Worst : O(n²) — reverse sorted
     * Space : O(1)
     * Stable: YES
     */
    public static void insertionSortDescRiskBalance(Client[] clients) {
        int n = clients.length;
        int shifts = 0;

        System.out.println("\nInsertion Sort (risk DESC, balance DESC)");

        for (int i = 1; i < n; i++) {
            Client key = clients[i];
            int j = i - 1;

            while (j >= 0 && compareDescRiskBalance(clients[j], key) > 0) {
                clients[j + 1] = clients[j];
                j--;
                shifts++;
            }
            clients[j + 1] = key;
        }
        System.out.println("Total shifts: " + shifts);
    }

    /**
     * Comparator for descending sort.
     * Returns positive when 'a' should come AFTER 'b' (i.e., a has lower priority).
     */
    private static int compareDescRiskBalance(Client a, Client b) {
        if (Double.compare(b.riskScore, a.riskScore) != 0) {
            return Double.compare(b.riskScore, a.riskScore);  // higher risk first
        }
        return Double.compare(b.accountBalance, a.accountBalance); // higher balance first on tie
    }

    // ─── UC3: Print top-N highest-risk clients ────────────────────────────────

    /**
     * Assumes the array is already sorted in descending riskScore order.
     * Prints the top-N entries for the risk management priority queue.
     */
    public static void printTopNHighRisk(Client[] clients, int n) {
        System.out.println("\n--- Top " + n + " Highest-Risk Clients ---");
        int limit = Math.min(n, clients.length);
        for (int i = 0; i < limit; i++) {
            System.out.printf("  #%d %s%n", i + 1, clients[i]);
        }
    }

    // ─── Helper: print full array ─────────────────────────────────────────────

    private static void printClients(Client[] clients, String label) {
        System.out.println(label);
        for (int i = 0; i < clients.length; i++) {
            System.out.printf("  [%2d] %s%n", i + 1, clients[i]);
        }
    }

    // ─── Helper: deep copy array ──────────────────────────────────────────────

    private static Client[] copyArray(Client[] src) {
        Client[] copy = new Client[src.length];
        for (int i = 0; i < src.length; i++) {
            copy[i] = new Client(src[i].name, src[i].riskScore, src[i].accountBalance);
        }
        return copy;
    }

    // ─── Main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        System.out.println("=== Problem 2: Client Risk Score Ranking ===\n");

        // ── Sample dataset (20 clients — representative of 500) ──
        Client[] clients = {
            new Client("Alice",    72.5, 125000),
            new Client("Bob",      45.0,  80000),
            new Client("Carol",    88.0, 210000),
            new Client("Dave",     33.5,  50000),
            new Client("Eve",      95.0, 340000),
            new Client("Frank",    72.5,  95000),  // same risk as Alice
            new Client("Grace",    61.0, 175000),
            new Client("Hank",     20.0,  30000),
            new Client("Iris",     88.0, 110000),  // same risk as Carol
            new Client("Jack",     55.5,  67000),
            new Client("Karen",    40.0, 220000),
            new Client("Leo",      78.0,  89000),
            new Client("Mia",      91.5, 450000),
            new Client("Nathan",   63.0,  72000),
            new Client("Olivia",   29.0,  41000),
            new Client("Paul",     84.5, 160000),
            new Client("Quinn",    50.0,  58000),
            new Client("Rose",     77.0, 130000),
            new Client("Steve",    66.0,  95000),
            new Client("Tina",     38.0,  48000),
        };

        System.out.println("Input (" + clients.length + " clients):");
        for (Client c : clients) System.out.println("  " + c);
        System.out.println();

        // ── UC1: Bubble Sort ascending (with swap visualisation) ──
        Client[] bubbleArr = copyArray(clients);
        bubbleSortAscending(bubbleArr, true);
        System.out.println();
        printClients(bubbleArr, "After Bubble Sort (ascending riskScore):");

        // ── Stability check: Alice and Frank both have risk=72.5 ──
        System.out.println();
        int aliceIdx = -1, frankIdx = -1;
        for (int i = 0; i < bubbleArr.length; i++) {
            if (bubbleArr[i].name.equals("Alice")) aliceIdx = i;
            if (bubbleArr[i].name.equals("Frank")) frankIdx = i;
        }
        System.out.println("Stability: Alice (risk=72.5) at index " + aliceIdx
                + ", Frank (risk=72.5) at index " + frankIdx
                + " → Alice before Frank? " + (aliceIdx < frankIdx)
                + " (original order preserved = stable)");

        // ── UC2: Insertion Sort DESC risk + balance ──
        Client[] insertArr = copyArray(clients);
        insertionSortDescRiskBalance(insertArr);
        System.out.println();
        printClients(insertArr, "After Insertion Sort (risk DESC, balance DESC):");

        // ── UC3: Top 10 highest-risk clients ──
        printTopNHighRisk(insertArr, 10);

        // ── Space complexity note ──
        System.out.println("\n--- Space Complexity ---");
        System.out.println("Both Bubble Sort and Insertion Sort operate IN-PLACE.");
        System.out.println("Extra space used: O(1) — only a single 'temp' / 'key' variable.");

        // ── Adaptive behaviour demo ──
        System.out.println("\n--- Adaptive Behaviour Demo ---");
        System.out.println("Insertion Sort on an already-sorted array (best case):");
        Client[] nearSorted = copyArray(insertArr); // already sorted descending
        long t0 = System.nanoTime();
        insertionSortDescRiskBalance(nearSorted);
        long t1 = System.nanoTime();
        System.out.println("Time on already-sorted input: " + (t1 - t0) + " ns  (near O(n) — minimal shifts)");
    }
}
