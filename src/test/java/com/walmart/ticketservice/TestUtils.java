package com.walmart.ticketservice;

import com.google.common.collect.Sets;

import java.math.BigDecimal;

import com.walmart.ticketservice.model.Level;
import com.walmart.ticketservice.model.Seat;
import com.walmart.ticketservice.model.VenueConfiguration;

public class TestUtils {
    public static final String EMAIL = "email@test.com";
    public static final Level LEVEL_1 = createLevel(1, "level1", 2, 10, BigDecimal.ONE);
    public static final Level LEVEL_2 = createLevel(2, "level2", 4, 20, BigDecimal.ONE);
    public static final Level LEVEL_3 = createLevel(3, "level3", 2, 10, BigDecimal.ONE);
    public static int HOLD_LIMIT = 1;

    public static VenueConfiguration VENUE_CONFIGURATION = new VenueConfiguration(HOLD_LIMIT, Sets.newHashSet(LEVEL_1, LEVEL_2, LEVEL_3));

    private TestUtils() {
    }

    public static Seat createSeat(int level, int row, int number) {
        return new Seat(level, row, number);
    }

    public static Level createLevel(int id, int rows, int seatsPerRow) {
        return createLevel(id, "level" + id, rows, seatsPerRow, BigDecimal.ONE);
    }

    public static Level createLevel(int id, String name, int rows, int seatsPerRow, BigDecimal price) {
        return new Level(id, name, rows, seatsPerRow, price);
    }
}
