package com.walmart.ticketservice.repository;

import java.util.stream.Stream;

import com.walmart.ticketservice.exception.SeatRepositoryException;
import com.walmart.ticketservice.model.Seat;
import com.walmart.ticketservice.model.Status;

/**
 * Simple repository for basic CRU operations on a seats.
 */
public interface SeatRepository {

    Stream<Seat> findAll();

    Stream<Seat> findAll(final Status status);

    Stream<Seat> findAll(int level);

    Stream<Seat> findAll(int level, Status status);

    /**
     * Find all seats within the specified range of levels.
     *
     * @return all seats within the levels requested (inclusive)
     * @throws IllegalArgumentException if minLevel is greater than maxLevel or equal to maxLevel
     */
    Stream<Seat> findAll(int minLevel, int maxLevel);

    /**
     * Find all seats within the specified range of levels and in requested status.
     *
     * @param minLevel
     * @param maxLevel
     * @return all seats within the levels requested (inclusive)
     * @throws IllegalArgumentException if minLevel is greater than maxLevel or equal to maxLevel
     */
    Stream<Seat> findAll(int minLevel, int maxLevel, Status status);

    /**
     * Returns an ordered Stream of seats within the provided level sorted by the best seat score.
     *
     * @param level
     * @return
     */
    Stream<Seat> findBest(int level);

    /**
     * Returns an ordered Stream of seats within the provided range of levels, sorted by the best seat score.
     *
     * @param minLevel
     * @param maxLevel
     * @return
     */
    Stream<Seat> findBest(int minLevel, int maxLevel);

    void save(Seat seat) throws SeatRepositoryException;

    void save(Iterable<Seat> seats) throws SeatRepositoryException;

}
