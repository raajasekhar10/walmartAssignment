package com.walmart.ticketservice.exception;

public abstract class TicketServiceException extends RuntimeException {

    public TicketServiceException(String message) {
        super(message);
    }

}
