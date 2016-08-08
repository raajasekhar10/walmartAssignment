package com.walmart.ticketservice.itest;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;
import java.util.Set;

import com.walmart.ticketservice.TestUtils;
import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.Status;
import com.walmart.ticketservice.model.VenueConfiguration;
import com.walmart.ticketservice.repository.InMemorySeatHoldRepository;
import com.walmart.ticketservice.repository.InMemorySeatRepository;
import com.walmart.ticketservice.repository.SeatHoldRepository;
import com.walmart.ticketservice.repository.SeatRepository;
import com.walmart.ticketservice.exception.SeatHoldException;
import com.walmart.ticketservice.service.SimpleTicketService;
import com.walmart.ticketservice.service.TicketService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This integration test will simulate a sequence of calls to the ticket service with expected
 * results. In some cases the test may sleep to allow for expiration.
 *
 * The venue configuration is
 * Level 1:  2 Rows, 10 seats
 * Level 2:  4 Rows, 40 seats
 * Level 3:  2 Rows, 20 seats for a total of
 * 120 seats.
 */
public class SimpleTicketServiceITest {

    private static final Logger logger = LoggerFactory.getLogger(SimpleTicketServiceITest.class);

    protected SeatRepository seatRepository;
    protected SeatHoldRepository seatHoldRepository;
    protected VenueConfiguration venueConfiguration = TestUtils.VENUE_CONFIGURATION;
    protected TicketService ticketService;

    Set<SeatHold> seatHolds = Sets.newHashSet();

    @BeforeMethod
    public void setUp() {
        seatRepository = new InMemorySeatRepository(venueConfiguration);
        seatHoldRepository = new InMemorySeatHoldRepository();
        ticketService = new SimpleTicketService(venueConfiguration, seatRepository, seatHoldRepository);
    }

    @Test
    public void testNumSeatsAvailable() {
        assertThat(ticketService.numSeatsAvailable(Optional.empty())).isEqualTo(120);
    }

    @Test()
    public void testNumSeatsInLevel() {
        assertThat(ticketService.numSeatsAvailable(Optional.of(1))).isEqualTo(20);
    }

    @Test()
    public void testFindAndHoldAcrossLevels() {
        SeatHold seatHold1 = ticketService.findAndHoldSeats(2, Optional.empty(), Optional.empty(), TestUtils.EMAIL);
        seatHolds.add(seatHold1);

        assertThat(seatHold1.getHeldSeats()).hasSize(2);
        final Range<Integer> expectedLevel1Scores = Range.closed(1, 2);
        assertThat(seatHold1.getHeldSeats().stream().allMatch(s -> expectedLevel1Scores.contains(s.getScore()))).isTrue();

        assertThat(ticketService.numSeatsAvailable(Optional.of(1))).isEqualTo(18);

        SeatHold seatHold2 = ticketService.findAndHoldSeats(4, Optional.of(2), Optional.empty(), TestUtils.EMAIL);
        final Range<Integer> expectedLevel2Scores = Range.closed(21, 24);
        assertThat(seatHold2.getHeldSeats().stream().allMatch(s -> expectedLevel2Scores.contains(s.getScore()))).isTrue();
    }

    @Test
    public void testFindAndHoldAndExpire() {
        SeatHold seatHold1 = ticketService.findAndHoldSeats(2, Optional.empty(), Optional.empty(), TestUtils.EMAIL);
        assertThat(ticketService.numSeatsAvailable(Optional.of(1))).isEqualTo(18);

        try {
            //Sleep so hold time can expire
            logger.info("Sleeping for 2 seconds to let time expire on the seat hold {}", seatHold1);
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }

        assertThat(ticketService.numSeatsAvailable(Optional.of(1))).isEqualTo(20);
        seatHold1 = ticketService.findAndHoldSeats(2, Optional.empty(), Optional.empty(), TestUtils.EMAIL);
        assertThat(seatHold1.getHeldSeats()).hasSize(2);
        final Range<Integer> expectedLevel1Scores = Range.closed(1, 2);
        assertThat(seatHold1.getHeldSeats().stream().allMatch(s -> expectedLevel1Scores.contains(s.getScore()))).isTrue();

    }

