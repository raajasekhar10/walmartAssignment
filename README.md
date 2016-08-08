# walmart-assignment

# Assumptions

Added runtime exceptions for the following cases:
 + Attempting to reserve seats after the seat hold expired
 + The number of seats requested exceeds the total available seats

TicketService implementation is not thread safe or transactional.



# Requirements
+ Java 8
+ Maven 3

# Running
From a terminal run:

    mvn test

