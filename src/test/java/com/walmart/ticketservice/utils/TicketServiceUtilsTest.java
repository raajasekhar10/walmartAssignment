package com.walmart.ticketservice.utils;

import com.google.common.collect.Range;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.walmart.ticketservice.TestUtils.VENUE_CONFIGURATION;

public class TicketServiceUtilsTest {


    @Test
    public void testIsValidVenueLevel() {
        assertThat(TicketServiceUtils.isValidVenueLevel(VENUE_CONFIGURATION, 1)).isTrue();
    }

    @Test
    public void testIsNotValidVenueLevel() {
        assertThat(TicketServiceUtils.isValidVenueLevel(VENUE_CONFIGURATION, 5)).isFalse();
    }

    @Test
    public void testVenueLevelRange() {
        Range<Integer> result = TicketServiceUtils.venueLevelRange(VENUE_CONFIGURATION);
        assertThat(result).isNotNull();
    }

    @Test
    public void checkBadRequestedLevels() {
        assertThatThrownBy(() -> TicketServiceUtils.checkRequestedLevels(2, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minLevel 2 cannot be greater than or equal to maxLevel 1");
    }
}