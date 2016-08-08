# walmart-assignment

# Assumptions

Added runtime exceptions for the following cases:
 + Attempting to reserve seats after the seat hold expired
 + The number of seats requested exceeds the total available seats

NOTE: TicketService implementation is not transactional.



# Requirements
+ Java 8
+ Maven 3

# Running
From command prompt/terminal run the following maven command:

    mvn test

