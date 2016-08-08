package com.walmart.ticketservice.model;

import com.google.common.base.MoreObjects;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * A temporary or permanent reservation on a set of seats.
 */
public class SeatHold {
    private int id;
    private String customerEmail;
    private Set<Seat> heldSeats;
    private LocalDateTime holdTime = LocalDateTime.now();
    private String confirmationCode;

    public SeatHold(String customerEmail, Set<Seat> heldSeats) {
        this.customerEmail = customerEmail;
        this.heldSeats = heldSeats;
    }

    public SeatHold(String customerEmail, Set<Seat> heldSeats, LocalDateTime holdTime) {
        this.customerEmail = customerEmail;
        this.heldSeats = heldSeats;
        this.holdTime = holdTime;
    }

    public SeatHold(int id, String customerEmail, Set<Seat> heldSeats) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.heldSeats = heldSeats;
    }

    public SeatHold(int id, String customerEmail, Set<Seat> heldSeats, LocalDateTime holdTime) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.heldSeats = heldSeats;
        this.holdTime = holdTime;
    }

    /**
     * Use this constructor when creating a permanent reservation
     */
    public SeatHold(int id, String customerEmail, Set<Seat> heldSeats, String confirmationCode, LocalDateTime holdTime) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.heldSeats = heldSeats;
        this.confirmationCode = confirmationCode;
        this.holdTime = holdTime;
    }

    public int getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public Set<Seat> getHeldSeats() {
        return heldSeats;
    }

    public LocalDateTime getHoldTime() {
        return holdTime;
    }

    public Optional<String> getConfirmationCode() {
        return Optional.ofNullable(confirmationCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeatHold seatHold = (SeatHold) o;

        return id == seatHold.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("customerEmail", customerEmail)
                .add("heldSeats", heldSeats)
                .add("holdTime", holdTime)
                .add("confirmationCode", confirmationCode)
                .toString();
    }
}
