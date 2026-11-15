package hu.unideb.inf.io;

import hu.unideb.inf.state.Board;
import hu.unideb.inf.state.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GameLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsBoardAndVehiclesFromTextFile() throws Exception {
        Path mapFile = tempDir.resolve("custom-map.txt");
        Files.writeString(mapFile, "6 6\nA 2 H 2 0\nB 3 V 0 4\n");

        Board board = GameLoader.loadOrDefault(mapFile.toString());

        assertEquals(6, board.getRows());
        assertEquals(6, board.getCols());

        Vehicle horizontal = board.getVehicleById("A").orElseThrow();
        assertEquals(2, horizontal.getLength());
        assertEquals(2, horizontal.getTopLeft().row());
        assertEquals(0, horizontal.getTopLeft().col());

        Vehicle vertical = board.getVehicleById("B").orElseThrow();
        assertEquals(3, vertical.getLength());
        assertEquals(0, vertical.getTopLeft().row());
        assertEquals(4, vertical.getTopLeft().col());
    }

    @Test
    void loadsDefaultBoardWhenFileDoesNotExist() {
        Path missingFile = tempDir.resolve("missing-map.txt");

        Board board = GameLoader.loadOrDefault(missingFile.toString());

        assertEquals(6, board.getRows());
        assertEquals(12, board.getCols());
        assertTrue(board.getVehicleById("1").isPresent());
        assertTrue(board.getVehicleById("2").isPresent());
    }

    @Test
    void loadsDefaultBoardWhenFileContentIsInvalid() throws Exception {
        Path invalidFile = tempDir.resolve("invalid-map.txt");
        Files.writeString(invalidFile, "not-a-valid-map");

        Board board = GameLoader.loadOrDefault(invalidFile.toString());

        assertEquals(6, board.getRows());
        assertEquals(12, board.getCols());
        assertTrue(board.getVehicleById("1").isPresent());
    }
}