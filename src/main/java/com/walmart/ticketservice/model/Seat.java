package com.walmart.ticketservice.model;

import com.google.common.base.MoreObjects;

/**
 * A seat within the venue.  The score represents a subjective value for a given seat.
 */
public class Seat {
    private int level;
    private int row;
    private int number;
    private int score;
    private Status status = Status.AVAILABLE;

    public Seat(int level, int row, int number) {
        this.level = level;
        this.row = row;
        this.number = number;
    }

    public Seat(int level, int row, int number, int score) {
        this.level = level;
        this.row = row;
        this.number = number;
        this.score = score;
    }

    public Seat(int level, int row, int number, int score, Status status) {
        this.level = level;
        this.row = row;
        this.number = number;
        this.score = score;
        this.status = status;
    }

    public int getLevel() {
        return level;
    }

    public int getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Seat seat = (Seat) o;

        if (level != seat.level) return false;
        if (row != seat.row) return false;
        return number == seat.number;

    }

    @Override
    public int hashCode() {
        int result = level;
        result = 31 * result + row;
        result = 31 * result + number;
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("level", level)
                .add("row", row)
                .add("number", number)
                .add("score", score)
                .add("status", status)
                .toString();
    }

}
