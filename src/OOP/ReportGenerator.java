package OOP;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import java.sql.Connection;

public class ReportGenerator {

    public static void showReport() {
        Connection conn = null;
        try {
            System.out.println("[Report] Compiling report from JRXML...");

            // Compiles .jrxml at runtime — avoids version mismatch with .jasper files
            String jrxmlPath = "src/OOP/AttendanceReport.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlPath);

            System.out.println("[Report] Connecting to database...");
            conn = DatabaseManager.connect();

            if (conn == null) {
                System.err.println("[Report] ERROR: Could not connect to database.");
                return;
            }

            System.out.println("[Report] Filling report with data...");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);

            System.out.println("[Report] Opening report viewer...");
            JasperViewer.viewReport(jasperPrint, false);

            System.out.println("[Report] Report opened successfully!");

        } catch (JRException e) {
            System.err.println("[Report] Jasper Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Always close the connection after the report is filled
            if (conn != null) {
                try { conn.close(); } catch (Exception ignored) {}
            }
        }
    }
}
