// src/test/java/hu/unideb/inf/db/HighScoreDaoTest.java
package hu.unideb.inf.db;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HighScoreDaoTest {

    private int getWins(String databaseName, String playerName) throws Exception {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1", "sa", "");
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT wins FROM HIGHSCORES WHERE player_name = ?")) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                return resultSet.getInt("wins");
            }
        }
    }

    @Test
    void testRecordWinShouldInsertFirstVictory() throws Exception {
        String databaseName = "highscore_first_" + UUID.randomUUID().toString().replace("-", "");
        HighScoreDao dao = new HighScoreDao("jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1");

        dao.recordWin("Roland");

        assertEquals(1, getWins(databaseName, "Roland"));
    }

    @Test
    void testRecordWinShouldIncrementExistingVictoryCount() throws Exception {
        String databaseName = "highscore_repeat_" + UUID.randomUUID().toString().replace("-", "");
        HighScoreDao dao = new HighScoreDao("jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1");

        dao.recordWin("Roland");
        dao.recordWin("Roland");
        dao.recordWin("Roland");

        assertEquals(3, getWins(databaseName, "Roland"));
    }

    @Test
    void testPrintHighScoresShouldOutputSavedPlayers() {
        String databaseName = "highscore_print_" + UUID.randomUUID().toString().replace("-", "");
        HighScoreDao dao = new HighScoreDao("jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1");
        dao.recordWin("Roland");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            dao.printHighScores();
        } finally {
            System.setOut(originalOut);
        }

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Roland"));
        assertTrue(text.contains("1 győzelem"));
    }
}
