package com.example.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LecturerFillForm implements Initializable {
    @FXML
    private Label ID;

    @FXML
    private Label Passw;

    @FXML
    private Label lec;

    @FXML
    private TextField Lecname;

    @FXML
    private TextField Lecture_ID;

    @FXML
    private TextField password;

    @FXML
    private ComboBox<Integer> weekComboBox;

    @FXML
    private TextField chapterField;

    @FXML
    private TextArea learningOutcomesArea;

    @FXML
    private Button saveButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button btn_logout;

    private Connection connection;

    public void setLoginDetails(String lecturerName, String lecturerID, String lecturerPassword) {
        Lecname.setText(lecturerName);
        Lecture_ID.setText(lecturerID);
        password.setText(lecturerPassword);
    }


    private boolean isDataDuplicate(String chapter, Integer week) {
        try {
            String query = "SELECT COUNT(*) FROM LecturerFillForm WHERE chapter = ? AND week = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, chapter);
            preparedStatement.setInt(2, week);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @FXML
    void openAttendanceForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("attendanceform.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Mark Attendance");
            stage.setScene(new Scene(root));
            stage.show();

            saveButton.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
   public void saveChapterAndOutcomes() {
        String chapter = chapterField.getText();
        String learningOutcomes = learningOutcomesArea.getText();
        Integer week = weekComboBox.getValue();
        String lecturerID = Lecture_ID.getText();
        String lecturerPassword = password.getText();
        String lecturerName = Lecname.getText();


        if (validateInputs(chapter, learningOutcomes, week, lecturerID, lecturerPassword, lecturerName)) {

            if (isDataDuplicate(chapter, week)) {
                showAlert("Duplicate Data", "This data already exists in the database.");
            } else {
                try {
                    String query = "INSERT INTO LecturerFillForm (week, chapter, learning_outcomes, lecturer_id, password, name) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, week);
                    preparedStatement.setString(2, chapter);
                    preparedStatement.setString(3, learningOutcomes);
                    preparedStatement.setString(4, lecturerID);
                    preparedStatement.setString(5, lecturerPassword);
                    preparedStatement.setString(6, lecturerName);

                    preparedStatement.executeUpdate();
                    statusLabel.setText("Form saved successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean validateInputs(String chapter, String learningOutcomes, Integer week, String lecturerID, String lecturerPassword, String lecturerName) {
        if (chapter.isEmpty() || learningOutcomes.isEmpty() || week == null || lecturerID.isEmpty() || lecturerPassword.isEmpty() || lecturerName == null) {
            showAlert("Input Error", "All fields must be filled.");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void loadPrevious() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Lecturer.fxml"));
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connection = Connect.connectDb();

        if (connection != null) {
            for (int i = 1; i <= 17; i++) {
                weekComboBox.getItems().add(i);
            }
        } else {
            showAlert("Database Connection Error", "Failed to connect to the database.");
        }

        btn_logout.setOnAction(event -> loadPrevious());
    }
}
