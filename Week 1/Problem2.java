import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

class FlashSaleManager {

    private ConcurrentHashMap<String, AtomicInteger> stock = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> waiting = new ConcurrentHashMap<>();

    public void addProduct(String productId, int quantity) {
        stock.put(productId, new AtomicInteger(quantity));
        waiting.put(productId, new ConcurrentLinkedQueue<>());
    }

    public int checkStock(String productId) {
        return stock.get(productId).get();
    }

    public String purchaseItem(String productId, int userId) {
        AtomicInteger s = stock.get(productId);
        if (s.getAndUpdate(v -> v > 0 ? v - 1 : v) > 0)
            return "Success, " + s.get() + " units remaining";
        waiting.get(productId).add(userId);
        return "Added to waiting list, position #" + waiting.get(productId).size();
    }

    public static void main(String[] args) {
        FlashSaleManager m = new FlashSaleManager();
        m.addProduct("IPHONE15_256GB", 100);
        System.out.println(m.checkStock("IPHONE15_256GB"));
        System.out.println(m.purchaseItem("IPHONE15_256GB", 12345));
    }
}
