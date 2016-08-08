package com.walmart.ticketservice.exception;

public class SeatHoldException extends TicketServiceException {
    private int seatHoldId;
    private String customer;

    public SeatHoldException(String message, int seatHoldId, String customer) {
        super(message);
        this.seatHoldId = seatHoldId;
        this.customer = customer;
    }

    public int getSeatHoldId() {
        return seatHoldId;
    }

    public String getCustomer() {
        return customer;
    }
}
