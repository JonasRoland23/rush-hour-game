package hu.unideb.inf.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class HighScoreDao {
    private static final Logger logger = LoggerFactory.getLogger(HighScoreDao.class);
    private static final String DB_URL = "jdbc:h2:./database";

    public HighScoreDao() {
        try (Connection conn = DriverManager.getConnection(DB_URL, "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS HIGHSCORES (" +
                    "player_name VARCHAR(255) PRIMARY KEY, " +
                    "wins INT NOT NULL)");
        } catch (SQLException e) {
            logger.error("Hiba az adatbázis inicializálásakor", e);
        }
    }

    public void recordWin(String playerName) {
        String sql = "MERGE INTO HIGHSCORES KEY(player_name) VALUES (?, COALESCE((SELECT wins + 1 FROM HIGHSCORES WHERE player_name = ?), 1))";
        try (Connection conn = DriverManager.getConnection(DB_URL, "sa", "");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.setString(2, playerName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Hiba a győzelem mentésekor", e);
        }
    }

    public void printHighScores() {
        System.out.println("\n--- HIGH SCORE TÁBLÁZAT ---");
        String sql = "SELECT player_name, wins FROM HIGHSCORES ORDER BY wins DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL, "sa", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("%-20s : %d győzelem%n", rs.getString("player_name"), rs.getInt("wins"));
            }
        } catch (SQLException e) {
            logger.error("Hiba a dicsőséglista lekérésekor", e);
        }
        System.out.println("---------------------------\n");
    }
}