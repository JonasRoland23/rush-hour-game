package hu.unideb.inf.state;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    void returnsOccupiedCoordinatesForHorizontalVehicle() {
        Vehicle vehicle = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(1, 1));

        assertEquals(2, vehicle.getOccupiedCoordinates().size());
        assertTrue(vehicle.getOccupiedCoordinates().contains(new Coordinate(1, 1)));
        assertTrue(vehicle.getOccupiedCoordinates().contains(new Coordinate(1, 2)));
    }

    @Test
    void returnsOccupiedCoordinatesForVerticalVehicle() {
        Vehicle vehicle = new Vehicle("B", 3, Orientation.VERTICAL, new Coordinate(0, 2));

        assertEquals(3, vehicle.getOccupiedCoordinates().size());
        assertTrue(vehicle.getOccupiedCoordinates().contains(new Coordinate(0, 2)));
        assertTrue(vehicle.getOccupiedCoordinates().contains(new Coordinate(1, 2)));
        assertTrue(vehicle.getOccupiedCoordinates().contains(new Coordinate(2, 2)));
    }

    @Test
    void movesHorizontalVehicleInBothDirections() {
        Vehicle vehicle = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(1, 1));

        vehicle.move(2);
        assertEquals(new Coordinate(1, 3), vehicle.getTopLeft());

        vehicle.move(-1);
        assertEquals(new Coordinate(1, 2), vehicle.getTopLeft());
    }

    @Test
    void movesVerticalVehicleInBothDirections() {
        Vehicle vehicle = new Vehicle("B", 2, Orientation.VERTICAL, new Coordinate(1, 1));

        vehicle.move(2);
        assertEquals(new Coordinate(3, 1), vehicle.getTopLeft());

        vehicle.move(-1);
        assertEquals(new Coordinate(2, 1), vehicle.getTopLeft());
    }

    @Test
    void checksWhetherVehicleOccupiesCoordinate() {
        Vehicle vehicle = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(1, 1));

        assertTrue(vehicle.occupies(new Coordinate(1, 1)));
        assertTrue(vehicle.occupies(new Coordinate(1, 2)));
        assertFalse(vehicle.occupies(new Coordinate(2, 1)));
    }

    @Test
    void exposesPropertiesAndComparesVehiclesById() {
        Vehicle first = new Vehicle("B", 2, Orientation.VERTICAL, new Coordinate(0, 0));
        Vehicle sameId = new Vehicle("B", 3, Orientation.HORIZONTAL, new Coordinate(2, 2));
        Vehicle differentId = new Vehicle("C", 2, Orientation.VERTICAL, new Coordinate(0, 0));

        assertEquals("B", first.getId());
        assertEquals(2, first.getLength());
        assertEquals(Orientation.VERTICAL, first.getOrientation());
        assertEquals(first, sameId);
        assertEquals(first.hashCode(), sameId.hashCode());
        assertNotEquals(first, differentId);

        first.setTopLeft(new Coordinate(4, 4));
        assertEquals(new Coordinate(4, 4), first.getTopLeft());
    }
}