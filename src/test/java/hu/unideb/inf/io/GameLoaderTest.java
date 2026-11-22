package hu.unideb.inf.io;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import hu.unideb.inf.state.Board;
import hu.unideb.inf.state.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void testLoadDefaultBoardShouldReadPackagedMap2Resource() {
        Board board = GameLoader.loadDefaultBoard();

        assertEquals(6, board.getRows());
        assertEquals(12, board.getCols());
        assertTrue(board.getVehicleById("1").isPresent());
        assertTrue(board.getVehicleById("2").isPresent());
        assertEquals(new Coordinate(4, 1), board.getVehicleById("1").orElseThrow().topLeft());
    }

    @Test
    void testLoadOrDefaultShouldReadCustomTextFile() throws Exception {
        Path mapFile = tempDir.resolve("custom-map.txt");
        Files.writeString(mapFile, "6 6\nA 2 H 2 0\nB 3 V 0 4\n");

        Board board = GameLoader.loadOrDefault(mapFile.toString());

        assertEquals(6, board.getRows());
        assertEquals(6, board.getCols());
        assertEquals(new Coordinate(2, 0), board.getVehicleById("A").orElseThrow().topLeft());
        assertEquals(new Coordinate(0, 4), board.getVehicleById("B").orElseThrow().topLeft());
    }

    @Test
    void testLoadOrDefaultShouldFallbackWhenFileMissingOrInvalid() throws Exception {
        Path missingFile = tempDir.resolve("missing.txt");
        Board fromMissing = GameLoader.loadOrDefault(missingFile.toString());
        assertEquals(6, fromMissing.getRows());
        assertEquals(12, fromMissing.getCols());

        Path invalidFile = tempDir.resolve("invalid.txt");
        Files.writeString(invalidFile, "hibas-tartalom");
        Board fromInvalid = GameLoader.loadOrDefault(invalidFile.toString());
        assertEquals(6, fromInvalid.getRows());
        assertEquals(12, fromInvalid.getCols());
    }

    @Test
    void testSaveAndReloadShouldPreserveExactBoardStateInTxt() throws Exception {
        Board original = new Board(6, 8);
        original.addVehicle(new Vehicle("1", 2, Orientation.HORIZONTAL, new Coordinate(2, 1)));
        original.addVehicle(new Vehicle("2", 2, Orientation.HORIZONTAL, new Coordinate(4, 5)));
        original.addVehicle(new Vehicle("A", 3, Orientation.VERTICAL, new Coordinate(0, 4)));
        original.moveVehicle(original.getVehicleById("1").orElseThrow(), 1);

        Path savedFile = tempDir.resolve("saved-map.txt");
        GameLoader.save(original, savedFile.toString());
        Board reloaded = GameLoader.loadOrDefault(savedFile.toString());

        assertEquals(original.getRows(), reloaded.getRows());
        assertEquals(original.getCols(), reloaded.getCols());
        List<Vehicle> expected = original.getVehicles().stream()
                .sorted(Comparator.comparing(Vehicle::id)).toList();
        List<Vehicle> actual = reloaded.getVehicles().stream()
                .sorted(Comparator.comparing(Vehicle::id)).toList();
        assertEquals(expected, actual);
    }

    @Test
    void testSaveAndReloadShouldPreserveExactBoardStateInJson() throws Exception {
        Board original = new Board(5, 7);
        original.addVehicle(new Vehicle("1", 2, Orientation.HORIZONTAL, new Coordinate(1, 0)));
        original.addVehicle(new Vehicle("2", 2, Orientation.HORIZONTAL, new Coordinate(3, 4)));

        Path jsonFile = tempDir.resolve("saved-map.json");
        GameLoader.save(original, jsonFile.toString());
        Board reloaded = GameLoader.loadOrDefault(jsonFile.toString());

        assertEquals(original.getRows(), reloaded.getRows());
        assertEquals(original.getCols(), reloaded.getCols());
        assertEquals(original.getVehicles(), reloaded.getVehicles());
    }

    @Test
    void testLoadFromResourceShouldFallbackWhenResourceDoesNotExist() {
        Board fallback = GameLoader.loadFromResource("/nonexistent-map.txt");
        assertEquals(6, fallback.getRows());
        assertEquals(12, fallback.getCols());
    }

    @Test
    void testSaveShouldRejectInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> GameLoader.save(null, "out.txt"));
        assertThrows(IllegalArgumentException.class, () -> GameLoader.save(new Board(3, 3), " "));
    }
}
