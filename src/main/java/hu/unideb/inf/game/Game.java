package hu.unideb.inf.game;

import hu.unideb.inf.db.HighScoreDao;
import hu.unideb.inf.io.GameLoader;
import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.state.Board;
import hu.unideb.inf.state.Vehicle;

import java.util.Optional;
import java.util.Scanner;

public class Game {
    private Board board;
    private String playerName;
    private final HighScoreDao highScoreDao = new HighScoreDao();
    private final SimpleAI ai = new SimpleAI();

    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== RUSH HOUR JÁTÉK ===");
        System.out.print("Működési mód (1: alapértelmezett pálya, 2: pályafájl megadása): ");
        String mode = scanner.nextLine().trim();

        if ("2".equals(mode)) {
            System.out.print("Adja meg a fájl elérését (pl: map2.txt): ");
            String path = scanner.nextLine().trim();
            board = GameLoader.loadOrDefault(path);
        } else {
            board = GameLoader.createDefaultBoard();
        }

        System.out.print("Kérlek, add meg a neved: ");
        playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) playerName = "Játékos";

        highScoreDao.printHighScores();

        boolean running = true;
        while (running) {
            renderBoard();

            // Humán lépés
            System.out.println("\n" + playerName + " következik!");
            System.out.print("Lépés megadása (pl. 'A 2' vagy 'exit'): ");
            String input = scanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Játék kilépve.");
                break;
            }

            String[] parts = input.split("\\s+");
            if (parts.length != 2) {
                System.out.println("Érvénytelen formátum! Használat: <Jármű_ID> <lépésszám>");
                continue;
            }

            String vehicleId = parts[0];
            int steps;
            try {
                steps = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("A lépésszámnak egész számnak kell lennie!");
                continue;
            }

            Optional<Vehicle> vOpt = board.getVehicleById(vehicleId);
            if (vOpt.isEmpty()) {
                System.out.println("Nincs ilyen ID-jú jármű!");
                continue;
            }

            Vehicle vehicle = vOpt.get();
            if (!board.canMove(vehicle, steps)) {
                System.out.println("Ez a lépés szabálytalan!");
                continue;
            }

            vehicle.move(steps);

            // Győzelem ellenőrzés Humánnak ('A' autó elérte a jobb szélt)
            if (vehicle.getId().equalsIgnoreCase("1") && vehicle.getTopLeft().col() + vehicle.getLength() >= board.getCols()) {
                renderBoard();
                System.out.println("\n GRATULÁLOK " + playerName + ", NYERTÉL!");
                highScoreDao.recordWin(playerName);
                highScoreDao.printHighScores();
                break;
            }

            // AI lépés
            System.out.println("\n--- GÉPI JÁTÉKOS LÉP ---");
            ai.makeMove(board);

            // Győzelem ellenőrzés AI-nak ('B' autó elérte a bal szélt)
            Optional<Vehicle> bOpt = board.getVehicleById("2");
            if (bOpt.isPresent() && bOpt.get().getTopLeft().col() <= 0) {
                renderBoard();
                System.out.println("\n A GÉPI JÁTÉKOS NYERT!");
                break;
            }
        }
    }

    private void renderBoard() {
        System.out.println("\n PÁLYA ÁLLAPOTA:");
        for (int r = 0; r < board.getRows(); r++) {
            System.out.print((r + 1) + " | ");
            for (int c = 0; c < board.getCols(); c++) {
                Coordinate coord = new Coordinate(r, c);
                Optional<Vehicle> v = board.getVehicles().stream()
                        .filter(veh -> veh.occupies(coord))
                        .findFirst();
                if (v.isPresent()) {
                    System.out.print(v.get().getId() + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println("|");
        }
    }
}