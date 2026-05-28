package OOP;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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
    private TableView<AttendanceRecord> table = new TableView<>();
    private ObservableList<AttendanceRecord> data = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.initializeDatabase();

        Label title = new Label("Attendance Management System");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Table Setup
        TableColumn<AttendanceRecord, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(180);

        TableColumn<AttendanceRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setMinWidth(120);

        TableColumn<AttendanceRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setMinWidth(120);

        table.getColumns().addAll(nameCol, dateCol, statusCol);
        loadDatabaseData(); 

        // Input Fields
        TextField nameInput = new TextField();
        nameInput.setPromptText("Student Name");
        nameInput.setPrefWidth(200);

        DatePicker dateInput = new DatePicker();
        dateInput.setValue(LocalDate.now());
        dateInput.setPrefWidth(150);

        ComboBox<String> statusInput = new ComboBox<>();
        statusInput.getItems().addAll("Present", "Absent", "Late");
        statusInput.setValue("Present");
        statusInput.setPrefWidth(150);

        // FEATURE: Auto-fill inputs when a row is clicked
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameInput.setText(newSelection.getName());
                dateInput.setValue(LocalDate.parse(newSelection.getDate()));
                statusInput.setValue(newSelection.getStatus());
            }
        });

        // BUTTON 1: Add (Create)
        Button addButton = new Button("Mark Attendance");
        addButton.setOnAction(e -> {
            if(!nameInput.getText().isEmpty()) {
                DatabaseManager.addRecord(nameInput.getText(), dateInput.getValue().toString(), statusInput.getValue());
                loadDatabaseData();
                nameInput.clear();
            }
        });

        // BUTTON 2: Update Selected (Update)
        Button updateButton = new Button("Update Selected");
        updateButton.setStyle("-fx-base: #ff9800; -fx-text-fill: white; -fx-font-weight: bold;"); // Orange
        updateButton.setOnAction(e -> {
            AttendanceRecord selectedRecord = table.getSelectionModel().getSelectedItem();
            if (selectedRecord != null && !nameInput.getText().isEmpty()) {
                DatabaseManager.updateRecord(
                    selectedRecord.getId(), 
                    nameInput.getText(), 
                    dateInput.getValue().toString(), 
                    statusInput.getValue()
                );
                loadDatabaseData(); // Refresh table
                nameInput.clear();  // Clear input
                table.getSelectionModel().clearSelection(); // Deselect row
            }
        });

        // BUTTON 3: Delete (Delete)
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-base: #f44336; -fx-text-fill: white; -fx-font-weight: bold;"); // Red
        deleteButton.setOnAction(e -> {
            AttendanceRecord selectedRecord = table.getSelectionModel().getSelectedItem();
            if (selectedRecord != null) {
                DatabaseManager.deleteRecord(selectedRecord.getId());
                loadDatabaseData();
                nameInput.clear();
            }
        });

        // BUTTON 4: Generate Report
        Button reportButton = new Button("Generate Report");
        reportButton.setStyle("-fx-base: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;"); // Blue
        reportButton.setOnAction(e -> {
            ReportGenerator.showReport(); 
        });

        // --- NEW CLEANER LAYOUT ---
        // Row 1: Input Fields
        HBox inputFieldsBox = new HBox(10);
        inputFieldsBox.getChildren().addAll(nameInput, dateInput, statusInput);

        // Row 2: Action Buttons
        HBox actionButtonsBox = new HBox(10);
        actionButtonsBox.getChildren().addAll(addButton, updateButton, deleteButton, reportButton);

        // Combine them
        VBox controlsLayout = new VBox(15);
        controlsLayout.setPadding(new Insets(10, 0, 0, 0));
        controlsLayout.getChildren().addAll(inputFieldsBox, actionButtonsBox);

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.getChildren().addAll(title, table, controlsLayout);

        // Stage Setup
        Scene scene = new Scene(mainLayout, 650, 550);
        primaryStage.setTitle("ADKJ - Full CRUD Attendance System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadDatabaseData() {
        data.clear();
        String query = "SELECT * FROM records";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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
            System.out.println("UI Refresh Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}