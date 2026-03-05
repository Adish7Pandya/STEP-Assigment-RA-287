Hotel Booking Management System 

An App-Based Learning Journey through Core Java & Data Structures 

📌 Project Objective 

This project bridges the gap between academic theory and real-world software engineering. Instead of treating data structures as isolated concepts, this system demonstrates their necessity in solving critical business challenges, such as real-time inventory consistency, fair request handling, and concurrency prevention.


🚀 Key Learning Pillars
The system is built incrementally, mirroring the evolution of a production-grade application: 

* Intentional Design: Every structure is chosen to solve a specific limitation of the previous iteration.
* Business Logic First: Focus remains on core system behavior, minimizing UI/input boilerplate to prioritize traceability and debugging.
* Deterministic Execution: Console-based interactions ensure that logic flow and state changes are clear and predictable. [12] 

------------------------------
🛠️ Data Structures & Their Business Context

| Data Structure [13, 14, 15, 16, 17] | Use Case in Hotel System | Problem Solved |
|---|---|---|
| ArrayList / List | Room Inventory Management | Maintaining a dynamic list of available rooms. |
| Queue (FIFO) | Booking Request Pipeline | Ensuring fairness: processing guests in the order they arrived. |
| HashSet | Unique Booking IDs | Preventing double-bookings and ensuring data integrity. |
| HashMap | Guest Profiles & Quick Lookup | Efficiently retrieving guest details without iterating through the whole list. |
| Comparable / Comparator | Room Pricing/Rating Sort | Enabling guests to find rooms based on specific preferences. |

------------------------------
🏗️ System Evolution (Incremental Stages)Phase 1: The Foundation (OOPS)

* Defining the Room, Guest, and Booking entities.
* Encapsulation and clear separation of concerns. [18, 19, 20, 21, 22] 

Phase 2: Fair Handling (Queues)

* Implementing a waitlist for peak seasons.
* Concept: First-In, First-Out (FIFO) processing. [23, 24, 25] 

Phase 3: Integrity & Uniqueness (Sets)

* Refactoring the system to prevent a single room from being assigned to two guests at once.
* Concept: Hashing and uniqueness enforcement.

Phase 4: Optimization (Maps)

* Moving from $O(n)$ search times to $O(1)$ for instant booking retrieval.
* Concept: Key-value pairing for high-performance lookups.

------------------------------
💻 Tech Stack

* Language: Java (JDK 11+)
* Concepts: Core Java, Collections Framework, Object-Oriented Programming (OOP).
* Architecture: Console-based Deterministic Logic. [26, 27, 28]
