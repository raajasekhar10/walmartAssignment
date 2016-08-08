package com.walmart.ticketservice.service;

import com.google.common.base.Preconditions;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.VenueConfiguration;
import com.walmart.ticketservice.utils.TicketServiceUtils;

import java.util.Optional;

/**
 * Enforces a simple template pattern on all methods defined in the {@link TicketService}.
 *
 * The template is as follows:
 * <ol>
 *     <li>Validation of request parameters</li>
 *     <li>Validation of venue levels (if requested)</li>
 *     <li>Cleanup of any expired seat holds</li>
 *     <li>Free up seats.</li>
 *     <li>Perform requested operation</li>
 * </ol>
 */
public abstract class AbstractTicketService implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTicketService.class);

    protected VenueConfiguration venueConfiguration;

    public AbstractTicketService(VenueConfiguration venueConfiguration) {
        this.venueConfiguration = venueConfiguration;
    }

    @Override
    public final int numSeatsAvailable(Optional<Integer> venueLevel) {
        Optional<Integer> actualVenueLevel = TicketServiceUtils.maybeNull(venueLevel);
        actualVenueLevel.ifPresent(this::validateVenueLevel);
        deleteExpiredSeatHoldsAndFree();
        return doNumSeatsAvailable(actualVenueLevel);
    }

    @Override
    public final SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel, String customerEmail) {
        Preconditions.checkArgument(numSeats > 0, "numSeats must be greater than 0");

        Optional<Integer> actualMinLevel = TicketServiceUtils.maybeNull(minLevel);
        Optional<Integer> actualMaxLevel = TicketServiceUtils.maybeNull(maxLevel);
        actualMinLevel.ifPresent(this::validateVenueLevel);
        actualMaxLevel.ifPresent(this::validateVenueLevel);

        if (actualMinLevel.isPresent() && actualMaxLevel.isPresent()) {
            int min = actualMinLevel.get();
            int max = actualMaxLevel.get();
            TicketServiceUtils.checkRequestedLevels(venueConfiguration, min, max);
        }

        validateCustomerEmail(customerEmail);
        deleteExpiredSeatHoldsAndFree();
        return doFindAndHoldSeats(numSeats, actualMinLevel, actualMaxLevel, customerEmail);
    }

    @Override
    public final String reserveSeats(int seatHoldId, String customerEmail) {
        Preconditions.checkArgument(seatHoldId > 0, "seatHoldId must be greater than 0.");
        validateCustomerEmail(customerEmail);
        deleteExpiredSeatHoldsAndFree();
        return doReserveSeats(seatHoldId, customerEmail);
    }

    protected void validateVenueLevel(int venueLevel) {
        Preconditions.checkArgument(TicketServiceUtils.isValidVenueLevel(venueConfiguration, venueLevel),
                "venueLevel " + venueLevel + " is not part of venue configuration.");
    }

    /**
     * Find all the {@link SeatHold} that have exceeded the hold limit provided in the venue
     * configuration and remove/expire them.
     */
    protected abstract Iterable<SeatHold> cleanUpExpiredSeatHolds();


    /**
     * Implementations must set the status of all seats associated with this iterable
     * to Status.AVAILABLE and save it.
     * @param seatHolds
     */
    protected abstract void freeUpSeats(Iterable<SeatHold> seatHolds);

    private void deleteExpiredSeatHoldsAndFree() {
        Iterable<SeatHold> expiredSeatHolds = cleanUpExpiredSeatHolds();
        freeUpSeats(expiredSeatHolds);
    }

    protected abstract int doNumSeatsAvailable(Optional<Integer> venueLevel);

    protected abstract SeatHold doFindAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel, String customerEmail);

    protected abstract String doReserveSeats(int seatHoldId, String customerEmail);

    protected void validateCustomerEmail(String customerEmail) {
        Preconditions.checkArgument(EmailValidator.getInstance().isValid(customerEmail), "customerEmail \"" + customerEmail + "\" is not valid");
    }
}