package hu.unideb.inf.state;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vehicle {
    private final String id;
    private final int length;
    private final Orientation orientation;
    private Coordinate topLeft;

    public Vehicle(String id, int length, Orientation orientation, Coordinate topLeft) {
        this.id = id;
        this.length = length;
        this.orientation = orientation;
        this.topLeft = topLeft;
    }

    public String getId() { return id; }
    public int getLength() { return length; }
    public Orientation getOrientation() { return orientation; }
    public Coordinate getTopLeft() { return topLeft; }
    public void setTopLeft(Coordinate topLeft) { this.topLeft = topLeft; }

    public List<Coordinate> getOccupiedCoordinates() {
        List<Coordinate> coords = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (orientation == Orientation.HORIZONTAL) {
                coords.add(new Coordinate(topLeft.row(), topLeft.col() + i));
            } else {
                coords.add(new Coordinate(topLeft.row() + i, topLeft.col()));
            }
        }
        return coords;
    }

    public boolean occupies(Coordinate coord) {
        return getOccupiedCoordinates().contains(coord);
    }

    public void move(int steps) {
        if (orientation == Orientation.HORIZONTAL) {
            this.topLeft = new Coordinate(topLeft.row(), topLeft.col() + steps);
        } else {
            this.topLeft = new Coordinate(topLeft.row() + steps, topLeft.col());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}