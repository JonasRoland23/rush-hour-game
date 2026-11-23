package hu.unideb.inf.game;

import hu.unideb.inf.db.HighScoreDao;
import hu.unideb.inf.io.BoardNotation;
import hu.unideb.inf.io.GameLoader;
import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.state.Board;
import hu.unideb.inf.state.Vehicle;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

public class Game {
    private final Scanner scanner;
    private final HighScoreDao highScoreDao;
    private final SimpleAI ai;
    private Board board;
    private String playerName;

    public Game() {
        this(new Scanner(System.in), new HighScoreDao(), new SimpleAI());
    }

    public Game(Scanner scanner, HighScoreDao highScoreDao, SimpleAI ai) {
        this.scanner = scanner;
        this.highScoreDao = highScoreDao;
        this.ai = ai;
    }

    public void start() {
        System.out.println("=== RUSH HOUR JÁTÉK ===");
        System.out.print("Működési mód (1: alapértelmezett pálya fájlból, 2: egyedi pályafájl megadása): ");
        String mode = readLineOrDefault("1");

        if ("2".equals(mode)) {
            System.out.print("Adja meg a fájl elérési útját (pl. map2.txt): ");
            String path = readLineOrDefault("");
            board = GameLoader.loadOrDefault(path);
        } else {
            board = GameLoader.loadDefaultBoard();
        }

        System.out.print("Kérlek, add meg a neved: ");
        playerName = readLineOrDefault("Játékos");
        if (playerName.isEmpty()) {
            playerName = "Játékos";
        }

        highScoreDao.printHighScores();

        while (true) {
            renderBoard();

            System.out.println("\n" + playerName + " következik!");
            System.out.print("Lépés (pl. '1 2' vagy 'a 2'), mentés ('save <fájl>'), kilépés ('exit'): ");
            String input = readLineOrDefault("exit");

            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Játék kilépve.");
                return;
            }

            if (handleSaveCommand(input)) {
                continue;
            }

            if (!performMove(input)) {
                continue;
            }

            if (checkAndHandleWinner()) {
                return;
            }

            System.out.println("\n--- GÉPI JÁTÉKOS LÉP ---");
            ai.makeMove(board);

            if (checkAndHandleWinner()) {
                return;
            }
        }
    }

    boolean performMove(String input) {
        if (input == null || input.isBlank()) {
            System.out.println("Érvénytelen formátum! Használat: <Jármű_ID vagy mező> <lépésszám>");
            return false;
        }
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2) {
            System.out.println("Érvénytelen formátum! Használat: <Jármű_ID vagy mező> <lépésszám>");
            return false;
        }

        int steps;
        try {
            steps = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            System.out.println("A lépésszámnak egész számnak kell lennie!");
            return false;
        }

        if (steps == 0) {
            System.out.println("A 0 lépés nem megengedett!");
            return false;
        }

        Optional<Vehicle> vehicleOpt = resolveVehicle(parts[0]);
        if (vehicleOpt.isEmpty()) {
            System.out.println("Nincs ilyen azonosítójú vagy mezőn álló jármű!");
            return false;
        }

        Vehicle vehicle = vehicleOpt.get();
        if (!board.canMove(vehicle, steps)) {
            System.out.println("Ez a lépés szabálytalan!");
            return false;
        }

        board.moveVehicle(vehicle, steps);
        return true;
    }

    boolean handleSaveCommand(String input) {
        if (input == null) {
            return false;
        }
        String trimmed = input.trim();
        if (!"save".equalsIgnoreCase(trimmed) && !trimmed.toLowerCase(Locale.ROOT).startsWith("save ")) {
            return false;
        }
        String targetPath = trimmed.length() > 4 ? trimmed.substring(4).trim() : "";
        if (targetPath.isEmpty()) {
            System.out.println("Használat: save <fájlnév>");
            return true;
        }
        try {
            GameLoader.save(board, targetPath);
            System.out.println("Aktuális állás elmentve: " + targetPath);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Hiba a mentés során: " + e.getMessage());
        }
        return true;
    }

    boolean checkAndHandleWinner() {
        Optional<String> winnerId = board.getWinnerVehicleId();
        if (winnerId.isEmpty()) {
            return false;
        }
        renderBoard();
        if ("1".equals(winnerId.get())) {
            System.out.println("\nGRATULÁLOK " + playerName + ", NYERTÉL!");
            highScoreDao.recordWin(playerName);
        } else {
            System.out.println("\nA GÉPI JÁTÉKOS NYERT!");
            highScoreDao.recordWin("GÉPI JÁTÉKOS");
        }
        highScoreDao.printHighScores();
        return true;
    }

    void setBoard(Board board) {
        this.board = board;
    }

    void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    private Optional<Vehicle> resolveVehicle(String token) {
        Optional<Vehicle> byId = board.getVehicleById(token);
        if (byId.isPresent()) {
            return byId;
        }
        Optional<Coordinate> cell = BoardNotation.parseCell(token);
        if (cell.isPresent()) {
            return board.getVehicleAt(cell.get());
        }
        return Optional.empty();
    }

    private String readLineOrDefault(String fallback) {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : fallback;
    }

    private void renderBoard() {
        System.out.println("\nPÁLYA ÁLLAPOTA:");
        System.out.print("    ");
        for (int c = 0; c < board.getCols(); c++) {
            System.out.printf("%-3s", BoardNotation.columnToLabel(c));
        }
        System.out.println();

        for (int r = 0; r < board.getRows(); r++) {
            System.out.printf("%2d |", r + 1);
            for (int c = 0; c < board.getCols(); c++) {
                Coordinate coord = new Coordinate(r, c);
                Optional<Vehicle> v = board.getVehicleAt(coord);
                System.out.printf("%-3s", v.map(Vehicle::id).orElse("."));
            }
            System.out.println("|");
        }
    }
}
