package OOP;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:attendance.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Database Connection Error: " + e.getMessage());
        }
        return conn;
    }

    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS records ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " student_name TEXT NOT NULL,"
                + " date TEXT NOT NULL,"
                + " status TEXT NOT NULL"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.out.println("Database Init Error: " + e.getMessage());
        }
    }

    // [C]REATE
    public static void addRecord(String name, String date, String status) {
        String insertSQL = "INSERT INTO records(student_name, date, status) VALUES(?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, date);
            pstmt.setString(3, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Insert Error: " + e.getMessage());
        }
    }

    // [U]PDATE (NEW METHOD)
    public static void updateRecord(String id, String name, String date, String status) {
        String updateSQL = "UPDATE records SET student_name = ?, date = ?, status = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, date);
            pstmt.setString(3, status);
            pstmt.setInt(4, Integer.parseInt(id));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update Error: " + e.getMessage());
        }
    }

    // [D]ELETE
    public static void deleteRecord(String id) {
        String deleteSQL = "DELETE FROM records WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, Integer.parseInt(id));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete Error: " + e.getMessage());
        }
    }
}