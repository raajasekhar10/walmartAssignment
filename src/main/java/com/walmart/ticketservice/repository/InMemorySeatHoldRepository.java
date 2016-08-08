package com.walmart.ticketservice.repository;

import com.google.common.collect.Sets;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.walmart.ticketservice.model.SeatHold;

public class InMemorySeatHoldRepository implements SeatHoldRepository {

    private Set<SeatHold> seatHolds = Sets.newHashSet();
    private int seatHoldId = 0;

    @Override
    public Optional<SeatHold> find(int seatHoldId) {
        return stream().filter(s -> s.getId() == seatHoldId).findFirst();
    }

    @Override
    public Stream<SeatHold> findAllExpired(int holdLimit) {
        return stream().filter(s ->
                s.getHoldTime().plusSeconds(holdLimit).isBefore(LocalDateTime.now())
        );
    }

    @Override
    public SeatHold save(SeatHold seatHold) {
        SeatHold toReturn;
        if (seatHold.getId() == 0) {
            toReturn = copy(seatHold, ++seatHoldId);
        } else {
            toReturn = copy(seatHold);
            seatHolds.remove(toReturn);
        }
        seatHolds.add(copy(toReturn));
        return toReturn;
    }

    @Override
    public void delete(SeatHold seatHold) {
        seatHolds.remove(seatHold);
    }

    private Stream<SeatHold> stream() {
        return seatHolds.stream().map(this::copy);
    }

    private SeatHold copy(SeatHold s) {
        return new SeatHold(s.getId(), s.getCustomerEmail(), s.getHeldSeats(), s.getConfirmationCode().orElse(null), s.getHoldTime());
    }

    private SeatHold copy(SeatHold s, int id) {
        return new SeatHold(id, s.getCustomerEmail(), s.getHeldSeats(), s.getConfirmationCode().orElse(null), s.getHoldTime());
    }
}