    @Test
    public void testFindAndHoldNoMoreSeats() {
        ticketService.findAndHoldSeats(20, Optional.of(1), Optional.empty(), TestUtils.EMAIL);
        assertThat(ticketService.numSeatsAvailable(Optional.of(1))).isEqualTo(0);
        assertThatThrownBy(() -> ticketService.findAndHoldSeats(2, Optional.of(1), Optional.empty(), TestUtils.EMAIL))
                .isInstanceOf(com.walmart.ticketservice.exception.NoAvailableSeatsException.class);
    }

    @Test
    public void testFindAndHoldMinLevel() {
        SeatHold seatHold = ticketService.findAndHoldSeats(2, Optional.of(2), Optional.empty(), TestUtils.EMAIL);
        assertThat(ticketService.numSeatsAvailable(Optional.of(2))).isEqualTo(78);

        final Range<Integer> expectedLevel1Scores = Range.closed(21, 22);
        assertThat(seatHold.getHeldSeats().stream().allMatch(s -> expectedLevel1Scores.contains(s.getScore()))).isTrue();
    }

    @Test
    public void testFindAndHoldMaxOnly() {
        SeatHold seatHold1 = ticketService.findAndHoldSeats(1, Optional.empty(), Optional.of(2), TestUtils.EMAIL);

        final Range<Integer> expectedLevel1Scores = Range.closed(1, 2);
        assertThat(seatHold1.getHeldSeats().stream().allMatch(s -> expectedLevel1Scores.contains(s.getScore()))).isTrue();
    }

    @Test
    public void testFindAndHoldInSameLevel() {
        SeatHold seatHold1 = ticketService.findAndHoldSeats(2, Optional.of(1), Optional.empty(), TestUtils.EMAIL);
        SeatHold seatHold2 = ticketService.findAndHoldSeats(2, Optional.of(1), Optional.empty(), TestUtils.EMAIL);

        final Range<Integer> expectedScores1 = Range.closed(1, 2);
        assertThat(seatHold1.getHeldSeats().stream().allMatch(s -> expectedScores1.contains(s.getScore()))).isTrue();

        final Range<Integer> expectedScores2 = Range.closed(3, 4);
        assertThat(seatHold2.getHeldSeats().stream().allMatch(s -> expectedScores2.contains(s.getScore()))).isTrue();
    }

    @Test
    public void testSingleFindAndHoldAcrossLevels() {
        SeatHold seatHold1 = ticketService.findAndHoldSeats(100, Optional.of(2), Optional.of(3), TestUtils.EMAIL);
        final Range<Integer> expectedLevel1Scores = Range.closed(21, 120);
        assertThat(seatHold1.getHeldSeats().stream().allMatch(s -> expectedLevel1Scores.contains(s.getScore()))).isTrue();
    }

    @Test
    public void testReserveNoSeatHold() {
        assertThatThrownBy(() -> ticketService.reserveSeats(1, TestUtils.EMAIL))
                .isInstanceOf(SeatHoldException.class);
    }

    @Test
    public void testReserve() {
        SeatHold seatHold = ticketService.findAndHoldSeats(2, Optional.of(2), Optional.empty(), TestUtils.EMAIL);
        String confirmationCode = ticketService.reserveSeats(seatHold.getId(), TestUtils.EMAIL);

        assertThat(confirmationCode).isNotEmpty();

        SeatHold reservation = seatHoldRepository.find(seatHold.getId()).get();
        assertThat(reservation.getId()).isEqualTo(seatHold.getId());
        assertThat(reservation.getCustomerEmail()).isEqualTo(TestUtils.EMAIL);
        reservation.getHeldSeats().forEach(s -> assertThat(s.getStatus()).isEqualTo(Status.RESERVED));
        assertThat(reservation.getConfirmationCode().get()).isEqualTo(confirmationCode);
        assertThat(ticketService.numSeatsAvailable(Optional.of(2))).isEqualTo(78);
    }
}
