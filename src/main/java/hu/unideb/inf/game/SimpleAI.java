package hu.unideb.inf.game;

import hu.unideb.inf.state.Board;
import hu.unideb.inf.state.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimpleAI {
    private final Random random = new Random();

    public boolean makeMove(Board board) {
        List<Vehicle> vehicles = new ArrayList<>(board.getVehicles());
        Collections.shuffle(vehicles);

        for (Vehicle v : vehicles) {
            List<Integer> validSteps = new ArrayList<>();
            int maxStep = Math.max(board.getRows(), board.getCols());
            for (int step = -maxStep; step <= maxStep; step++) {
                if (step != 0 && board.canMove(v, step)) {
                    validSteps.add(step);
                }
            }

            if (!validSteps.isEmpty()) {
                int chosenStep = validSteps.get(random.nextInt(validSteps.size()));
                v.move(chosenStep);
                System.out.println("AI lépett: Jármű " + v.getId() + " elmozdítva " + chosenStep + " mezővel.");
                return true;
            }
        }
        return false;
    }
}