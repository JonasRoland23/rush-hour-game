package hu.unideb.inf.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import hu.unideb.inf.state.Board;
import hu.unideb.inf.state.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


public final class GameLoader {
    private static final Logger logger = LoggerFactory.getLogger(GameLoader.class);
    private static final String DEFAULT_RESOURCE = "/map2.txt";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    record VehicleData(String id, int length, Orientation orientation, Coordinate topLeft) {
    }

    record BoardSnapshot(int rows, int cols, List<VehicleData> vehicles) {
    }

    private GameLoader() {
    }

    public static Board loadDefaultBoard() {
        return loadFromResource(DEFAULT_RESOURCE);
    }

    public static Board loadFromResource(String resourcePath) {
        String normalized = normalizeResourcePath(resourcePath);
        try (InputStream is = GameLoader.class.getResourceAsStream(normalized)) {
            if (is == null) {
                logger.warn("Erőforrás nem található ({}), alapértelmezett pálya töltődik be.", normalized);
                return createDefaultBoard();
            }
            return parseFromStream(is, normalized);
        } catch (Exception e) {
            logger.error("Hiba az erőforrás beolvasásakor ({}), alapértelmezett pálya töltődik be.", normalized, e);
            return createDefaultBoard();
        }
    }

    public static Board loadOrDefault(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            logger.warn("Üres fájlnév, alapértelmezett pálya töltődik be.");
            return createDefaultBoard();
        }

        String trimmed = filePath.trim();
        Path path;
        try {
            path = Path.of(trimmed);
        } catch (InvalidPathException e) {
            logger.warn("Érvénytelen fájlútvonal ({}), alapértelmezett pálya töltődik be.", trimmed, e);
            return createDefaultBoard();
        }

        if (Files.isRegularFile(path)) {
            try (InputStream is = Files.newInputStream(path)) {
                return parseFromStream(is, trimmed);
            } catch (Exception e) {
                logger.error("Hiba a fájl beolvasásakor ({}), alapértelmezett pálya töltődik be.", trimmed, e);
                return createDefaultBoard();
            }
        }

        String resourceName = normalizeResourcePath(trimmed);
        try (InputStream is = GameLoader.class.getResourceAsStream(resourceName)) {
            if (is != null) {
                return parseFromStream(is, resourceName);
            }
        } catch (Exception e) {
            logger.error("Hiba az erőforrás beolvasásakor ({}), alapértelmezett pálya töltődik be.", resourceName, e);
            return createDefaultBoard();
        }

        logger.warn("Fájl nem található ({}), alapértelmezett pálya töltődik be.", trimmed);
        return createDefaultBoard();
    }

    public static void save(Board board, String filePath) throws IOException {
        if (board == null || filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("A tábla és a fájl elérési útja nem lehet üres.");
        }
        Path path = Path.of(filePath.trim());
        if (filePath.trim().toLowerCase(Locale.ROOT).endsWith(".json")) {
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                List<VehicleData> vehicleData = board.getVehicles().stream()
                        .map(v -> new VehicleData(v.id(), v.length(), v.orientation(), v.topLeft()))
                        .toList();
                BoardSnapshot snapshot = new BoardSnapshot(board.getRows(), board.getCols(), vehicleData);
                OBJECT_MAPPER.writeValue(writer, snapshot);
            }
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(board.getRows() + " " + board.getCols());
            writer.newLine();
            for (Vehicle vehicle : board.getVehicles()) {
                String ori = vehicle.orientation() == Orientation.HORIZONTAL ? "H" : "V";
                writer.write(vehicle.id() + " " + vehicle.length() + " " + ori + " "
                        + vehicle.topLeft().row() + " " + vehicle.topLeft().col());
                writer.newLine();
            }
        }
    }

    public static Board createDefaultBoard() {
        Board board = new Board(6, 12);
        // Humán céljármű (1)
        board.addVehicle(new Vehicle("1", 2, Orientation.HORIZONTAL, new Coordinate(2, 0)));
        // AI céljármű (2)
        board.addVehicle(new Vehicle("2", 2, Orientation.HORIZONTAL, new Coordinate(3, 10)));
        // Akadályok
        board.addVehicle(new Vehicle("A", 3, Orientation.VERTICAL, new Coordinate(1, 2)));
        board.addVehicle(new Vehicle("B", 2, Orientation.HORIZONTAL, new Coordinate(0, 4)));
        board.addVehicle(new Vehicle("C", 2, Orientation.VERTICAL, new Coordinate(3, 5)));
        board.addVehicle(new Vehicle("D", 3, Orientation.HORIZONTAL, new Coordinate(1, 7)));
        return board;
    }

    private static Board parseFromStream(InputStream inputStream, String sourceName) throws IOException {
        if (sourceName.toLowerCase(Locale.ROOT).endsWith(".json")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                BoardSnapshot snapshot = OBJECT_MAPPER.readValue(reader, BoardSnapshot.class);
                Board board = new Board(snapshot.rows(), snapshot.cols());
                if (snapshot.vehicles() == null || snapshot.vehicles().isEmpty()) {
                    throw new IllegalArgumentException("A JSON pálya nem tartalmaz járműveket.");
                }
                for (VehicleData v : snapshot.vehicles()) {
                    board.addVehicle(new Vehicle(v.id(), v.length(), v.orientation(), v.topLeft()));
                }
                return board;
            }
        }

        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            if (!scanner.hasNextInt()) {
                throw new IllegalArgumentException("Hiányzik a sorok száma.");
            }
            int rows = scanner.nextInt();
            if (!scanner.hasNextInt()) {
                throw new IllegalArgumentException("Hiányzik az oszlopok száma.");
            }
            int cols = scanner.nextInt();
            Board board = new Board(rows, cols);

            while (scanner.hasNext()) {
                String id = scanner.next();
                int length = scanner.nextInt();
                String oriStr = scanner.next();
                int r = scanner.nextInt();
                int c = scanner.nextInt();

                Orientation orientation;
                if ("H".equalsIgnoreCase(oriStr)) {
                    orientation = Orientation.HORIZONTAL;
                } else if ("V".equalsIgnoreCase(oriStr)) {
                    orientation = Orientation.VERTICAL;
                } else {
                    throw new IllegalArgumentException("Érvénytelen irány: " + oriStr);
                }
                board.addVehicle(new Vehicle(id, length, orientation, new Coordinate(r, c)));
            }
            if (board.getVehicles().isEmpty()) {
                throw new IllegalArgumentException("A pálya nem tartalmaz járműveket.");
            }
            return board;
        }
    }

    private static String normalizeResourcePath(String rawPath) {
        String normalized = rawPath.trim().replace('\\', '/');
        String prefix = "src/main/resources/";
        if (normalized.startsWith(prefix)) {
            normalized = normalized.substring(prefix.length());
        }
        return normalized.startsWith("/") ? normalized : "/" + normalized;
    }
}
