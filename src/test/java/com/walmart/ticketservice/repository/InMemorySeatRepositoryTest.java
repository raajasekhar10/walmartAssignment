package com.walmart.ticketservice.repository;

import com.google.common.collect.Sets;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.walmart.ticketservice.SeatCounterAnswer;
import com.walmart.ticketservice.TestUtils;
import com.walmart.ticketservice.exception.SeatRepositoryException;
import com.walmart.ticketservice.model.Level;
import com.walmart.ticketservice.model.Seat;
import com.walmart.ticketservice.model.Status;
import com.walmart.ticketservice.model.VenueConfiguration;
import com.walmart.ticketservice.utils.SeatScorer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test of a venue with configuration
 *
 * Level 1 has 2 rows with 4 seats per row. xxxx xxxx
 *
 * Level 2 has 2 rows with 6 seats per row. xxxxxx xxxxxx
 */
public class InMemorySeatRepositoryTest {

    private SeatScorer seatScorer;
    private SeatRepository seatRepository;

    private VenueConfiguration venueConfiguration;

    private Level level1 = TestUtils.createLevel(1, 2, 4);

    private Level level2 = TestUtils.createLevel(2, 2, 6);

    @BeforeMethod
    public void setUp() throws Exception {
        venueConfiguration = new VenueConfiguration(TestUtils.HOLD_LIMIT, Sets.newHashSet(level1, level2));
        seatScorer = mock(SeatScorer.class);
        when(seatScorer.computeScore(any(Seat.class))).thenAnswer(new SeatCounterAnswer());

        seatRepository = new InMemorySeatRepository(venueConfiguration, seatScorer);
    }

    @Test
    public void testFindAll() {
        assertThat(seatRepository.findAll().count()).isEqualTo(20);
    }

    @Test
    public void testFindInLevel() {
        assertThat(seatRepository.findAll(1).count()).isEqualTo(8);
    }

    @Test
    public void testFindInLevelOutsideConfig() {
        assertThat(seatRepository.findAll(100).count()).isEqualTo(0);
    }

    @Test
    public void testFindInStatus() {
        assertThat(seatRepository.findAll(Status.AVAILABLE).count()).isEqualTo(20);
    }

    @Test
    public void testFindInStatusNone() {
        assertThat(seatRepository.findAll(Status.RESERVED).count()).isEqualTo(0);
    }

    @DataProvider(name = "badLevelsDatasource")
    public Object[][] badLevelsDatasource() {
        return new Object[][]{
                {1, 1},
                {2, 1}
        };
    }

    @Test(expectedExceptions = IllegalArgumentException.class, dataProvider = "badLevelsDatasource")
    public void testBadRequestLevels(int minLevel, int maxLevel) {
        seatRepository.findAll(minLevel, maxLevel);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, dataProvider = "badLevelsDatasource")
    public void testBadRequestLevelsWithStatus(int minLevel, int maxLevel) {
        seatRepository.findAll(minLevel, maxLevel, Status.AVAILABLE);
    }

    @Test
    public void testFindAllInRange() {
        assertThat(seatRepository.findAll(1, 2).count()).isEqualTo(20);
    }

    @Test
    public void testFindBest() {
        Seat expectedSeat = new Seat(1, 1, 1);

        Seat actualSeat = seatRepository.findBest(1).limit(1).findFirst().get();
        assertThat(actualSeat).isEqualTo(expectedSeat);
    }

    @Test
    public void testFindBestInRange() {
        Seat expectedSeat = new Seat(1, 1, 1);

        Seat actualSeat = seatRepository.findBest(1, 2).limit(1).findFirst().get();
        assertThat(actualSeat).isEqualTo(expectedSeat);
    }

    @Test
    public void testSave() {
        Seat seat = TestUtils.createSeat(1, 1, 1);
        seat.setScore(1);
        seat.setStatus(Status.RESERVED);

        seatRepository.save(seat);

        Seat savedSeat = seatRepository.findAll(Status.RESERVED).findFirst().get();
        assertThat(savedSeat).isEqualTo(seat);
    }

    @Test(expectedExceptions = SeatRepositoryException.class)
    public void testSaveBadSeat() {
        //Level 100
        Seat seat = TestUtils.createSeat(100, 1, 1);
        seat.setScore(1);
        seat.setStatus(Status.RESERVED);

        seatRepository.save(seat);
    }

}