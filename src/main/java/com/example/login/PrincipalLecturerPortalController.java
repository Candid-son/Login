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

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PrincipalLecturerPortalController implements Initializable {

    @FXML
    private TextField PrincipalLecturer_ID;

    @FXML
    private Button PrincipalLecturer_LoginBtn;

    @FXML
    private PasswordField PrincipalLecturer_Password;

    @FXML
    private ComboBox<String> PrincipalLecturer_Select;


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

    public void loginAccount(){

        if(PrincipalLecturer_ID.getText().isEmpty() || PrincipalLecturer_Password.getText().isEmpty()){
            ErrorMessage("Please fill in the blanks");
        } else{

            String selectData = "SELECT * FROM principal WHERE username = ? and Password = ?";

            connect = Connect.connectDb();

            try{
                prepare = connect.prepareStatement(selectData);
                prepare.setString(1, PrincipalLecturer_ID.getText());
                prepare.setString(2, PrincipalLecturer_Password.getText());

                result = prepare.executeQuery();

                if(result.next()){
                    SuccessMessage("Login successful");
                    logLogin(PrincipalLecturer_ID.getText());


                    Parent root = FXMLLoader.load(getClass().getResource("Principaldash.fxml"));

                    Stage stage = new Stage();
                    stage.setTitle("Principal Lecturer form");
                    stage.setScene(new Scene(root));

                    stage.show();

                    PrincipalLecturer_LoginBtn.getScene().getWindow().hide();
                } else{
                    ErrorMessage("Incorrect UserName/Password");
                }
            } catch(Exception e){
                e.printStackTrace();
            }


        }

    }

    public void switchform(){
        try{
            Parent root = null;
            if (PrincipalLecturer_Select.getSelectionModel().getSelectedItem().equals("Lecturer Portal")){
                root = FXMLLoader.load(getClass().getResource("Lecturer.fxml"));
            } else if (PrincipalLecturer_Select.getSelectionModel().getSelectedItem().equals("Admin Portal")) {
                root =  FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            } else if(PrincipalLecturer_Select.getSelectionModel().getSelectedItem().equals("Principal Lecturer Portal")) {
                root = FXMLLoader.load(getClass().getResource("PrincipalLecturerPortal.fxml"));
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.show();

            PrincipalLecturer_Select.getScene().getWindow().hide();
        }  catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<String> users;

    public PrincipalLecturerPortalController(){
        users = new ArrayList<>();
        users.add("Admin Portal");
        users.add("Principal Lecturer Portal");
        users.add("Lecturer Portal");
    }

    public void selectuser(){
        List<String> ListU = new ArrayList<>();

        for (String data : users) {
            ListU.add(data);
        }

        ObservableList<String> ListData = FXCollections.observableArrayList(ListU);
        PrincipalLecturer_Select.setItems(ListData);
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectuser();
    }
}
