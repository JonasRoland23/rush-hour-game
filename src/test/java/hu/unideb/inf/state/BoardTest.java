package hu.unideb.inf.state;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void testBoardOperations() {
        Board board = new Board(6, 6);
        assertEquals(6, board.getRows());
        assertEquals(6, board.getCols());

        Vehicle v1 = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(2, 0));
        Vehicle v2 = new Vehicle("C", 2, Orientation.VERTICAL, new Coordinate(1, 3));

        board.addVehicle(v1);
        board.addVehicle(v2);

        assertEquals(2, board.getVehicles().size());
        assertTrue(board.getVehicleById("A").isPresent());
        assertFalse(board.getVehicleById("NONEXISTENT").isPresent());

        assertTrue(board.canMove(v1, 1));
        assertFalse(board.canMove(v1, -1));
        assertFalse(board.canMove(v1, 3));
        assertTrue(board.canMove(v1, 0));
    }
}