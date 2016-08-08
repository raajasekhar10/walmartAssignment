package com.walmart.ticketservice.service;

import com.google.common.collect.Sets;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;
import java.util.Set;

import com.walmart.ticketservice.TestUtils;
import com.walmart.ticketservice.exception.NoAvailableSeatsException;
import com.walmart.ticketservice.exception.SeatHoldException;
import com.walmart.ticketservice.model.Seat;
import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.Status;
import com.walmart.ticketservice.model.VenueConfiguration;
import com.walmart.ticketservice.repository.SeatHoldRepository;
import com.walmart.ticketservice.repository.SeatRepository;
import com.walmart.ticketservice.utils.ConfirmationCodeGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


/**
 *
 */
public class SimpleTicketServiceTest {

    VenueConfiguration venueConfiguration = TestUtils.VENUE_CONFIGURATION;
    SeatRepository seatRepository;
    SeatHoldRepository seatHoldRepository;
    ConfirmationCodeGenerator confirmationCodeGenerator;

    SimpleTicketService simpleTicketService;

    SeatHold seatHold1;
    SeatHold seatHold2;
    Seat seat1;
    Seat seat2;
    Seat seat3;

    Set<Seat> seats;

    @BeforeMethod
    public void setUp() throws Exception {
        seatRepository = mock(SeatRepository.class);
        seatHoldRepository = mock(SeatHoldRepository.class);
        confirmationCodeGenerator = mock(ConfirmationCodeGenerator.class);
        simpleTicketService = new SimpleTicketService(venueConfiguration, seatRepository, seatHoldRepository, confirmationCodeGenerator);

        seat1 = TestUtils.createSeat(1, 1, 1);
        seat2 = TestUtils.createSeat(2, 1, 1);
        seat3 = TestUtils.createSeat(3, 1, 1);
        seats = Sets.newHashSet(seat1, seat2, seat3);
        seatHold1 = new SeatHold(1, TestUtils.EMAIL, Sets.newHashSet(seat1));
        seatHold2 = new SeatHold(2, TestUtils.EMAIL, Sets.newHashSet(seat2));

        when(seatHoldRepository.findAllExpired(venueConfiguration.getHoldLimit())).thenReturn(Sets.newHashSet(seatHold1, seatHold2).stream());

    }

    @Test
    public void testCleanUpExpiredSeatHolds() throws Exception {
        simpleTicketService.cleanUpExpiredSeatHolds();
        verify(seatHoldRepository).findAllExpired(venueConfiguration.getHoldLimit());
        verify(seatHoldRepository).delete(seatHold1);
        verify(seatHoldRepository).delete(seatHold2);
    }

    @Test
    public void testFreeUpSeats() throws Exception {
        Set<SeatHold> seatHolds = Sets.newHashSet(seatHold1, seatHold2);
        simpleTicketService.freeUpSeats(seatHolds);
        verify(seatRepository).save(anyCollection());
        assertThat(seat1.getStatus()).isEqualTo(Status.AVAILABLE);
        assertThat(seat2.getStatus()).isEqualTo(Status.AVAILABLE);
    }

    @Test
    public void testDoNumSeatsAvailable() throws Exception {
        when(seatRepository.findAll(Status.AVAILABLE)).thenReturn(seats.stream());
        simpleTicketService.doNumSeatsAvailable(Optional.empty());
        verify(seatRepository).findAll(Status.AVAILABLE);
    }

    @Test
    public void testDoNumSeatsAvailableInLevel() throws Exception {
        when(seatRepository.findAll(1, Status.AVAILABLE)).thenReturn(seats.stream());
        simpleTicketService.doNumSeatsAvailable(Optional.of(1));
        verify(seatRepository).findAll(1, Status.AVAILABLE);
    }

    @Test
    public void testDoFindAndHoldSeatsNoLevel() {
        when(seatRepository.findBest(1)).thenReturn(seats.stream());
        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(new SeatHold(1, TestUtils.EMAIL, Sets.newHashSet(seat1)));
        SeatHold result = simpleTicketService.doFindAndHoldSeats(1, Optional.empty(), Optional.empty(), TestUtils.EMAIL);

        assertThat(result.getHeldSeats()).hasSize(1);
        verify(seatRepository).findBest(1);
        verify(seatHoldRepository).save(any(SeatHold.class));

        assertThat(result.getCustomerEmail()).isEqualTo(TestUtils.EMAIL);
        Seat savedSeat = result.getHeldSeats().stream().findAny().get();
        assertThat(savedSeat.getStatus()).isEqualTo(Status.HELD);
    }

    @Test
    public void testDoFindAndHoldSeatsWithMinLevel() {
        when(seatRepository.findBest(1)).thenReturn(seats.stream());
        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(new SeatHold(1, TestUtils.EMAIL, Sets.newHashSet(seat1)));
        SeatHold result = simpleTicketService.doFindAndHoldSeats(1, Optional.of(1), Optional.empty(), TestUtils.EMAIL);

        assertThat(result.getHeldSeats()).hasSize(1);
        verify(seatRepository).findBest(1);
        verify(seatHoldRepository).save(any(SeatHold.class));

        assertThat(result.getCustomerEmail()).isEqualTo(TestUtils.EMAIL);
        Seat savedSeat = result.getHeldSeats().stream().findAny().get();
        assertThat(savedSeat.getStatus()).isEqualTo(Status.HELD);
    }

    @Test
    public void testDoFindAndHoldSeatsWithBoth() {
        when(seatRepository.findBest(1, 3)).thenReturn(seats.stream());
        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(new SeatHold(1, TestUtils.EMAIL, Sets.newHashSet(seat1)));
        SeatHold result = simpleTicketService.doFindAndHoldSeats(1, Optional.of(1), Optional.of(3), TestUtils.EMAIL);

        assertThat(result.getHeldSeats()).hasSize(1);
        verify(seatRepository).findBest(1, 3);
        verify(seatHoldRepository).save(any(SeatHold.class));

        assertThat(result.getCustomerEmail()).isEqualTo(TestUtils.EMAIL);
        Seat savedSeat = result.getHeldSeats().stream().findAny().get();
        assertThat(savedSeat.getStatus()).isEqualTo(Status.HELD);
    }

    @Test
    public void testDoFindAndHoldSeatsNoSeats() {
        Set<Seat> emptySet = Sets.newHashSet();
        when(seatRepository.findBest(1)).thenReturn(emptySet.stream());

        assertThatThrownBy(() -> simpleTicketService.doFindAndHoldSeats(1, Optional.empty(), Optional.empty(), TestUtils.EMAIL))
                .isInstanceOf(NoAvailableSeatsException.class);

        verifyZeroInteractions(seatHoldRepository);
    }

    @Test
    public void testDoReserveSeatsNotFound() {
        when(seatHoldRepository.find(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> simpleTicketService.doReserveSeats(1, TestUtils.EMAIL))
                .isInstanceOf(SeatHoldException.class)
                .hasMessage("No seat hold found with id 1");

    }

    @Test
    public void testDoReserveSeats() {
        when(seatHoldRepository.find(1)).thenReturn(Optional.of(seatHold1));
        when(confirmationCodeGenerator.generate()).thenReturn("confirmed");

        simpleTicketService.doReserveSeats(1, TestUtils.EMAIL);

        verify(seatHoldRepository).find(1);
        verify(seatRepository).save(seatHold1.getHeldSeats());
        verify(seatHoldRepository).save(any(SeatHold.class));
    }
}