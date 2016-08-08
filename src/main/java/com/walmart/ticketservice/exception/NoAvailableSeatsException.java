package com.walmart.ticketservice.exception;

import java.util.Optional;

public class NoAvailableSeatsException extends TicketServiceException {
    private int numSeats;
    private String customerEmail;
    private Optional<Integer> minLevel;
    private Optional<Integer> maxLevel;
    private int available;

    public NoAvailableSeatsException(String message, int numSeats, String customerEmail, Optional<Integer> minLevel, Optional<Integer> maxLevel, int available) {
        super(message);
        this.numSeats = numSeats;
        this.customerEmail = customerEmail;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.available = available;
    }

    public int getNumSeats() {
        return numSeats;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public Optional<Integer> getMinLevel() {
        return minLevel;
    }

    public Optional<Integer> getMaxLevel() {
        return maxLevel;
    }

    public int getAvailable() {
        return available;
    }
}
