package hu.unideb.inf.game;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import hu.unideb.inf.state.Board;
import hu.unideb.inf.state.Vehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleAITest {

    @Test
    void makesAValidMoveWhenOneIsAvailable() {
        Board board = new Board(1, 3);
        Vehicle vehicle = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(0, 0));
        board.addVehicle(vehicle);

        SimpleAI ai = new SimpleAI();

        assertTrue(ai.makeMove(board));
        assertEquals(new Coordinate(0, 1), vehicle.getTopLeft());
    }

    @Test
    void returnsFalseWhenNoVehicleCanMove() {
        Board board = new Board(1, 2);
        Vehicle vehicle = new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(0, 0));
        board.addVehicle(vehicle);

        SimpleAI ai = new SimpleAI();

        assertFalse(ai.makeMove(board));
        assertEquals(new Coordinate(0, 0), vehicle.getTopLeft());
    }
}