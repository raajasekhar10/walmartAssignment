package com.walmart.ticketservice.exception;

import com.walmart.ticketservice.model.Seat;

/**
 * This exception is thrown when a 1 or more seats are saved that do not belong in the original
 * venue configuration;
 */
public class SeatRepositoryException extends RuntimeException {

    private Iterable<Seat> badSeats;

    public SeatRepositoryException(Iterable<Seat> badSeats) {
        this.badSeats = badSeats;
    }

    public SeatRepositoryException(String message, Iterable<Seat> badSeats) {
        super(message);
        this.badSeats = badSeats;
    }

    public SeatRepositoryException(String message, Throwable cause, Iterable<Seat> badSeats) {
        super(message, cause);
        this.badSeats = badSeats;
    }

    public SeatRepositoryException(Throwable cause, Iterable<Seat> badSeats) {
        super(cause);
        this.badSeats = badSeats;
    }

    public SeatRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Iterable<Seat> badSeats) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.badSeats = badSeats;
    }

    public Iterable<Seat> getBadSeats() {
        return badSeats;
    }
}
