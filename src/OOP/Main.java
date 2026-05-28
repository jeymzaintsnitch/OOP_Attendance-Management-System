package OOP;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class Main extends Application {

    private final TableView<AttendanceRecord> table = new TableView<>();
    private final ObservableList<AttendanceRecord> data = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {

        // Initialize DB on startup
        DatabaseManager.initializeDatabase();

        // ── Title ────────────────────────────────────────────────
        Label title = new Label("Attendance Management System");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // ── Table Columns ─────────────────────────────────────────
        TableColumn<AttendanceRecord, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMinWidth(50);

        TableColumn<AttendanceRecord, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(200);

        TableColumn<AttendanceRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setMinWidth(120);

        TableColumn<AttendanceRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setMinWidth(100);

        table.getColumns().addAll(idCol, nameCol, dateCol, statusCol);
        table.setPlaceholder(new Label("No attendance records found."));
        loadDatabaseData();

        // ── Input Fields ──────────────────────────────────────────
        TextField nameInput = new TextField();
        nameInput.setPromptText("Student Name");
        nameInput.setPrefWidth(200);

        DatePicker dateInput = new DatePicker(LocalDate.now());
        dateInput.setPrefWidth(150);

        ComboBox<String> statusInput = new ComboBox<>();
        statusInput.getItems().addAll("Present", "Absent", "Late");
        statusInput.setValue("Present");
        statusInput.setPrefWidth(120);

        // Auto-fill inputs when a row is selected
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameInput.setText(newVal.getName());
                try {
                    dateInput.setValue(LocalDate.parse(newVal.getDate()));
                } catch (Exception e) {
                    dateInput.setValue(LocalDate.now());
                }
                statusInput.setValue(newVal.getStatus());
            }
        });

        // ── Buttons ───────────────────────────────────────────────

        // ADD
        Button addButton = new Button("Mark Attendance");
        addButton.setStyle("-fx-base: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> {
            String name = nameInput.getText().trim();
            if (name.isEmpty()) {
                showAlert("Input Error", "Please enter a student name.");
                return;
            }
            DatabaseManager.addRecord(name, dateInput.getValue().toString(), statusInput.getValue());
            loadDatabaseData();
            clearInputs(nameInput, dateInput, statusInput);
        });

        // UPDATE
        Button updateButton = new Button("Update Selected");
        updateButton.setStyle("-fx-base: #ff9800; -fx-text-fill: white; -fx-font-weight: bold;");
        updateButton.setOnAction(e -> {
            AttendanceRecord selected = table.getSelectionModel().getSelectedItem();
            String name = nameInput.getText().trim();
            if (selected == null) {
                showAlert("No Selection", "Please select a record to update.");
                return;
            }
            if (name.isEmpty()) {
                showAlert("Input Error", "Please enter a student name.");
                return;
            }
            DatabaseManager.updateRecord(
                selected.getId(),
                name,
                dateInput.getValue().toString(),
                statusInput.getValue()
            );
            loadDatabaseData();
            clearInputs(nameInput, dateInput, statusInput);
            table.getSelectionModel().clearSelection();
        });

        // DELETE
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-base: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setOnAction(e -> {
            AttendanceRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("No Selection", "Please select a record to delete.");
                return;
            }
            // Confirm before deleting
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete record for \"" + selected.getName() + "\" on " + selected.getDate() + "?",
                ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirm Delete");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    DatabaseManager.deleteRecord(selected.getId());
                    loadDatabaseData();
                    clearInputs(nameInput, dateInput, statusInput);
                }
            });
        });

        // GENERATE REPORT
        Button reportButton = new Button("Generate Report");
        reportButton.setStyle("-fx-base: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        reportButton.setOnAction(e -> ReportGenerator.showReport());

        // ── Layout ────────────────────────────────────────────────
        HBox inputRow = new HBox(10, nameInput, dateInput, statusInput);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        HBox buttonRow = new HBox(10, addButton, updateButton, deleteButton, reportButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox controls = new VBox(10, inputRow, buttonRow);
        controls.setPadding(new Insets(10, 0, 0, 0));

        VBox root = new VBox(15, title, table, controls);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 700, 560);
        primaryStage.setTitle("ADKJ - Attendance Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void loadDatabaseData() {
        data.clear();
        String query = "SELECT * FROM records ORDER BY date DESC";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(query)) {

            while (rs.next()) {
                data.add(new AttendanceRecord(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("student_name"),
                    rs.getString("date"),
                    rs.getString("status")
                ));
            }
            table.setItems(data);

        } catch (Exception e) {
            System.err.println("[UI] Load Error: " + e.getMessage());
        }
    }

    private void clearInputs(TextField nameInput, DatePicker dateInput, ComboBox<String> statusInput) {
        nameInput.clear();
        dateInput.setValue(LocalDate.now());
        statusInput.setValue("Present");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
