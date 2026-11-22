package hu.unideb.inf.state;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Vehicle(String id, int length, Orientation orientation, Coordinate topLeft) {

    public Vehicle {
        id = Objects.requireNonNull(id, "A jármű azonosítója nem lehet null.").trim();
        orientation = Objects.requireNonNull(orientation, "A jármű iránya nem lehet null.");
        topLeft = Objects.requireNonNull(topLeft, "A jármű pozíciója nem lehet null.");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("A jármű azonosítója nem lehet üres.");
        }
        if (length < 2) {
            throw new IllegalArgumentException("A jármű hossza legalább 2 mező kell legyen.");
        }
    }

    public Vehicle moved(int steps) {
        if (orientation == Orientation.HORIZONTAL) {
            return new Vehicle(id, length, orientation, topLeft.translate(0, steps));
        }
        return new Vehicle(id, length, orientation, topLeft.translate(steps, 0));
    }

    public List<Coordinate> getOccupiedCoordinates() {
        List<Coordinate> coords = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            if (orientation == Orientation.HORIZONTAL) {
                coords.add(topLeft.translate(0, i));
            } else {
                coords.add(topLeft.translate(i, 0));
            }
        }
        return List.copyOf(coords);
    }

    public boolean occupies(Coordinate coord) {
        return getOccupiedCoordinates().contains(coord);
    }
}
