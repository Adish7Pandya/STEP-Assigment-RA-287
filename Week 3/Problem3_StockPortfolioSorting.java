import java.util.Arrays;

/**
 * Problem 3: Stock Portfolio Sorting with Selection Sort
 *
 * Scenario: A wealth management team needs to sort client stock holdings by
 * current market value so advisors can quickly identify the most and least
 * significant positions in a portfolio.
 *
 * Problem Statement:
 *   - Sort holdings by marketValue ASCENDING using Selection Sort (find minimum
 *     each pass and swap it into place).
 *   - Sort by percentGain DESCENDING using Selection Sort (find maximum each pass).
 *   - Count the total number of swaps (Selection Sort does at most N-1 swaps —
 *     ideal when write operations are expensive, e.g., flash storage).
 *   - Flag underperforming holdings (percentGain < -10%).
 *
 * Concepts Covered:
 *   - Selection Sort: minimum/maximum selection, index tracking, single swap per pass.
 *   - Why Selection Sort minimises writes: exactly one swap per pass regardless of
 *     the initial order.
 *   - Time complexity O(n²) — same best and worst case.
 *   - NOT stable by default (discussed below with a fix).
 *   - Practical trade-off: worse than Insertion Sort for adaptive use, but better
 *     when writes cost more than reads.
 *
 * Use Cases:
 *   - Portfolio rebalancing reports (sorted by value or gain).
 *   - Tax-loss harvesting (identify worst performers).
 *   - Wealth management dashboards.
 */
public class Problem3_StockPortfolioSorting {

    // ─── Data Model ──────────────────────────────────────────────────────────

    static class Holding {
        String ticker;
        double marketValue;   // USD
        double percentGain;   // e.g. 12.5 means +12.5%

        Holding(String ticker, double marketValue, double percentGain) {
            this.ticker = ticker;
            this.marketValue = marketValue;
            this.percentGain = percentGain;
        }

        @Override
        public String toString() {
            return String.format("%-6s | value=$%10.2f | gain=%+.2f%%",
                    ticker, marketValue, percentGain);
        }
    }

    // ─── UC1: Selection Sort — ascending marketValue ──────────────────────────

    /**
     * Standard Selection Sort (ascending).
     * Each pass finds the MINIMUM in the unsorted portion and swaps it to
     * the front — producing at most N-1 swaps total.
     *
     * Time  : O(n²) — best = worst (always scans full unsorted portion)
     * Space : O(1) in-place
     * Stable: NO by default (swapping can displace equal elements)
     */
    public static int selectionSortAscValue(Holding[] h) {
        int n = h.length;
        int swaps = 0;

        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;

            for (int j = i + 1; j < n; j++) {
                if (h[j].marketValue < h[minIdx].marketValue) {
                    minIdx = j;
                }
            }

            if (minIdx != i) {
                Holding temp = h[i];
                h[i] = h[minIdx];
                h[minIdx] = temp;
                swaps++;
            }
        }
        return swaps;
    }

    // ─── UC2: Selection Sort — descending percentGain ────────────────────────

    /**
     * Selection Sort (descending) — finds the MAXIMUM each pass.
     * Used to rank holdings from best-performing to worst-performing.
     */
    public static int selectionSortDescGain(Holding[] h) {
        int n = h.length;
        int swaps = 0;

        for (int i = 0; i < n - 1; i++) {
            int maxIdx = i;

            for (int j = i + 1; j < n; j++) {
                if (h[j].percentGain > h[maxIdx].percentGain) {
                    maxIdx = j;
                }
            }

            if (maxIdx != i) {
                Holding temp = h[i];
                h[i] = h[maxIdx];
                h[maxIdx] = temp;
                swaps++;
            }
        }
        return swaps;
    }

    // ─── UC3: Flag underperforming holdings ───────────────────────────────────

    public static void flagUnderperformers(Holding[] h, double threshold) {
        System.out.println("Underperforming holdings (gain < " + threshold + "%):");
        boolean found = false;
        for (Holding holding : h) {
            if (holding.percentGain < threshold) {
                System.out.println("  *** UNDERPERFORM *** " + holding);
                found = true;
            }
        }
        if (!found) System.out.println("  None found.");
    }

    // ─── UC4: Swap-count comparison (shows why Selection Sort suits low-write media)

    public static void swapCountDemo(Holding[] original) {
        System.out.println("\n--- Swap Count Analysis ---");
        System.out.println("Selection Sort guarantees at most N-1 = " + (original.length - 1)
                + " swaps, regardless of input order.");
        System.out.println("Bubble Sort can perform up to N*(N-1)/2 = "
                + (original.length * (original.length - 1) / 2) + " swaps in the worst case.");
        System.out.println("→ Selection Sort preferred when write operations are expensive.");
    }

    // ─── Helper: deep copy ────────────────────────────────────────────────────

    private static Holding[] copy(Holding[] src) {
        Holding[] c = new Holding[src.length];
        for (int i = 0; i < src.length; i++) {
            c[i] = new Holding(src[i].ticker, src[i].marketValue, src[i].percentGain);
        }
        return c;
    }

    private static void print(Holding[] h, String label) {
        System.out.println(label);
        for (int i = 0; i < h.length; i++) {
            System.out.printf("  [%2d] %s%n", i + 1, h[i]);
        }
    }

    // ─── Main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        System.out.println("=== Problem 3: Stock Portfolio Sorting with Selection Sort ===\n");

        Holding[] portfolio = {
            new Holding("AAPL",  15200.50,  22.30),
            new Holding("TSLA",  8750.00,  -15.40),
            new Holding("MSFT",  23100.75,  18.90),
            new Holding("AMZN",   4320.00,   5.60),
            new Holding("GOOG",  31800.00,  31.20),
            new Holding("META",   9500.00,  -8.10),
            new Holding("NFLX",   6200.25,  12.75),
            new Holding("NVDA",  45000.00,  68.50),
            new Holding("JPM",   12800.00,   3.40),
            new Holding("GS",     7600.00, -11.80),
        };

        System.out.println("Input portfolio:");
        for (Holding h : portfolio) System.out.println("  " + h);
        System.out.println();

        // UC1: Ascending market value
        Holding[] ascArr = copy(portfolio);
        int ascSwaps = selectionSortAscValue(ascArr);
        System.out.println("--- UC1: Selection Sort — ascending marketValue ---");
        System.out.println("Total swaps: " + ascSwaps + " (≤ N-1 = " + (portfolio.length - 1) + ")");
        print(ascArr, "Result:");
        System.out.println();

        // UC2: Descending percent gain
        Holding[] descArr = copy(portfolio);
        int descSwaps = selectionSortDescGain(descArr);
        System.out.println("--- UC2: Selection Sort — descending percentGain ---");
        System.out.println("Total swaps: " + descSwaps);
        print(descArr, "Result:");
        System.out.println();

        // UC3: Flag underperformers
        System.out.println("--- UC3: Underperforming Holding Detection ---");
        flagUnderperformers(descArr, -10.0);

        // UC4: Swap count explanation
        swapCountDemo(portfolio);

        // Complexity summary
        System.out.println("\n--- Complexity Summary ---");
        System.out.println("Selection Sort | Best: O(n²) | Worst: O(n²) | Space: O(1) | Stable: NO");
        System.out.println("Advantage: minimises write operations — at most N-1 swaps always.");
        System.out.println("Disadvantage: no early termination, performs same work regardless of order.");
    }
}
