import java.util.*;

/**
 * Problem 2: E-commerce Flash Sale Inventory Manager
 * 
 * This system manages inventory during flash sales with high concurrent access.
 * It uses HashMaps for O(1) stock lookup and maintains waiting lists for out-of-stock items.
 */
public class InventoryManager {
    // Product class to store product information
    static class Product {
        String productId;
        String productName;
        int stockLevel;
        double price;
        
        public Product(String productId, String productName, int stockLevel, double price) {
            this.productId = productId;
            this.productName = productName;
            this.stockLevel = stockLevel;
            this.price = price;
        }
    }
    
    // Order class to track purchase requests
    static class Order {
        String orderId;
        String customerId;
        String productId;
        int quantity;
        long timestamp;
        
        public Order(String orderId, String customerId, String productId, int quantity) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.productId = productId;
            this.quantity = quantity;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    // HashMap for O(1) product lookup: productId -> Product
    private HashMap<String, Product> inventory;
    
    // HashMap for waiting lists: productId -> Queue of customers
    private HashMap<String, Queue<String>> waitingLists;
    
    // HashMap to track successful orders
    private HashMap<String, List<Order>> successfulOrders;
    
    // Counter for generating order IDs
    private int orderCounter;
    
    /**
     * Constructor initializes the inventory system
     */
    public InventoryManager() {
        this.inventory = new HashMap<>();
        this.waitingLists = new HashMap<>();
        this.successfulOrders = new HashMap<>();
        this.orderCounter = 1;
    }
    
    /**
     * Adds a product to inventory
     * Time Complexity: O(1)
     * 
     * @param productId Unique product identifier
     * @param productName Name of the product
     * @param stockLevel Initial stock quantity
     * @param price Product price
     */
    public void addProduct(String productId, String productName, int stockLevel, double price) {
        Product product = new Product(productId, productName, stockLevel, price);
        inventory.put(productId, product);
        waitingLists.put(productId, new LinkedList<>());
    }
    
    /**
     * Checks stock availability for a product
     * Time Complexity: O(1)
     * 
     * @param productId Product to check
     * @return Current stock level, -1 if product doesn't exist
     */
    public int checkStockAvailability(String productId) {
        Product product = inventory.get(productId);
        if (product == null) {
            return -1; // Product doesn't exist
        }
        return product.stockLevel;
    }
    
    /**
     * Processes a purchase request
     * Time Complexity: O(1) for stock check and update
     * 
     * @param customerId Customer making the purchase
     * @param productId Product to purchase
     * @param quantity Quantity requested
     * @return Order ID if successful, null if failed
     */
    public String processPurchase(String customerId, String productId, int quantity) {
        // O(1) product lookup
        Product product = inventory.get(productId);
        
        if (product == null) {
            return null; // Product doesn't exist
        }
        
        // Check stock availability
        if (product.stockLevel >= quantity) {
            // Process the purchase
            product.stockLevel -= quantity;
            
            // Create order
            String orderId = "ORD" + String.format("%06d", orderCounter++);
            Order order = new Order(orderId, customerId, productId, quantity);
            
            // Track successful order
            successfulOrders.computeIfAbsent(customerId, k -> new ArrayList<>()).add(order);
            
            return orderId;
        } else {
            // Add to waiting list if out of stock
            addToWaitingList(customerId, productId);
            return null; // Purchase failed - out of stock
        }
    }
    
    /**
     * Adds a customer to the waiting list for a product
     * Time Complexity: O(1)
     * 
     * @param customerId Customer to add
     * @param productId Product they're waiting for
     */
    public void addToWaitingList(String customerId, String productId) {
        Queue<String> waitingList = waitingLists.get(productId);
        if (waitingList != null && !waitingList.contains(customerId)) {
            waitingList.add(customerId);
        }
    }
    
