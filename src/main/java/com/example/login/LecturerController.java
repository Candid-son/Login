package com.example.login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LecturerController implements Initializable {

    @FXML private TextField Lecturer_ID;
    @FXML private Button Lecturer_LoginBtn;
    @FXML private PasswordField Lecturer_Password;
    @FXML private ComboBox<String> Lecturer_Select;
    @FXML private TextField lecturer_name;


    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Alert alert;

    private void ErrorMessage(String message){
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void SuccessMessage(String message){
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void logLogin(String username) {
        try {

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);


            connect = Connect.connectDb();
            String query = "INSERT INTO log (username, login_time) VALUES (?, ?)";


            prepare = connect.prepareStatement(query);
            prepare.setString(1, username);
            prepare.setString(2, timestamp);


            int rowsAffected = prepare.executeUpdate();

            if (rowsAffected > 0) {
                SuccessMessage("Login logged successfully in the database.");
            } else {
                ErrorMessage("Failed to log login in the database.");
            }

        } catch (Exception e) {
            System.out.println("An error occurred while logging the login in the database.");
            e.printStackTrace();
        }
    }

    @FXML
    public void loginAccount() {

        if (lecturer_name.getText().isEmpty() || Lecturer_ID.getText().isEmpty() || Lecturer_Password.getText().isEmpty()) {
            ErrorMessage("Please fill in the blanks");
        } else {
            String selectData = "SELECT * FROM lecturers WHERE name = ? and lecturer_id = ? and password = ?";
            connect = Connect.connectDb();
            try {
                prepare = connect.prepareStatement(selectData);
                prepare.setString(1, lecturer_name.getText());
                prepare.setString(2, Lecturer_ID.getText());
                prepare.setString(3, Lecturer_Password.getText());
                result = prepare.executeQuery();

                if (result.next()) {
                    SuccessMessage("Login successful");

                    logLogin(lecturer_name.getText());

                        FXMLLoader loader= new FXMLLoader(getClass().getResource("LecturerFillForm.fxml"));
                        Parent root = loader.load();

                        LecturerFillForm lecturerFillFormController = loader.getController();
                        lecturerFillFormController.setLoginDetails(lecturer_name.getText(), Lecturer_ID.getText(), Lecturer_Password.getText());

                        Stage stage = new Stage();
                        stage.setTitle("Lecturer form");
                        stage.setScene(new Scene(root));
                        stage.show();

                    Lecturer_LoginBtn.getScene().getWindow().hide();

            }else {
                    ErrorMessage("Incorrect UserName/Password");
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    @FXML
    public void switchform() {
        try {
            Parent root = null;
            if (Lecturer_Select.getSelectionModel().getSelectedItem().equals("Admin Portal")) {
                root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            } else if (Lecturer_Select.getSelectionModel().getSelectedItem().equals("Principal Lecturer Portal")) {
                root = FXMLLoader.load(getClass().getResource("PrincipalLecturerPortal.fxml"));
            } else if (Lecturer_Select.getSelectionModel().getSelectedItem().equals("Lecturer Portal")) {
                root = FXMLLoader.load(getClass().getResource("Lecturer.fxml"));
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            Lecturer_Select.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> users;
    public LecturerController() {
        users = new ArrayList<>();
        users.add("Admin Portal");
        users.add("Principal Lecturer Portal");
        users.add("Lecturer Portal");
    }

    public void selectuser() {
        List<String> ListU = new ArrayList<>(users);
        ObservableList<String> ListData = FXCollections.observableArrayList(ListU);
        Lecturer_Select.setItems(ListData);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectuser();

    }
}
