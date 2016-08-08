package com.walmart.ticketservice.utils;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walmart.ticketservice.model.Level;
import com.walmart.ticketservice.model.Seat;
import com.walmart.ticketservice.model.VenueConfiguration;

/**
 * A {@link SeatScorer} that determines the score using a count.
 *
 * The seat in level 1, row 1, seat 1 would be the 'best' seat, while the seat in level max, row
 * max, seats per row would be the 'worst' seat
 */
public class BasicSeatScorer implements SeatScorer {

    private static final Logger logger = LoggerFactory.getLogger(BasicSeatScorer.class);

    private VenueConfiguration venueConfiguration;

    /**
     * @param venueConfiguration a valid {@link VenueConfiguration} that includes at least 1 level
     * @throws IllegalStateException if the venue configuration does not include any levels
     */
    public BasicSeatScorer(VenueConfiguration venueConfiguration) {
        this.venueConfiguration = venueConfiguration;
    }

    @Override
    public int computeScore(Seat seat) {
        Preconditions.checkArgument(seat != null, "seat cannot be null");
        /**
         * Determine the score using the following steps
         * 1.  O = the level offset if level != 1
         * 2.  M = For a given row, get the largest score possible for that row.
         * 3.  Score = M - (Level.seatsPerRow - seatNumber) + O
         */
        Level level = getLevel(seat.getLevel());
        int row = seat.getRow();
        int levelOffset = 0;
        int maxScore;

        if (seat.getLevel() != 0)
            levelOffset = getLevelOffset(seat.getLevel());

        maxScore = row * level.getSeatsPerRow();

        int score = maxScore - (level.getSeatsPerRow() - seat.getNumber()) + levelOffset;
        logger.debug("Calculated score of {} using levelOffset {}, maxScore {} and row {} for seat {}",
                score, levelOffset, maxScore, row, seat);
        return score;
    }

    private Integer getLevelOffset(int level) {
        return venueConfiguration.getLevels().stream()
                .filter(l -> l.getId() < level)
                .mapToInt(Level::getTotalSeats)
                .sum();
    }

    private Level getLevel(int level) {
        return venueConfiguration.getLevels().stream()
                .filter(l -> l.getId() == level)
                .findFirst().get();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("venueConfiguration", venueConfiguration)
                .toString();
    }
}
