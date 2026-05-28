package OOP;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import java.sql.Connection;

public class ReportGenerator {

    public static void showReport() {
        try {
            System.out.println("=== Compiling Report from JRXML ===");

            String jrxmlPath = "src/OOP/AttendanceReport.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlPath);

            System.out.println("=== Filling Report with Data ===");
            Connection conn = DatabaseManager.connect();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);

            System.out.println("=== Opening Report Viewer ===");
            JasperViewer.viewReport(jasperPrint, false);

            System.out.println("=== Report Window Opened Successfully ===");

        } catch (Throwable t) {
            System.out.println("\n!!! JASPER REPORTING ERROR !!!");
            t.printStackTrace();
        }
    }
}