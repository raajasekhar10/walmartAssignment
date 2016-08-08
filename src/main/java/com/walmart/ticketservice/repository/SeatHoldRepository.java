package com.walmart.ticketservice.repository;

import java.util.Optional;
import java.util.stream.Stream;

import com.walmart.ticketservice.model.SeatHold;

public interface SeatHoldRepository {

    Optional<SeatHold> find(int seatHoldId);

    Stream<SeatHold> findAllExpired(int holdLimit);

    SeatHold save(SeatHold seatHold);

    void delete(SeatHold seatHold);
}
