package com.walmart.ticketservice.service;

import java.util.Optional;

import com.walmart.ticketservice.exception.TicketServiceException;
import com.walmart.ticketservice.model.SeatHold;

public interface TicketService {

    /**
     * The number of seats in the requested level that are neither held nor reserved
     *
     * @param venueLevel a numeric venue level identifier to limit the search
     * @return the number of tickets available on the provided level
     * @throws IllegalArgumentException if a venueLevel is provided but not a valid level id.
     */
    int numSeatsAvailable(Optional<Integer> venueLevel);

    /**
     * Find and hold the best available seats for a customer
     *
     * @param numSeats      the number of seats to find and hold.  Must be > 0
     * @param minLevel      the minimum venue level.  Must be less than max if provided
     * @param maxLevel      the maximum venue level.  Must be greater than min if provided
     * @param customerEmail unique identifier for the customer
     * @return a SeatHold object identifying the specific seats and related information
     * @throws IllegalArgumentException
     * @throws NoAvailableSeatsException if no seats are available
     */
    SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel,
                              Optional<Integer> maxLevel, String customerEmail) throws TicketServiceException;

    /**
     * Commit seats held for a specific customer
     *
     * @param seatHoldId    the seat hold identifier
     * @param customerEmail the email address of the customer to which the seat hold is assigned
     * @return a reservation confirmation code
     * @throws SeatHoldException if the {@link SeatHold} has expired
     */
    String reserveSeats(int seatHoldId, String customerEmail) throws TicketServiceException;

}
