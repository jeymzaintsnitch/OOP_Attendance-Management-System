package OOP;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AttendanceRecord {

    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty date;
    private final SimpleStringProperty status;

    public AttendanceRecord(String id, String name, String date, String status) {
        this.id     = new SimpleStringProperty(id);
        this.name   = new SimpleStringProperty(name);
        this.date   = new SimpleStringProperty(date);
        this.status = new SimpleStringProperty(status);
    }

    // --- Getters (required by PropertyValueFactory) ---
    public String getId()     { return id.get(); }
    public String getName()   { return name.get(); }
    public String getDate()   { return date.get(); }
    public String getStatus() { return status.get(); }

    // --- Property accessors (required for live TableView binding) ---
    public StringProperty idProperty()     { return id; }
    public StringProperty nameProperty()   { return name; }
    public StringProperty dateProperty()   { return date; }
    public StringProperty statusProperty() { return status; }
}
