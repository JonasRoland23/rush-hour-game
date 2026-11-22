package hu.unideb.inf.state;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleTest {

    @Test
    void testGetOccupiedCoordinatesShouldReturnAllCellsForHorizontalAndVertical() {
        Vehicle horizontal = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(1, 1));
        Vehicle vertical = new Vehicle("B", 3, Orientation.VERTICAL, new Coordinate(0, 2));

        assertEquals(2, horizontal.getOccupiedCoordinates().size());
        assertTrue(horizontal.occupies(new Coordinate(1, 1)));
        assertTrue(horizontal.occupies(new Coordinate(1, 2)));
        assertFalse(horizontal.occupies(new Coordinate(2, 1)));

        assertEquals(3, vertical.getOccupiedCoordinates().size());
        assertTrue(vertical.occupies(new Coordinate(0, 2)));
        assertTrue(vertical.occupies(new Coordinate(1, 2)));
        assertTrue(vertical.occupies(new Coordinate(2, 2)));
    }

    @Test
    void testMovedShouldReturnNewImmutableInstanceWithoutMutatingOriginal() {
        Vehicle original = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(1, 1));

        Vehicle forward = original.moved(2);
        Vehicle backward = forward.moved(-1);

        assertEquals(new Coordinate(1, 1), original.topLeft());
        assertEquals(new Coordinate(1, 3), forward.topLeft());
        assertEquals(new Coordinate(1, 2), backward.topLeft());
    }

    @Test
    void testValueObjectEqualityHashCodeAndToString() {
        Vehicle v1 = new Vehicle("A", 2, Orientation.VERTICAL, new Coordinate(0, 0));
        Vehicle v2 = new Vehicle("A", 2, Orientation.VERTICAL, new Coordinate(0, 0));
        Vehicle v3 = new Vehicle("A", 2, Orientation.VERTICAL, new Coordinate(1, 0));
        Vehicle v4 = new Vehicle("B", 2, Orientation.VERTICAL, new Coordinate(0, 0));

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1, v3);
        assertNotEquals(v1, v4);
        assertTrue(v1.toString().contains("A"));
        assertThrows(UnsupportedOperationException.class, () -> v1.getOccupiedCoordinates().clear());
    }

    @Test
    void testConstructorShouldRejectInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("A", 1, Orientation.HORIZONTAL, new Coordinate(0, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(" ", 2, Orientation.HORIZONTAL, new Coordinate(0, 0)));
        assertThrows(NullPointerException.class,
                () -> new Vehicle("A", 2, Orientation.HORIZONTAL, null));
    }
}
