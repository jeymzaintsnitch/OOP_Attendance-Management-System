package OOP;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // attendance.db must be in your project root folder (JavaFX/attendance.db)
    // Eclipse working directory is the project root, so this path is correct.
    private static final String URL = "jdbc:sqlite:attendance.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("[DB] Connection Error: " + e.getMessage());
            return null;
        }
    }

    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS records ("
                + " id      INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " student_name TEXT NOT NULL,"
                + " date    TEXT NOT NULL,"
                + " status  TEXT NOT NULL"
                + ");";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("[DB] Database initialized.");
        } catch (SQLException e) {
            System.err.println("[DB] Init Error: " + e.getMessage());
        }
    }

    // [C]REATE
    public static void addRecord(String name, String date, String status) {
        String sql = "INSERT INTO records(student_name, date, status) VALUES(?,?,?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, date);
            ps.setString(3, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB] Insert Error: " + e.getMessage());
        }
    }

    // [U]PDATE
    public static void updateRecord(String id, String name, String date, String status) {
        String sql = "UPDATE records SET student_name=?, date=?, status=? WHERE id=?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, date);
            ps.setString(3, status);
            ps.setInt(4, Integer.parseInt(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB] Update Error: " + e.getMessage());
        }
    }

    // [D]ELETE
    public static void deleteRecord(String id) {
        String sql = "DELETE FROM records WHERE id=?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB] Delete Error: " + e.getMessage());
        }
    }
}
