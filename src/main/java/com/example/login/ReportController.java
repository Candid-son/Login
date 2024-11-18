package com.example.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportController {
    @FXML
    private Button btn_back;

    @FXML
    private TableView<Report> reportTable;

    @FXML
    private TableColumn<Report, Integer> columnChallenge;

    @FXML
    private TableColumn<Report, String> columnRecommendation;

    @FXML
    private TableColumn<Report, String> columnClassId;

    @FXML
    private TableColumn<Report, String> columnModuleId;

    @FXML
    private TableColumn<Report, String> columnWeekId;

    private ObservableList<Report> reportData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        btn_back.setOnAction(actionEvent -> loadPrevious());
        columnChallenge.setCellValueFactory(new PropertyValueFactory<>("challenge"));
        columnRecommendation.setCellValueFactory(new PropertyValueFactory<>("recommendation"));
        columnClassId.setCellValueFactory(new PropertyValueFactory<>("classId"));
        columnModuleId.setCellValueFactory(new PropertyValueFactory<>("moduleId"));
        columnWeekId.setCellValueFactory(new PropertyValueFactory<>("weekId"));

        loadReports();
    }

    @FXML
    private void loadReports() {
        try (Connection connection = Connect.connectDb();
             Statement statement = connection.createStatement()) {

            String query = "SELECT * FROM principallecturer";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                // Retrieve data from the ResultSet
                int challenge = resultSet.getInt("challenge");
                String recommendation = resultSet.getString("recommendation");
                String classId = resultSet.getString("class_id");
                String moduleId = resultSet.getString("module_id");
                String weekId = resultSet.getString("week_id");

                // Create a Report object
                Report reportEntry = new Report(challenge, recommendation, classId, moduleId, weekId);
                reportData.add(reportEntry);
            }

            // Set the data to the TableView
            reportTable.setItems(reportData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadPrevious() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Admin Portal");
            stage.setScene(new Scene(root));
            stage.show();

            btn_back.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
