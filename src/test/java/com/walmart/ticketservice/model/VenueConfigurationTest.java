package com.walmart.ticketservice.model;

import com.google.common.collect.Sets;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class VenueConfigurationTest {

    @DataProvider(name = "invalidHoldLimitDatasource")
    public Object[][] invalidHoldLimitDatasource() {
        return new Object[][]{
                {-1},
                {0}
        };
    }

    @Test(dataProvider = "invalidHoldLimitDatasource")
    public void testInvalidHoldLimit(int value) {
        assertThatThrownBy(() -> new VenueConfiguration(value, Sets.newHashSet()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("holdLimit must be greater than 0");
    }

    @Test()
    public void testEmptySetException() {
        assertThatThrownBy(() -> new VenueConfiguration(1, Sets.newHashSet()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("levels cannot be empty");
    }

}