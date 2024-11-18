package com.example.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LogbookController {

    @FXML
    private TableView<LogEntry> logTable;

    @FXML
    private TableColumn<LogEntry, Integer> columnId;

    @FXML
    private TableColumn<LogEntry, String> columnUsername;

    @FXML
    private TableColumn<LogEntry, String> columnLoginTime;


    @FXML
    private Button btn_back1;

    private ObservableList<LogEntry> logData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        btn_back1.setOnAction(actionEvent -> loadPrevious());
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));


        columnLoginTime.setCellValueFactory(cellData -> {
            LocalDateTime loginTime = cellData.getValue().getLoginTime();

            return new javafx.beans.property.SimpleStringProperty(loginTime != null ? loginTime.toString() : "N/A");
        });


        loadLogbook();
    }

    private void loadLogbook() {
        try (Connection connection = Connect.connectDb();
             Statement statement = connection.createStatement()) {

            String query = "SELECT * FROM log";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {

                Timestamp timestamp = resultSet.getTimestamp("login_time");
                LocalDateTime loginTime = timestamp != null ? timestamp.toLocalDateTime() : null;


                LogEntry logEntry = new LogEntry(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        loginTime
                );
                logData.add(logEntry);
            }


            logTable.setItems(logData);

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

            btn_back1.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
