package hu.unideb.inf.state;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void testGetVehiclesShouldReturnUnmodifiableSnapshot() {
        Board board = new Board(4, 4);
        board.addVehicle(new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(0, 0)));

        assertThrows(UnsupportedOperationException.class, () -> board.getVehicles().clear());
    }

    @Test
    void testMoveShouldBeRejectedWhenPathIsBlocked() {
        // Given
        Board board = new Board(6, 6);
        Vehicle moving = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(2, 0));
        Vehicle obstacle = new Vehicle("B", 2, Orientation.VERTICAL, new Coordinate(1, 2));
        board.addVehicle(moving);
        board.addVehicle(obstacle);

        // When
        boolean canMove = board.canMove(moving, 1);

        // Then
        assertFalse(canMove);
    }

    @Test
    void testCanMoveShouldRejectZeroStepsAndOutOfBounds() {
        Board board = new Board(6, 6);
        Vehicle vehicle = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(2, 2));
        board.addVehicle(vehicle);

        assertFalse(board.canMove(vehicle, 0));
        assertFalse(board.canMove(vehicle, -3));
        assertFalse(board.canMove(vehicle, 3));
        assertFalse(board.canMove(null, 1));
    }

    @Test
    void testTargetVehicle1ShouldWinOnlyThroughRightExit() {
        // Given
        Board board = new Board(3, 6);
        Vehicle humanTarget = new Vehicle("1", 2, Orientation.HORIZONTAL, new Coordinate(1, 2));
        Vehicle aiTarget = new Vehicle("2", 2, Orientation.HORIZONTAL, new Coordinate(0, 2));
        board.addVehicle(humanTarget);
        board.addVehicle(aiTarget);

        // When & Then: vehicle 1 cannot leave on the left, only on the right exit (1, 6)
        assertFalse(board.canMove(humanTarget, -3));
        assertTrue(board.canMove(humanTarget, 3));
        board.moveVehicle(humanTarget, 3);

        assertEquals("1", board.getWinnerVehicleId().orElseThrow());
    }

    @Test
    void testTargetVehicle2ShouldWinOnlyThroughLeftExit() {
        // Given
        Board board = new Board(3, 6);
        Vehicle humanTarget = new Vehicle("1", 2, Orientation.HORIZONTAL, new Coordinate(0, 2));
        Vehicle aiTarget = new Vehicle("2", 2, Orientation.HORIZONTAL, new Coordinate(1, 2));
        board.addVehicle(humanTarget);
        board.addVehicle(aiTarget);

        // When & Then: vehicle 2 cannot leave on the right, only on the left exit (1, -1)
        assertFalse(board.canMove(aiTarget, 3));
        assertTrue(board.canMove(aiTarget, -3));
        board.moveVehicle(aiTarget, -3);

        assertEquals("2", board.getWinnerVehicleId().orElseThrow());
    }

    @Test
    void testSetExitAndValidationRules() {
        Board board = new Board(4, 5);
        board.addVehicle(new Vehicle("1", 2, Orientation.HORIZONTAL, new Coordinate(2, 1)));
        board.setExit("1", new Coordinate(2, 5));

        assertEquals(new Coordinate(2, 5), board.getExit("1").orElseThrow());
        assertThrows(IllegalArgumentException.class, () -> board.setExit("1", new Coordinate(1, 5)));
        assertThrows(IllegalArgumentException.class, () -> board.setExit("UNKNOWN", new Coordinate(2, 5)));
        assertThrows(IllegalArgumentException.class,
                () -> board.addVehicle(new Vehicle("1", 2, Orientation.HORIZONTAL, new Coordinate(0, 0))));
        assertThrows(IllegalArgumentException.class,
                () -> board.addVehicle(new Vehicle("B", 2, Orientation.VERTICAL, new Coordinate(1, 1))));
    }
}