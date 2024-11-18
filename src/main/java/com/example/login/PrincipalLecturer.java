package com.example.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PrincipalLecturer implements Initializable {

    @FXML
    private Label chall_id;

    @FXML
    private TextArea chall_textarea_id;

    @FXML
    private ComboBox<String> class_id;

    @FXML
    private ComboBox<String> module_id;

    @FXML
    private Label recomm_id;

    @FXML
    private TextArea recomm_textarea_id;

    @FXML
    private Button submit_id;

    @FXML
    private ComboBox<String> week_id;

    @FXML
    private Button btn_back;

    @FXML
    private ListView<String> lecturerListView;

    @FXML
    private TextArea lecturerFormDetails;

    private Connection connection;

    private ObservableList<String> lecturerNames = FXCollections.observableArrayList();

    // Initialize the connection in the constructor or an initialization method
    private void initializeConnection() {
        connection = Connect.connectDb();  // Establish the database connection
        if (connection == null) {
            System.err.println("Connection failed!");  // Handle the error if connection is not established
        }
    }

    private boolean isDataDuplicate(String challenge, String recommendation, String module, String week) {
        try {
            String query = "SELECT COUNT(*) FROM principallecturer WHERE challenge = ? AND recommendation = ? AND module_id = ? AND week_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, challenge);
            preparedStatement.setString(2, recommendation);
            preparedStatement.setString(3, module);
            preparedStatement.setString(4, week);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Returns true if there is a duplicate
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void populateComboBoxes() {
        ObservableList<String> classes = FXCollections.observableArrayList("BSCBIT", "BSCIT", "BSCSM");
        class_id.setItems(classes);

        ObservableList<String> modules = FXCollections.observableArrayList("Java Programming", "E commerce system", "Software project management", "Decision support system", "Internet Payment");
        module_id.setItems(modules);

        ObservableList<String> weeks = FXCollections.observableArrayList("Wk 1", "Wk 2", "Wk 3", "Wk 4", "Wk 5", "Wk 6", "Wk 7", "Wk 8", "Wk 9", "Wk 10", "Wk 11", "Wk 12", "Wk 13", "Wk 14", "Wk 15", "Wk 16", "Wk 17");
        week_id.setItems(weeks);
    }

    private void populateLecturerListView() {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM lecturerfillform")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                lecturerNames.add(resultSet.getString("name"));
            }

            lecturerListView.setItems(lecturerNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveDataToDatabase(String challenge, String recommendation, String classId, String moduleId, String weekId) {
        String query = "INSERT INTO PrincipalLecturer (challenge, recommendation, class_id, module_id, week_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, challenge);
            preparedStatement.setString(2, recommendation);
            preparedStatement.setString(3, classId);
            preparedStatement.setString(4, moduleId);
            preparedStatement.setString(5, weekId);

            preparedStatement.executeUpdate();
            System.out.println("Data saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonSubmitReport(ActionEvent event) {
        String challengeText = chall_textarea_id.getText();
        String recommendationText = recomm_textarea_id.getText();
        String selectedClass = class_id.getValue();
        String selectedModule = module_id.getValue();
        String selectedWeek = week_id.getValue();

        if (selectedClass == null || selectedModule == null || selectedWeek == null) {
            showAlert("Missing Information", "Please select a class, module, and week before submitting.");
            return;
        }

        boolean isDuplicate = isDataDuplicate(challengeText, recommendationText, selectedModule, selectedWeek);

        if (!isDuplicate) {
            saveDataToDatabase(challengeText, recommendationText, selectedClass, selectedModule, selectedWeek);
            System.out.println("Data saved successfully!");
        } else {
            showAlert("Duplicate Entry", "The data you entered already exists (excluding the class).");
        }

        // Clear fields after submission
        chall_textarea_id.clear();
        recomm_textarea_id.clear();
        class_id.getSelectionModel().clearSelection();
        module_id.getSelectionModel().clearSelection();
        week_id.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void loadPrevious() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PrincipalLecturerPortal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Principal Portal");
            stage.setScene(new Scene(root));
            stage.show();

            btn_back.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadLecturerFormDetails(String lecturerName) {
        String query = "SELECT * FROM lecturerfillform WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, lecturerName);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String chapter = resultSet.getString("chapter");
                String learningOutcomes = resultSet.getString("learning_outcomes");
                String week = resultSet.getString("week");

                lecturerFormDetails.setText(
                                "Chapter: " + chapter + "\n" +
                                "Learning outcomes: " + learningOutcomes + "\n" +
                                "Week: " + week
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeConnection();  // Initialize the database connection
        populateComboBoxes();
        populateLecturerListView();

        lecturerListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadLecturerFormDetails(newValue);
            }
        });

        btn_back.setOnAction(event -> loadPrevious());
    }
}
