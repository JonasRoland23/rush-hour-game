package hu.unideb.inf.state;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class Board {
    private final int rows;
    private final int cols;
    private final List<Vehicle> vehicles;
    private final Map<String, Coordinate> exits;

    public Board(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("A tábla méreteinek pozitívnak kell lenniük.");
        }
        this.rows = rows;
        this.cols = cols;
        this.vehicles = new ArrayList<>();
        this.exits = new HashMap<>();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public List<Vehicle> getVehicles() {
        return List.copyOf(vehicles);
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("A jármű nem lehet null.");
        }
        if (getVehicleById(vehicle.id()).isPresent()) {
            throw new IllegalArgumentException("Már létezik ilyen azonosítójú jármű: " + vehicle.id());
        }
        for (Coordinate coord : vehicle.getOccupiedCoordinates()) {
            if (!isInside(coord)) {
                throw new IllegalArgumentException("A jármű kilóg a tábláról: " + vehicle.id());
            }
            if (isCellOccupied(coord, null)) {
                throw new IllegalArgumentException("A jármű ütközik egy másik járművel: " + vehicle.id());
            }
        }
        if (("1".equals(vehicle.id()) || "2".equals(vehicle.id()))
                && vehicle.orientation() != Orientation.HORIZONTAL) {
            throw new IllegalArgumentException("A céljárműveknek vízszintesnek kell lenniük.");
        }
        vehicles.add(vehicle);
        if ("1".equals(vehicle.id())) {
            exits.put("1", new Coordinate(vehicle.topLeft().row(), cols));
        } else if ("2".equals(vehicle.id())) {
            exits.put("2", new Coordinate(vehicle.topLeft().row(), -1));
        }
    }

    public void setExit(String vehicleId, Coordinate exitCoord) {
        Vehicle vehicle = getVehicleById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Ismeretlen jármű: " + vehicleId));
        if (exitCoord == null) {
            throw new IllegalArgumentException("A kijárat koordinátája nem lehet null.");
        }
        if (vehicle.orientation() == Orientation.HORIZONTAL
                && exitCoord.row() == vehicle.topLeft().row()
                && (exitCoord.col() == -1 || exitCoord.col() == cols)) {
            exits.put(vehicle.id(), exitCoord);
            return;
        }
        throw new IllegalArgumentException("A kijáratnak a céljármű sorában, a tábla szélén kell lennie.");
    }

    public Optional<Coordinate> getExit(String vehicleId) {
        return Optional.ofNullable(exits.get(vehicleId));
    }

    public Optional<Vehicle> getVehicleById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        String trimmed = id.trim();
        return vehicles.stream()
                .filter(v -> v.id().equalsIgnoreCase(trimmed))
                .findFirst();
    }

    public Optional<Vehicle> getVehicleAt(Coordinate coord) {
        if (coord == null) {
            return Optional.empty();
        }
        return vehicles.stream()
                .filter(v -> v.occupies(coord))
                .findFirst();
    }

    public boolean isCellOccupied(Coordinate coord, Vehicle self) {
        return vehicles.stream()
                .filter(v -> self == null || !v.id().equalsIgnoreCase(self.id()))
                .anyMatch(v -> v.occupies(coord));
    }

    public boolean canMove(Vehicle vehicle, int steps) {
        if (vehicle == null || steps == 0) {
            return false;
        }
        Optional<Vehicle> existing = getVehicleById(vehicle.id());
        if (existing.isEmpty()) {
            return false;
        }
        long absSteps = Math.abs((long) steps);
        if (absSteps > Math.max(rows, cols) + 1L) {
            return false;
        }

        int dir = steps > 0 ? 1 : -1;
        Vehicle current = existing.get();
        Coordinate allowedExit = exits.get(current.id());

        for (int step = 1; step <= absSteps; step++) {
            current = current.moved(dir);
            for (Coordinate checkCoord : current.getOccupiedCoordinates()) {
                if (!isInside(checkCoord)) {
                    if (!checkCoord.equals(allowedExit)) {
                        return false;
                    }
                } else if (isCellOccupied(checkCoord, current)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void moveVehicle(Vehicle vehicle, int steps) {
        if (!canMove(vehicle, steps)) {
            throw new IllegalArgumentException("Ez a lépés szabálytalan!");
        }
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).id().equalsIgnoreCase(vehicle.id())) {
                vehicles.set(i, vehicles.get(i).moved(steps));
                return;
            }
        }
    }

    public Optional<String> getWinnerVehicleId() {
        for (Vehicle vehicle : vehicles) {
            Coordinate exit = exits.get(vehicle.id());
            if (exit != null && vehicle.occupies(exit)) {
                return Optional.of(vehicle.id());
            }
        }
        return Optional.empty();
    }

    private boolean isInside(Coordinate coord) {
        return coord.row() >= 0 && coord.row() < rows
                && coord.col() >= 0 && coord.col() < cols;
    }
}
