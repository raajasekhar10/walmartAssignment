package com.walmart.ticketservice.utils;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.walmart.ticketservice.model.Seat;

import static org.assertj.core.api.Assertions.assertThat;
import static com.walmart.ticketservice.TestUtils.VENUE_CONFIGURATION;

public class BasicSeatScorerTest {

    private SeatScorer seatScorer;

    @BeforeMethod
    public void setUp() throws Exception {
        seatScorer = new BasicSeatScorer(VENUE_CONFIGURATION);
    }

    @DataProvider(name = "scoreDatasource")
    public Object[][] dataSource() {
        return new Object[][]{
                {new Seat(1, 1, 1), 1},
                {new Seat(1, 2, 10), 20},
                {new Seat(2, 1, 1), 21},
                {new Seat(2, 1, 20), 40},
                {new Seat(2, 2, 2), 42},
                {new Seat(2, 4, 20), 100},
                {new Seat(2, 3, 11), 71}
        };
    }

    @Test(dataProvider = "scoreDatasource")
    public void testComputeScore(Seat seat, int expectedScore) throws Exception {
        int actualScore = seatScorer.computeScore(seat);
        assertThat(actualScore).isEqualTo(expectedScore);
    }
}