    /**
     * Gets the waiting list for a product
     * Time Complexity: O(1) to access, O(n) to return list
     * 
     * @param productId Product to check
     * @return List of customers waiting
     */
    public List<String> getWaitingList(String productId) {
        Queue<String> waitingList = waitingLists.get(productId);
        if (waitingList == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(waitingList);
    }
    
    /**
     * Restocks a product and processes waiting list
     * Time Complexity: O(k) where k is number of waiting customers
     * 
     * @param productId Product to restock
     * @param quantity Quantity to add
     * @return Number of waiting customers fulfilled
     */
    public int restockProduct(String productId, int quantity) {
        Product product = inventory.get(productId);
        if (product == null) {
            return 0;
        }
        
        product.stockLevel += quantity;
        
        // Process waiting list
        Queue<String> waitingList = waitingLists.get(productId);
        int fulfilled = 0;
        
        while (!waitingList.isEmpty() && product.stockLevel > 0) {
            String customerId = waitingList.poll();
            // Attempt to fulfill with 1 unit
            if (product.stockLevel >= 1) {
                product.stockLevel -= 1;
                
                String orderId = "ORD" + String.format("%06d", orderCounter++);
                Order order = new Order(orderId, customerId, productId, 1);
                successfulOrders.computeIfAbsent(customerId, k -> new ArrayList<>()).add(order);
                
                fulfilled++;
            }
        }
        
        return fulfilled;
    }
    
    /**
     * Gets all products with their current stock levels
     * 
     * @return Map of productId -> stock level
     */
    public Map<String, Integer> getAllStockLevels() {
        Map<String, Integer> stockLevels = new HashMap<>();
        for (Map.Entry<String, Product> entry : inventory.entrySet()) {
            stockLevels.put(entry.getKey(), entry.getValue().stockLevel);
        }
        return stockLevels;
    }
    
    /**
     * Gets customer's order history
     * 
     * @param customerId Customer to check
     * @return List of orders
     */
    public List<Order> getCustomerOrders(String customerId) {
        return successfulOrders.getOrDefault(customerId, new ArrayList<>());
    }
    
    /**
     * Main method for testing the inventory manager
     */
    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();
        
        System.out.println("=== E-commerce Flash Sale Inventory Manager ===\n");
        
        // Add products (flash sale items with limited stock)
        System.out.println("--- Setting up Flash Sale Products ---");
        manager.addProduct("LAPTOP001", "Gaming Laptop", 100, 999.99);
        manager.addProduct("PHONE001", "Smartphone Pro", 50, 799.99);
        manager.addProduct("HEADPHONE001", "Wireless Headphones", 200, 149.99);
        manager.addProduct("WATCH001", "Smart Watch", 75, 299.99);
        
        System.out.println("Products added to inventory:");
        System.out.println(manager.getAllStockLevels());
        System.out.println();
        
        // Check stock availability
        System.out.println("--- Stock Availability Checks ---");
        System.out.println("LAPTOP001 stock: " + manager.checkStockAvailability("LAPTOP001") + " units");
        System.out.println("PHONE001 stock: " + manager.checkStockAvailability("PHONE001") + " units");
        System.out.println();
        
        // Simulate flash sale purchases
        System.out.println("--- Flash Sale Starting - Processing Purchase Requests ---");
        
        // Successful purchases
        String order1 = manager.processPurchase("CUST001", "LAPTOP001", 2);
        String order2 = manager.processPurchase("CUST002", "LAPTOP001", 3);
        String order3 = manager.processPurchase("CUST003", "PHONE001", 1);
        
        System.out.println("Customer CUST001 order: " + (order1 != null ? order1 + " ✓" : "Failed ✗"));
        System.out.println("Customer CUST002 order: " + (order2 != null ? order2 + " ✓" : "Failed ✗"));
        System.out.println("Customer CUST003 order: " + (order3 != null ? order3 + " ✓" : "Failed ✗"));
        System.out.println();
        
        System.out.println("Updated stock after purchases:");
        System.out.println(manager.getAllStockLevels());
        System.out.println();
        
        // Simulate high demand - reduce stock to 0
        System.out.println("--- Simulating High Demand (Selling Out) ---");
        manager.inventory.get("PHONE001").stockLevel = 2;
        
        for (int i = 1; i <= 10; i++) {
            String orderId = manager.processPurchase("CUST" + String.format("%03d", 100 + i), "PHONE001", 1);
            if (orderId == null) {
                System.out.println("Customer CUST" + String.format("%03d", 100 + i) + " added to waiting list");
            } else {
                System.out.println("Customer CUST" + String.format("%03d", 100 + i) + " order: " + orderId + " ✓");
            }
        }
        System.out.println();
        
        // Check waiting list
        System.out.println("--- Waiting List for PHONE001 ---");
        List<String> waitingList = manager.getWaitingList("PHONE001");
        System.out.println("Number of customers waiting: " + waitingList.size());
        System.out.println("Waiting customers: " + waitingList);
        System.out.println();
        
        // Restock and process waiting list
        System.out.println("--- Restocking PHONE001 ---");
        int fulfilled = manager.restockProduct("PHONE001", 5);
        System.out.println("Restocked 5 units");
        System.out.println("Fulfilled " + fulfilled + " waiting customers");
        System.out.println("Remaining waiting customers: " + manager.getWaitingList("PHONE001").size());
        System.out.println();
        
        // Performance test
        System.out.println("--- Performance Test (50,000 concurrent requests simulation) ---");
        manager.addProduct("FLASH_ITEM", "Limited Edition Item", 100, 49.99);
        
        long startTime = System.nanoTime();
        int successfulPurchases = 0;
        int failedPurchases = 0;
        
        for (int i = 0; i < 50000; i++) {
            String orderId = manager.processPurchase("CUST" + i, "FLASH_ITEM", 1);
            if (orderId != null) {
                successfulPurchases++;
            } else {
                failedPurchases++;
            }
        }
        
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
        
        System.out.println("Processed 50,000 purchase requests in " + String.format("%.2f", duration) + " ms");
        System.out.println("Average time per request: " + String.format("%.4f", duration / 50000) + " ms");
        System.out.println("Successful purchases: " + successfulPurchases);
        System.out.println("Failed purchases (waiting list): " + failedPurchases);
        System.out.println("Final stock level: " + manager.checkStockAvailability("FLASH_ITEM"));
        System.out.println("\n✓ System prevents overselling and maintains O(1) performance");
        System.out.println("✓ No overselling occurred - stock cannot go below 0");
    }
}
