package com.walmart.ticketservice.utils;

import java.util.Comparator;

import com.walmart.ticketservice.model.Seat;

/**
 * Uses the score assigns to each seat.
 */
public class SeatComparator implements Comparator<Seat> {
    @Override
    public int compare(Seat o1, Seat o2) {
        return Integer.compare(o1.getScore(), o2.getScore());
    }
}
