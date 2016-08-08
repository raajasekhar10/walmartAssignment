package com.walmart.ticketservice.utils;

import com.walmart.ticketservice.model.Seat;

/**
 * Given a Seat compute its 'score'.  The score can be used to determine the best available seats.
 *
 * <strong>Note:</strong> This scoring goes from smallest to largest.  Meaning that a seat with
 * score = 1 will be better than a seat with score = 2.
 */
public interface SeatScorer {
    int computeScore(Seat seat);
}
