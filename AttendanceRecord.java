package OOP;

import javafx.beans.property.SimpleStringProperty;

public class AttendanceRecord {
    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty date;
    private final SimpleStringProperty status;

    public AttendanceRecord(String id, String name, String date, String status) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.date = new SimpleStringProperty(date);
        this.status = new SimpleStringProperty(status);
    }

    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getDate() { return date.get(); }
    public String getStatus() { return status.get(); }
}