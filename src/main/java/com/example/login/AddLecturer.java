package com.example.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddLecturer implements Initializable {

    @FXML
    private TextField id;

    @FXML
    private TextField name;

    @FXML
    private TextField lec_password;

    @FXML
    private Button btn_add;

    @FXML
    private Button btn_back;

    private Connection connection;


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private boolean isDataDuplicate(String Id) {
        connection = Connect.connectDb();
        if (connection == null) {
            showAlert("Database Error", "Unable to connect to the database.");
            return false;
        }

        try {
            String query = "SELECT COUNT(*) FROM lecturers WHERE lecturer_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, Id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @FXML
    private void handleAddLecturer() {
        String Id = id.getText().trim();
        String Name = name.getText().trim();
        String Password = lec_password.getText().trim();

        if (Id.isEmpty() || Name.isEmpty() || Password.isEmpty()) {
            showAlert("Validation Error", "All fields are required. Please fill in all fields before submitting.");
            return;
        }

        if (isDataDuplicate(Id)) {
            showAlert("Duplicate Data", "A lecturer with this ID already exists in the database.");
            return;
        }
        if (isDataDuplicate(Password)) {
            showAlert("Duplicate Data", "A lecturer with this password already exists in the database.");
            return;
        }


        connection = Connect.connectDb();
        if (connection == null) {
            showAlert("Database Error", "Unable to connect to the database.");
            return;
        }

        try {
            String sql = "INSERT INTO lecturers (lecturer_id, name, password) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Id);
            statement.setString(2, Name);
            statement.setString(3, Password);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Add Lecturer");
                alert.setHeaderText("Lecturer Added");
                alert.setContentText("Lecturer " + Name + " has been added successfully!");
                alert.showAndWait();

                id.clear();
                name.clear();
                lec_password.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while adding the lecturer. Please try again.");
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btn_add.setOnAction(event -> handleAddLecturer());
        btn_back.setOnAction(event -> loadPrevious());
    }
}
