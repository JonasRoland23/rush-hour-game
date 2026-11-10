package hu.unideb.inf.state;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Board {
    private final int rows;
    private final int cols;
    private final List<Vehicle> vehicles;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.vehicles = new ArrayList<>();
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public List<Vehicle> getVehicles() { return vehicles; }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public Optional<Vehicle> getVehicleById(String id) {
        return vehicles.stream()
                .filter(v -> v.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    public boolean isCellOccupied(Coordinate coord, Vehicle self) {
        return vehicles.stream()
                .filter(v -> !v.equals(self))
                .anyMatch(v -> v.occupies(coord));
    }

    public boolean canMove(Vehicle vehicle, int steps) {
        if (steps == 0) return true;
        int dir = steps > 0 ? 1 : -1;
        int absSteps = Math.abs(steps);

        Coordinate currentTopLeft = vehicle.getTopLeft();
        for (int step = 1; step <= absSteps; step++) {
            Coordinate nextTopLeft = vehicle.getOrientation() == Orientation.HORIZONTAL
                    ? new Coordinate(currentTopLeft.row(), currentTopLeft.col() + (step * dir))
                    : new Coordinate(currentTopLeft.row() + (step * dir), currentTopLeft.col());

            for (int i = 0; i < vehicle.getLength(); i++) {
                Coordinate checkCoord = vehicle.getOrientation() == Orientation.HORIZONTAL
                        ? new Coordinate(nextTopLeft.row(), nextTopLeft.col() + i)
                        : new Coordinate(nextTopLeft.row() + i, nextTopLeft.col());

                if (checkCoord.row() < 0 || checkCoord.row() >= rows ||
                        checkCoord.col() < 0 || checkCoord.col() >= cols) {
                    return false;
                }

                if (isCellOccupied(checkCoord, vehicle)) {
                    return false;
                }
            }
        }
        return true;
    }
}