package com.walmart.ticketservice.model;

import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelTest {

    @Test
    public void testGetTotalSeats() {
        Level level = new Level(1, "test", 2, 5, BigDecimal.ONE);
        assertThat(level.getTotalSeats()).isEqualTo(10);
    }
}