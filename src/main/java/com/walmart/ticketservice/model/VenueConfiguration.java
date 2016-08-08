package com.walmart.ticketservice.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * A simple description of the Venue
 */
public class VenueConfiguration {
    private int holdLimit = 5;
    private Set<Level> levels = Sets.newHashSet();

    public VenueConfiguration(int holdLimit, Set<Level> levels) {
        Preconditions.checkArgument(holdLimit > 0, "holdLimit must be greater than 0");
        Preconditions.checkArgument(!levels.isEmpty(), "levels cannot be empty");
        this.holdLimit = holdLimit;
        this.levels = levels;
    }

    public int getHoldLimit() {
        return holdLimit;
    }

    public Set<Level> getLevels() {
        return levels;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("holdLimit", holdLimit)
                .add("levels", levels)
                .toString();
    }
}
