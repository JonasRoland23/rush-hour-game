package hu.unideb.inf.state;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    void testGetOccupiedCoordinatesHorizontal() {
        Vehicle v = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(1, 1));
        var coords = v.getOccupiedCoordinates();

        assertEquals(2, coords.size());
        assertTrue(coords.contains(new Coordinate(1, 1)));
        assertTrue(coords.contains(new Coordinate(1, 2)));
    }

    @Test
    void testMoveAndGetters() {
        Vehicle v = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(1, 1));
        assertEquals("A", v.getId());
        assertEquals(2, v.getLength());
        assertEquals(Orientation.HORIZONTAL, v.getOrientation());

        v.move(2);
        assertEquals(new Coordinate(1, 3), v.getTopLeft());

        v.setTopLeft(new Coordinate(0, 0));
        assertEquals(new Coordinate(0, 0), v.getTopLeft());
    }

    @Test
    void testVerticalVehicleAndEquals() {
        Vehicle v1 = new Vehicle("B", 2, Orientation.VERTICAL, new Coordinate(0, 0));
        Vehicle v2 = new Vehicle("B", 2, Orientation.VERTICAL, new Coordinate(0, 0));

        assertTrue(v1.occupies(new Coordinate(1, 0)));
        assertFalse(v1.occupies(new Coordinate(0, 1)));

        v1.move(1);
        assertEquals(new Coordinate(1, 0), v1.getTopLeft());

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }
}