package hu.unideb.inf.io;

import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import hu.unideb.inf.state.Board;
import hu.unideb.inf.state.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Scanner;

public class GameLoader {
    private static final Logger logger = LoggerFactory.getLogger(GameLoader.class);

    public static Board loadOrDefault(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            logger.info("Fájl nem található ({}), alapértelmezett pálya töltődik be.", filePath);
            return createDefaultBoard();
        }

        try (Scanner scanner = new Scanner(file)) {
            if (!scanner.hasNextInt()) return createDefaultBoard();
            int rows = scanner.nextInt();
            int cols = scanner.nextInt();
            Board board = new Board(rows, cols);

            while (scanner.hasNext()) {
                String id = scanner.next();
                int length = scanner.nextInt();
                String oriStr = scanner.next();
                int r = scanner.nextInt();
                int c = scanner.nextInt();

                Orientation orientation = oriStr.equalsIgnoreCase("H") ? Orientation.HORIZONTAL : Orientation.VERTICAL;
                board.addVehicle(new Vehicle(id, length, orientation, new Coordinate(r, c)));
            }
            return board;
        } catch (Exception e) {
            logger.error("Hiba a pálya beolvasásakor, alapértelmezett pálya töltődik be.", e);
            return createDefaultBoard();
        }
    }

    public static Board createDefaultBoard() {
        Board board = new Board(6, 12);
        // Humán céljármű (A)
        board.addVehicle(new Vehicle("A", 2, Orientation.HORIZONTAL, new Coordinate(2, 0)));
        // AI céljármű (B)
        board.addVehicle(new Vehicle("B", 2, Orientation.HORIZONTAL, new Coordinate(3, 10)));
        // Akadályok
        board.addVehicle(new Vehicle("C", 3, Orientation.VERTICAL, new Coordinate(1, 2)));
        board.addVehicle(new Vehicle("D", 2, Orientation.HORIZONTAL, new Coordinate(0, 4)));
        board.addVehicle(new Vehicle("E", 2, Orientation.VERTICAL, new Coordinate(3, 5)));
        return board;
    }
}