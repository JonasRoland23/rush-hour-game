// src/test/java/hu/unideb/inf/game/SimpleAITest.java
package hu.unideb.inf.game;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import hu.unideb.inf.state.Board;
import hu.unideb.inf.state.Vehicle;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleAITest {

    @Test
    void testMakeMoveShouldSlideVehicleToValidPosition() {
        Board board = new Board(1, 3);
        board.addVehicle(new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(0, 0)));
        SimpleAI ai = new SimpleAI(new Random(42));

        boolean moved = ai.makeMove(board);

        assertTrue(moved);
        assertEquals(new Coordinate(0, 1), board.getVehicleById("A").orElseThrow().topLeft());
    }

    @Test
    void testMakeMoveShouldReturnFalseWhenCompletelyBlocked() {
        Board board = new Board(1, 2);
        board.addVehicle(new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(0, 0)));
        SimpleAI ai = new SimpleAI();

        boolean moved = ai.makeMove(board);

        assertFalse(moved);
        assertEquals(new Coordinate(0, 0), board.getVehicleById("A").orElseThrow().topLeft());
    }

    @Test
    void testMakeMoveShouldNeverPerformZeroStep() {
        Board board = new Board(1, 4);
        board.addVehicle(new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(0, 1)));
        SimpleAI ai = new SimpleAI(new Random(7));

        assertTrue(ai.makeMove(board));
        assertNotEquals(new Coordinate(0, 1), board.getVehicleById("A").orElseThrow().topLeft());
    }
}
