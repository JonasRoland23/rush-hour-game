package hu.unideb.inf.game;

import hu.unideb.inf.db.HighScoreDao;
import hu.unideb.inf.model.Coordinate;
import hu.unideb.inf.model.Orientation;
import hu.unideb.inf.state.Board;
import hu.unideb.inf.state.Vehicle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class GameTest {

    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreOut() {
        System.setOut(originalOut);
    }

    @Test
    void testHumanVictoryShouldBeRecordedWhenMovingByCellNotation() throws Exception {
        Path mapFile = tempDir.resolve("win-human.txt");
        Files.writeString(mapFile, "3 6\n1 2 H 1 2\n2 2 H 0 1\n");
        HighScoreDao dao = mock(HighScoreDao.class);
        SimpleAI ai = mock(SimpleAI.class);
        Game game = new Game(new Scanner("2\n" + mapFile + "\nRoland\nc2 3\n"), dao, ai);
        captureOutput();

        game.start();

        verify(dao).recordWin("Roland");
        verify(ai, never()).makeMove(any(Board.class));
    }

    @Test
    void testAiVictoryShouldBeRecordedInDatabaseWhenAiReachesExit() throws Exception {
        Path mapFile = tempDir.resolve("win-ai.txt");
        Files.writeString(mapFile, "4 6\n1 2 H 0 0\n2 2 H 2 1\nA 2 V 0 4\n");
        HighScoreDao dao = mock(HighScoreDao.class);
        SimpleAI ai = mock(SimpleAI.class);
        doAnswer(invocation -> {
            Board b = invocation.getArgument(0);
            Vehicle v2 = b.getVehicleById("2").orElseThrow();
            b.moveVehicle(v2, -2);
            return true;
        }).when(ai).makeMove(any(Board.class));
        Game game = new Game(new Scanner("2\n" + mapFile + "\nRoland\n1 1\n"), dao, ai);
        captureOutput();

        game.start();

        verify(dao).recordWin("GÉPI JÁTÉKOS");
    }

    @Test
    void testHumanPushingAiTargetToItsExitShouldTriggerAiVictoryImmediately() throws Exception {
        Path mapFile = tempDir.resolve("push-ai.txt");
        Files.writeString(mapFile, "3 6\n1 2 H 0 0\n2 2 H 1 2\n");
        HighScoreDao dao = mock(HighScoreDao.class);
        SimpleAI ai = mock(SimpleAI.class);
        Game game = new Game(new Scanner("2\n" + mapFile + "\nRoland\n2 -3\n"), dao, ai);
        captureOutput();

        game.start();

        verify(dao).recordWin("GÉPI JÁTÉKOS");
        verify(ai, never()).makeMove(any(Board.class));
    }

    @Test
    void testZeroStepShouldBeRejectedAndSaveCommandShouldPersistBoard() throws Exception {
        Path mapFile = tempDir.resolve("save-test.txt");
        Files.writeString(mapFile, "3 6\n1 2 H 1 2\n2 2 H 0 1\n");
        Path saveFile = tempDir.resolve("saved-state.txt");
        HighScoreDao dao = mock(HighScoreDao.class);
        SimpleAI ai = mock(SimpleAI.class);
        Game game = new Game(
                new Scanner("2\n" + mapFile + "\nRoland\n1 0\nsave " + saveFile + "\nexit\n"),
                dao, ai);
        ByteArrayOutputStream out = captureOutput();

        game.start();

        assertTrue(out.toString(StandardCharsets.UTF_8).contains("A 0 lépés nem megengedett!"));
        assertTrue(Files.exists(saveFile));
        assertTrue(Files.readString(saveFile).startsWith("3 6"));
        verify(dao, never()).recordWin(any());
    }

    @Test
    void testPerformMoveShouldRejectBlockedOrMalformedCommands() {
        Board board = new Board(4, 4);
        board.addVehicle(new Vehicle("1", 2, Orientation.HORIZONTAL, new Coordinate(1, 0)));
        board.addVehicle(new Vehicle("A", 2, Orientation.VERTICAL, new Coordinate(0, 2)));
        Game game = new Game(new Scanner(""), mock(HighScoreDao.class), mock(SimpleAI.class));
        game.setBoard(board);
        game.setPlayerName("Roland");
        captureOutput();

        assertFalse(game.performMove(""));
        assertFalse(game.performMove("1"));
        assertFalse(game.performMove("1 nemSzam"));
        assertFalse(game.performMove("1 0"));
        assertFalse(game.performMove("Z 1"));
        assertFalse(game.performMove("1 1"));
    }

    private ByteArrayOutputStream captureOutput() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        return out;
    }
}
