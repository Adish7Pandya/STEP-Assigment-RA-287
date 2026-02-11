# Week 1 - HashMap Data Structure Problems

This week focuses on practical applications of HashMap data structures for solving real-world problems with O(1) time complexity.

## Problem 1: Social Media Username Availability Checker

### Overview
A registration system for a social media platform that handles 10 million users and processes 1000 concurrent username checks per second.

### Features
- **O(1) Username Lookup**: Instant availability checking using HashMap
- **Username Suggestions**: Provides alternative usernames when requested one is taken
- **Popularity Tracking**: Tracks and reports most frequently attempted usernames
- **High Performance**: Handles concurrent requests efficiently

### Key Concepts
- HashMap basics (key-value mapping)
- O(1) lookup performance
- Collision handling
- Frequency counting

### Implementation Details
- `HashMap<String, Integer>` for username → userId mapping
- `HashMap<String, Integer>` for tracking attempt frequency
- Multiple suggestion strategies:
  - Appending numbers (1-10)
  - Replacing underscores with dots
  - Adding underscores
  - Prepending common prefixes

### Usage
```bash
javac UsernameAvailabilityChecker.java
java UsernameAvailabilityChecker
```

### Sample Output
```
checkAvailability("john_doe") → false (already taken)
checkAvailability("jane_smith") → false (already taken)
checkAvailability("new_user") → true (available)
suggestAlternatives("john_doe") → ["john_doe1", "john_doe2", "john_doe3"]
getMostAttempted() → "admin (52 attempts)"
```

### Performance
- Processes 1000 username checks in ~4ms
- Average time per check: ~0.004ms
- Demonstrates O(1) time complexity

---

## Problem 2: E-commerce Flash Sale Inventory Manager

### Overview
An inventory management system for flash sales where 50,000 customers simultaneously try to purchase limited stock items (100 units available). Prevents overselling while maintaining high performance.

### Features
- **Real-time Stock Tracking**: O(1) stock level monitoring
- **Purchase Processing**: Fast order fulfillment with inventory updates
- **Waiting List Management**: Automatic queue for out-of-stock items
- **Restock Processing**: Fulfills waiting customers automatically
- **Overselling Prevention**: Ensures stock never goes negative

### Key Concepts
- HashMap for instant stock lookup
- Collision resolution (multiple users buying same product)
- Queue management for waiting lists
- Performance optimization under high load

### Implementation Details
- `HashMap<String, Product>` for productId → Product mapping
- `HashMap<String, Queue<String>>` for waiting lists per product
- `HashMap<String, List<Order>>` for order tracking
- Atomic stock updates to prevent overselling

### Usage
```bash
javac InventoryManager.java
java InventoryManager
```

### Sample Output
```
Stock Availability:
LAPTOP001 stock: 100 units
PHONE001 stock: 50 units

Flash Sale Results:
Customer CUST001 order: ORD000001 ✓
Customer CUST002 order: ORD000002 ✓

Waiting List: [CUST103, CUST104, CUST105, ...]
Restocked 5 units → Fulfilled 5 waiting customers
```

### Performance
- Processes 50,000 purchase requests in ~3.5 seconds
- Average time per request: ~0.07ms
- Successfully prevents overselling (0 stock never goes negative)
- Demonstrates O(1) time complexity for individual operations

---

## Technical Requirements
- Java 8 or higher
- No external dependencies required

## Running the Programs

### Compile
```bash
cd "Week 1"
javac UsernameAvailabilityChecker.java
javac InventoryManager.java
```

### Execute
```bash
java UsernameAvailabilityChecker
java InventoryManager
```

## Key Takeaways

1. **HashMap Efficiency**: Both problems demonstrate O(1) lookup and insertion performance
2. **Real-world Applications**: Practical use cases for social media and e-commerce platforms
3. **Scalability**: Solutions handle high concurrent load efficiently
4. **Data Structure Selection**: HashMap is ideal for scenarios requiring fast lookups by key

## Use Cases

### Username Checker
- Twitter/Instagram registration
- Gaming platform username selection
- Email address availability checking

### Inventory Manager
- E-commerce flash sales
- Limited edition product launches
- Ticket booking systems
- Reservation management

---

## Author
STEP Assignment RA-287
