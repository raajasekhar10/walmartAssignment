package com.walmart.ticketservice.model;


import com.google.common.base.MoreObjects;

import java.math.BigDecimal;

public class Level {
    private int id;
    private String name;
    private int rows;
    private int seatsPerRow;
    private BigDecimal price;

    public Level(int id, String name, int rows, int seatsPerRow, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return rows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public int getTotalSeats() {
        return rows * seatsPerRow;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Level level = (Level) o;

        return id == level.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("rows", rows)
                .add("seatsPerRow", seatsPerRow)
                .add("price", price)
                .toString();
    }
}
