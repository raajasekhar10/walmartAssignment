package com.walmart.ticketservice.service;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Optional;

import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.VenueConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.walmart.ticketservice.TestUtils.VENUE_CONFIGURATION;


public class AbstractTicketServiceTest {

    TestTicketService testTicketService;

    @BeforeMethod
    public void setUp() throws Exception {
        testTicketService = new TestTicketService(VENUE_CONFIGURATION);
    }

    @Test
    public void testNumSeatsAvailableNoLevel() {
        testTicketService.numSeatsAvailable(Optional.empty());
        alwaysAssert();
        assertThat(testTicketService.calledDoNumberSeatsAvailable).isTrue();
    }

    @Test
    public void testNumSeatsAvailableWithGoodLevel() {
        testTicketService.numSeatsAvailable(Optional.of(1));
        alwaysAssert();
        assertThat(testTicketService.calledDoNumberSeatsAvailable).isTrue();
    }

    @Test
    public void testNumSeatsAvailableWithBadLevel() {
        assertThatThrownBy(() -> testTicketService.numSeatsAvailable(Optional.of(100)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("venueLevel 100 is not part of venue configuration.");
        assertThat(testTicketService.calledCleanUp).isFalse();
        assertThat(testTicketService.calledFreeUp).isFalse();
        assertThat(testTicketService.calledDoNumberSeatsAvailable).isFalse();
    }

    @Test
    public void testFindAndHoldSeats() throws Exception {
        testTicketService.findAndHoldSeats(1, null, null, "test@test.com");
        alwaysAssert();
        assertThat(testTicketService.calledDoFindAndHoldSeats).isTrue();
    }

    @DataProvider(name = "findAndHoldDatasource")
    public Object[][] findAndHoldDatasource() {
        return new Object[][]{
                {0, null, null, "test@test.com", "numSeats must be greater than 0"},
                {1, null, null, "bademail", "customerEmail \"bademail\" is not valid"},
                {1, Optional.of(4), null, "test@test.com", "venueLevel 4 is not part of venue configuration."},
                {1, Optional.of(1), Optional.of(4), "test@test.com", "venueLevel 4 is not part of venue configuration."},
                {1, Optional.of(2), Optional.of(1), "test@test.com", "minLevel " + 2 +
                        " cannot be greater than or equal to maxLevel " + 1}
        };
    }

    @Test(dataProvider = "findAndHoldDatasource")
    public void testFindAndHoldSeatsBadParameters(int numSeats, Optional<Integer> min, Optional<Integer> max, String email, String errorMessage)
            throws Exception {
        assertThatThrownBy(() -> testTicketService.findAndHoldSeats(numSeats, min, max, email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
        assertThat(testTicketService.calledCleanUp).isFalse();
        assertThat(testTicketService.calledFreeUp).isFalse();
        assertThat(testTicketService.calledDoFindAndHoldSeats).isFalse();
    }

    @DataProvider(name = "reserveSeatDatasource")
    public Object[][] resourceSeatDatasource() {
        return new Object[][]{
                {0, "test@test.com", "seatHoldId must be greater than 0."},
                {1, "bademail", "customerEmail \"bademail\" is not valid"}
        };
    }

    @Test(dataProvider = "reserveSeatDatasource")
    public void testReserveSeatsBadParameters(int seatHoldId, String customerEmail, String errorMessage) throws Exception {
        assertThatThrownBy(() -> testTicketService.reserveSeats(seatHoldId, customerEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
        assertThat(testTicketService.calledCleanUp).isFalse();
        assertThat(testTicketService.calledFreeUp).isFalse();
        assertThat(testTicketService.calledDoReserveSeats).isFalse();
    }

    private void alwaysAssert() {
        assertThat(testTicketService.calledCleanUp).isTrue();
        assertThat(testTicketService.calledFreeUp).isTrue();
    }

    private class TestTicketService extends AbstractTicketService {

        private boolean calledCleanUp = false;
        private boolean calledFreeUp = false;
        private boolean calledDoNumberSeatsAvailable = false;
        private boolean calledDoFindAndHoldSeats = false;
        private boolean calledDoReserveSeats = false;

        public TestTicketService(VenueConfiguration venueConfiguration) {
            super(venueConfiguration);
        }

        @Override
        protected Iterable<SeatHold> cleanUpExpiredSeatHolds() {
            calledCleanUp = true;
            return null;
        }

        @Override
        protected void freeUpSeats(Iterable<SeatHold> seatHolds) {
            calledFreeUp = true;
        }

        @Override
        protected int doNumSeatsAvailable(Optional<Integer> venueLevel) {
            calledDoNumberSeatsAvailable = true;
            return 0;
        }

        @Override
        protected SeatHold doFindAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel, String customerEmail) {
            calledDoFindAndHoldSeats = true;
            return null;
        }

        @Override
        protected String doReserveSeats(int seatHoldId, String customerEmail) {
            calledDoReserveSeats = true;
            return null;
        }

    }
}