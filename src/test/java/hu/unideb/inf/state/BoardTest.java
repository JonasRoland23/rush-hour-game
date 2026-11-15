package hu.unideb.inf.state;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void boardStoresVehiclesAndFindsThemById() {
        Board board = new Board(6, 6);
        Vehicle horizontal = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(2, 0));
        Vehicle vertical = new Vehicle("B", 2, Orientation.VERTICAL, new Coordinate(1, 3));

        board.addVehicle(horizontal);
        board.addVehicle(vertical);

        assertEquals(6, board.getRows());
        assertEquals(6, board.getCols());
        assertEquals(2, board.getVehicles().size());
        assertSame(horizontal, board.getVehicleById("a").orElseThrow());
        assertSame(vertical, board.getVehicleById("B").orElseThrow());
        assertTrue(board.getVehicleById("UNKNOWN").isEmpty());
    }

    @Test
    void canMoveVehicleIntoFreeSpace() {
        Board board = new Board(6, 6);
        Vehicle vehicle = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(2, 2));
        board.addVehicle(vehicle);

        assertTrue(board.canMove(vehicle, 1));
        assertTrue(board.canMove(vehicle, -1));
    }

    @Test
    void cannotMoveVehicleOutsideBoard() {
        Board board = new Board(6, 6);
        Vehicle horizontal = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(2, 0));
        Vehicle vertical = new Vehicle("B", 2, Orientation.VERTICAL, new Coordinate(0, 4));
        board.addVehicle(horizontal);
        board.addVehicle(vertical);

        assertFalse(board.canMove(horizontal, -1));
        assertFalse(board.canMove(vertical, -1));
        assertFalse(board.canMove(horizontal, 5));
    }

    @Test
    void cannotMoveVehicleThroughAnotherVehicle() {
        Board board = new Board(6, 6);
        Vehicle moving = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(2, 0));
        Vehicle obstacle = new Vehicle("B", 2, Orientation.VERTICAL, new Coordinate(1, 2));
        board.addVehicle(moving);
        board.addVehicle(obstacle);

        assertFalse(board.canMove(moving, 1));
    }

    @Test
    void checksVerticalMovement() {
        Board board = new Board(6, 6);
        Vehicle vehicle = new Vehicle("A", 2, Orientation.VERTICAL, new Coordinate(2, 2));
        board.addVehicle(vehicle);

        assertTrue(board.canMove(vehicle, 1));
        assertTrue(board.canMove(vehicle, -1));
    }

    @Test
    void zeroStepsAreAcceptedByTheCurrentMovementCheck() {
        Board board = new Board(6, 6);
        Vehicle vehicle = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(2, 2));
        board.addVehicle(vehicle);

        assertTrue(board.canMove(vehicle, 0));
    }
}