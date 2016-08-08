package com.walmart.ticketservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.gmi.junction.StreamUtils;

import com.walmart.ticketservice.exception.NoAvailableSeatsException;
import com.walmart.ticketservice.exception.SeatHoldException;
import com.walmart.ticketservice.model.Seat;
import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.Status;
import com.walmart.ticketservice.model.VenueConfiguration;
import com.walmart.ticketservice.repository.SeatHoldRepository;
import com.walmart.ticketservice.repository.SeatRepository;
import com.walmart.ticketservice.utils.ConfirmationCodeGenerator;
import com.walmart.ticketservice.utils.DefaultConfirmationCodeGenerator;
import com.walmart.ticketservice.utils.TicketServiceUtils;

/**
 * A simple implementation of the {@link TicketService}.
 */
public class SimpleTicketService extends AbstractTicketService {

    private static final Logger logger = LoggerFactory.getLogger(SimpleTicketService.class);

    private SeatRepository seatRepository;
    private SeatHoldRepository seatHoldRepository;
    private ConfirmationCodeGenerator confirmationCodeGenerator = new DefaultConfirmationCodeGenerator();

    public SimpleTicketService(VenueConfiguration venueConfiguration, SeatRepository seatRepository,
                               SeatHoldRepository seatHoldRepository) {
        super(venueConfiguration);
        this.seatRepository = seatRepository;
        this.seatHoldRepository = seatHoldRepository;
    }

    public SimpleTicketService(VenueConfiguration venueConfiguration, SeatRepository seatRepository,
                               SeatHoldRepository seatHoldRepository, ConfirmationCodeGenerator confirmationCodeGenerator) {
        super(venueConfiguration);
        this.seatRepository = seatRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.confirmationCodeGenerator = confirmationCodeGenerator;
    }

    @Override
    protected Iterable<SeatHold> cleanUpExpiredSeatHolds() {
        Iterable<SeatHold> seatHolds = seatHoldRepository.findAllExpired(venueConfiguration.getHoldLimit())
                .collect(Collectors.toSet());
        seatHolds.forEach(s -> {
            logger.info("Deleting seat hold {}", s);
            seatHoldRepository.delete(s);
        });
        return seatHolds;
    }

    @Override
    protected void freeUpSeats(Iterable<SeatHold> seatHolds) {
        Iterable<Seat> availableSeats = StreamUtils.create(seatHolds)
                .flatMap(s -> s.getHeldSeats().stream())
                .peek(s -> s.setStatus(Status.AVAILABLE))
                .collect(Collectors.toSet());
        seatRepository.save(availableSeats);
    }

    @Override
    protected int doNumSeatsAvailable(Optional<Integer> venueLevel) {
        //Map to Supplier functions to either search within a level or entire venue
        //The map function method reference will use findAllAvailable(int)
        //while the orElseGet will be use the no argument version.
        return venueLevel.map(this::findAllAvailable)
                .orElseGet(this::findAllAvailable).get();
    }

    @Override
    protected SeatHold doFindAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel, String customerEmail) {
        int minLevelToUse = minLevel.orElseGet(() -> TicketServiceUtils.getMinVenueLevel(venueConfiguration));
        int maxLevelToUse = maxLevel.orElse(minLevelToUse - 1);
        Stream<Seat> bestSeatsStream;

        //Exclusive search of a particular level
        if (maxLevelToUse < minLevelToUse) {
            logger.debug("Max level not requested by {}. Will only search level {} ", customerEmail, minLevelToUse);
            bestSeatsStream = seatRepository
                    .findBest(minLevelToUse)
                    .limit(numSeats);
        } else {
            bestSeatsStream = seatRepository
                    .findBest(minLevelToUse, maxLevelToUse)
                    .limit(numSeats);
        }

        Set<Seat> bestSeats = bestSeatsStream.collect(Collectors.toSet());

        if (bestSeats.size() < numSeats) {
            throw new NoAvailableSeatsException("Only " + bestSeats.size() + " seats were found and " + numSeats + " were requested.",
                    numSeats, customerEmail, minLevel, maxLevel, bestSeats.size());
        }

        SeatHold seatHold = new SeatHold(customerEmail, bestSeats);
        seatHold = seatHoldRepository.save(seatHold);
        bestSeats.forEach(b -> b.setStatus(Status.HELD));
        seatRepository.save(bestSeats);

        logger.info("Saved seat hold {} with seats {}", seatHold.getId(), bestSeats);
        return seatHold;
    }

    @Override
    protected String doReserveSeats(int seatHoldId, String customerEmail) {
        SeatHold seatHold = seatHoldRepository.find(seatHoldId)
                .orElseThrow(() -> new SeatHoldException("No seat hold found with id " + seatHoldId, seatHoldId, customerEmail));

        seatHold.getHeldSeats().forEach(b -> b.setStatus(Status.RESERVED));
        seatRepository.save(seatHold.getHeldSeats());
        String confirmationCode = confirmationCodeGenerator.generate();

        SeatHold confirmedSeatHold = new SeatHold(seatHold.getId(), customerEmail, seatHold.getHeldSeats(),
                confirmationCode, seatHold.getHoldTime());
        seatHoldRepository.save(confirmedSeatHold);
        logger.info("Reservation complete for seat hold {} with confirmation code {}.", seatHoldId, confirmationCode);
        return confirmationCode;
    }

    protected Supplier<Integer> findAllAvailable() {
        return () -> (int) seatRepository.findAll(Status.AVAILABLE).count();
    }

    protected Supplier<Integer> findAllAvailable(int venueLevel) {
        return () -> (int) seatRepository.findAll(venueLevel, Status.AVAILABLE).count();
    }

}
