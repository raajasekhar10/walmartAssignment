package com.walmart.ticketservice.repository;

import com.google.common.collect.Sets;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.walmart.ticketservice.TestUtils;
import com.walmart.ticketservice.model.SeatHold;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemorySeatHoldRepositoryTest {

    private SeatHoldRepository seatHoldRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        seatHoldRepository = new InMemorySeatHoldRepository();

    }

    @Test
    public void testSaveAndFind() {
        SeatHold seatHold = new SeatHold(1, TestUtils.EMAIL, Sets.newHashSet(TestUtils.createSeat(1, 1, 1)));
        seatHoldRepository.save(seatHold);
        assertThat(seatHoldRepository.find(1).isPresent()).isTrue();
    }

    @Test
    public void testUpdate() {
        SeatHold seatHold = new SeatHold(1, TestUtils.EMAIL, Sets.newHashSet(TestUtils.createSeat(1, 1, 1)));
        SeatHold saved = seatHoldRepository.save(seatHold);
        SeatHold toUpdate = new SeatHold(saved.getId(), saved.getCustomerEmail(), saved.getHeldSeats(), "test", saved.getHoldTime());
        seatHoldRepository.save(toUpdate);

        saved = seatHoldRepository.find(1).get();
        assertThat(saved.getConfirmationCode().get()).isEqualTo("test");
    }

    @Test
    public void testFindNotFound() {
        assertThat(seatHoldRepository.find(1).isPresent()).isFalse();
    }

    @Test
    public void testFindAllExpired() {
        SeatHold seatHold1 = new SeatHold(1, TestUtils.EMAIL, Sets.newHashSet(TestUtils.createSeat(1, 1, 1)));
        SeatHold seatHold2 = new SeatHold(2, TestUtils.EMAIL, Sets.newHashSet(TestUtils.createSeat(2, 1, 1)),
                LocalDateTime.now().minusSeconds(10));
        seatHoldRepository.save(seatHold1);
        seatHoldRepository.save(seatHold2);

        Set<SeatHold> expiredSeatHolds = seatHoldRepository.findAllExpired(5).collect(Collectors.toSet());
        assertThat(expiredSeatHolds).hasSize(1);
        assertThat(expiredSeatHolds.stream().findFirst().get()).isEqualTo(seatHold2);
    }


    @Test
    public void testDelete() {
        SeatHold seatHold1 = new SeatHold(1, TestUtils.EMAIL, Sets.newHashSet(TestUtils.createSeat(1, 1, 1)));
        seatHoldRepository.save(seatHold1);

        seatHoldRepository.delete(seatHold1);
        assertThat(seatHoldRepository.find(1).isPresent()).isFalse();
    }
}