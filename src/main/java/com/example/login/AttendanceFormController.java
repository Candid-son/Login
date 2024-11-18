package com.example.login;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AttendanceFormController implements Initializable {

    @FXML
    private TableView<Student> attendanceTable;

    @FXML
    private TableColumn<Student, String> idColumn;

    @FXML
    private TableColumn<Student, String> nameColumn;

    @FXML
    private TableColumn<Student, Boolean> presentColumn;

    @FXML
    private Button saveButton;

    @FXML
    private Button btn_logout;

    private ObservableList<Student> studentList;

    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectDb();
        initializeTable();
        loadStudents();
    }

    private void connectDb() {

        connection = Connect.connectDb();
        if (connection == null) {
            showAlert("Error", "Failed to connect to the database.");
        }
    }

    @FXML
    private void loadPrevious() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LecturerFillForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Lecturer Login Portal");
            stage.setScene(new Scene(root));
            stage.show();

            btn_logout.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        presentColumn.setCellValueFactory(cellData -> cellData.getValue().presentProperty());
        presentColumn.setCellFactory(column -> new TableCell<Student, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        getTableView().getItems().get(getIndex()).setPresent(newValue);
                    });
                    setGraphic(checkBox);
                }
            }
        });

        btn_logout.setOnAction(actionEvent -> loadPrevious());
    }

    private void loadStudents() {
        studentList = FXCollections.observableArrayList(
                new Student("901015656", "Bokang Masupha"),
                new Student("901012345", "Toka Tsenoli"),
                new Student("901067898", "Moesa Retselisitsoe"),
                new Student("901012789", "Mosiuoa Nkhothu"),
                new Student("901098765", "Nteboheleng Mahoko")
        );
        attendanceTable.setItems(studentList);
    }

    @FXML
    private void saveAttendance() {
        String query = "INSERT INTO attendance (student_id, student_name, present) VALUES (?, ?, ?)";
        try {
            for (Student student : studentList) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, student.getStudentId());
                preparedStatement.setString(2, student.getStudentName());
                preparedStatement.setBoolean(3, student.isPresent());
                preparedStatement.executeUpdate();
            }
            showAlert("Success", "Attendance saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save attendance.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Student {
        private final StringProperty studentId;
        private final StringProperty studentName;
        private final BooleanProperty present;

        public Student(String studentId, String studentName) {
            this.studentId = new SimpleStringProperty(studentId);
            this.studentName = new SimpleStringProperty(studentName);
            this.present = new SimpleBooleanProperty(false);
        }

        public String getStudentId() {
            return studentId.get();
        }

        public String getStudentName() {
            return studentName.get();
        }

        public boolean isPresent() {
            return present.get();
        }

        public void setPresent(boolean present) {
            this.present.set(present);
        }

        public BooleanProperty presentProperty() {
            return present;
        }
    }
}
