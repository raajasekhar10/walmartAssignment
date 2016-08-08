package com.walmart.ticketservice.repository;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.walmart.ticketservice.exception.SeatRepositoryException;
import com.walmart.ticketservice.model.Seat;
import com.walmart.ticketservice.model.Status;
import com.walmart.ticketservice.model.VenueConfiguration;
import com.walmart.ticketservice.utils.BasicSeatScorer;
import com.walmart.ticketservice.utils.SeatComparator;
import com.walmart.ticketservice.utils.SeatScorer;
import com.walmart.ticketservice.utils.TicketServiceUtils;

/**
 * This implementation initializes the set of seats by reading the venue configuration.
 */
public class InMemorySeatRepository implements SeatRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemorySeatRepository.class);

    private VenueConfiguration venueConfiguration;
    private Set<Seat> seats = null;
    private Comparator<Seat> seatComparator = null;
    private SeatScorer seatScorer;

    public InMemorySeatRepository(VenueConfiguration venueConfiguration) {
        this(venueConfiguration, new SeatComparator(), new BasicSeatScorer(venueConfiguration));
    }

    public InMemorySeatRepository(VenueConfiguration venueConfiguration, Comparator<Seat> seatComparator) {
        this(venueConfiguration, seatComparator, new BasicSeatScorer(venueConfiguration));
    }

    public InMemorySeatRepository(VenueConfiguration venueConfiguration, SeatScorer seatScorer) {
        this(venueConfiguration, new SeatComparator(), seatScorer);
    }

    public InMemorySeatRepository(VenueConfiguration venueConfiguration, Comparator<Seat> seatComparator, SeatScorer seatScorer) {
        this.venueConfiguration = venueConfiguration;
        this.seatComparator = seatComparator;
        this.seatScorer = seatScorer;
        seats = Sets.newLinkedHashSet();
        init();
    }

    @Override
    public Stream<Seat> findAll() {
        return filter(s -> true);
    }

    @Override
    public Stream<Seat> findAll(Status status) {
        return filter(inStatus(status));
    }

    @Override
    public Stream<Seat> findAll(int level) {
        return filter(inLevel(level));
    }

    @Override
    public Stream<Seat> findAll(int level, Status status) {
        return filter(inLevel(level)
                .and(inStatus(status)));
    }

    @Override
    public Stream<Seat> findAll(int minLevel, int maxLevel) {
        TicketServiceUtils.checkRequestedLevels(minLevel, maxLevel);
        return filter(inRange(minLevel, maxLevel));

    }

    @Override
    public Stream<Seat> findAll(int minLevel, int maxLevel, Status status) {
        TicketServiceUtils.checkRequestedLevels(minLevel, maxLevel);
        return filter(inRange(minLevel, maxLevel)
                .and(inStatus(status)));
    }

    @Override
    public Stream<Seat> findBest(int level) {
        return findAll(level, Status.AVAILABLE)
                .sorted(seatComparator);
    }

    @Override
    public Stream<Seat> findBest(int minLevel, int maxLevel) {
        return findAll(minLevel, maxLevel, Status.AVAILABLE)
                .sorted(seatComparator);
    }

    @Override
    public void save(Seat seat) {
        Preconditions.checkNotNull(seat, "seat cannot be null");
        save(Lists.newArrayList(seat));
    }

    @Override
    public void save(Iterable<Seat> seats) throws SeatRepositoryException {
        Preconditions.checkNotNull(seats, "seats cannot be null");
        final List<Seat> badSeats = Lists.newArrayList();
        seats.forEach(s -> {
            boolean inBackingSet = this.seats.contains(s);
            if (!inBackingSet) {
                badSeats.add(s);
            }
        });

        if (badSeats.isEmpty()) {
            seats.forEach(s -> {
                //Since the mutable properties of seat can change force a remove and then add.
                this.seats.remove(s);
                this.seats.add(s);
            });
        } else {
            throw new SeatRepositoryException("1 or more seats did not belong in the original set.", badSeats);
        }
    }


    private Predicate<Seat> inLevel(int level) {
        return s -> s.getLevel() == level;
    }

    private Predicate<Seat> inStatus(Status status) {
        return s -> s.getStatus().equals(status);
    }

    private Predicate<Seat> inRange(int minLevel, int maxLevel) {
        return s -> Range.closed(minLevel, maxLevel).contains(s.getLevel());
    }

    private Stream<Seat> streamAll() {
        return seats.stream()
                .map(s -> new Seat(s.getLevel(), s.getRow(), s.getNumber(), s.getScore(), s.getStatus()));
    }

    private Stream<Seat> filter(Predicate<Seat> filter) {
        return streamAll()
                .filter(filter);
    }

    private void init() {
        //Create the seats by iterating through all levels and rows and applying the seat score after
        venueConfiguration.getLevels().forEach(l -> {
            for (int i = 1; i <= l.getRows(); i++) {
                for (int j = 1; j <= l.getSeatsPerRow(); j++) {
                    Seat seat = new Seat(l.getId(), i, j);
                    int score = seatScorer.computeScore(seat);
                    seat.setScore(score);
                    seats.add(seat);
                }
            }
        });
    }
}
