package com.walmart.ticketservice.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.IntStream;

import com.walmart.ticketservice.model.Level;
import com.walmart.ticketservice.model.VenueConfiguration;

public class TicketServiceUtils {
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceUtils.class);
    private TicketServiceUtils() {

    }

    public static boolean isValidVenueLevel(VenueConfiguration venueConfiguration, int level) {
        return venueLevelRange(venueConfiguration).contains(level);
    }

    public static Range<Integer> venueLevelRange(VenueConfiguration venueConfiguration) {
        int minLevel = getMinVenueLevel(venueConfiguration);
        int maxLevel = getMaxVenueLevel(venueConfiguration);
        return Range.closed(minLevel, maxLevel);
    }

    public static void checkRequestedLevels(int minLevel, int maxLevel) {
        Preconditions.checkArgument(minLevel < maxLevel, "minLevel " + minLevel +
                " cannot be greater than or equal to maxLevel " + maxLevel);
    }

    public static int getMinVenueLevel(VenueConfiguration venueConfiguration) {
        return baseLevelStream(venueConfiguration)
                .min().getAsInt();
    }

    public static int getMaxVenueLevel(VenueConfiguration venueConfiguration) {
        return baseLevelStream(venueConfiguration)
                .max().getAsInt();
    }

    public static void checkRequestedLevels(VenueConfiguration venueConfiguration, int minLevel, int maxLevel) {
        checkRequestedLevels(minLevel, maxLevel);
        Range<Integer> venueRange = venueLevelRange(venueConfiguration);
        Preconditions.checkArgument(venueRange.contains(minLevel), "minLevel " + minLevel + " is not part of the venue.");
        Preconditions.checkArgument(venueRange.contains(maxLevel), "maxLevel " + maxLevel + " is not part of the venue.");
    }

    /**
     * If the provided optional is null, return an Optional.empty();
     * @param optional
     * @param <T>
     * @return
     */
    public static <T> Optional<T> maybeNull(Optional<T> optional) {
        return optional != null ? optional : Optional.empty();
    }

    private static IntStream baseLevelStream(VenueConfiguration venueConfiguration) {
        return venueConfiguration.getLevels().stream()
                .mapToInt(Level::getId);
    }

}
