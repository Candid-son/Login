package com.example.login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
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

import com.example.login.admin;


public class HelloController implements  Initializable{

    @FXML
    private PasswordField Admin_Password;

    @FXML
    private ComboBox<String> Admin_User;

    @FXML
    private TextField Admin_UserName;

    @FXML
    private Button Admin_loginBtn;

    private List<String> users;



    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private Alert alert;

    private String loggedInUsername; 
    private String loggedInPassword;



    private void ErrorMessage(String message){
         alert = new Alert(AlertType.ERROR);
         alert.setTitle("Error Message");
         alert.setHeaderText(null);
         alert.setContentText(message);
         alert.showAndWait();
    }

    private void SuccessMessage(String message){
        alert = new Alert(AlertType.INFORMATION);
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



    public void loginAccount() {
        if (Admin_UserName.getText().isEmpty() || Admin_Password.getText().isEmpty()) {
            ErrorMessage("Please fill in the blanks");
        } else {
            String selectData = "SELECT * FROM admin WHERE UserName = ? and Password = ?";

            connect = Connect.connectDb();

            try {
                prepare = connect.prepareStatement(selectData);
                prepare.setString(1, Admin_UserName.getText());
                prepare.setString(2, Admin_Password.getText());

                result = prepare.executeQuery();

                if (result.next()) {

                    logLogin(Admin_UserName.getText());

                    loggedInUsername = Admin_UserName.getText();
                    loggedInPassword = Admin_Password.getText();
                    SuccessMessage("Login successful");


                    FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
                    Parent root = loader.load();


                    admin dashboardController = loader.getController();
                    dashboardController.setLoggedInCredentials(loggedInUsername, loggedInPassword);


                    Stage stage = new Stage();
                    stage.setTitle("Admin form");
                    stage.setScene(new Scene(root));
                    stage.show();


                    Admin_loginBtn.getScene().getWindow().hide();
                } else {
                    ErrorMessage("Incorrect UserName/Password");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void viewProfile(String username, String password) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminProfile.fxml"));
            Parent root = loader.load();


            AdminProfileController profileController = loader.getController();
            profileController.setAdminCredentials(username, password);


            profileController.loadProfile();


            Stage stage = new Stage();
            stage.setTitle("Admin Profile");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void switchform(){
        try{
            Parent root = null;
            if (Admin_User.getSelectionModel().getSelectedItem().equals("Principal Lecturer Portal")){
                root = FXMLLoader.load(getClass().getResource("PrincipalLecturerPortal.fxml"));
            } else if (Admin_User.getSelectionModel().getSelectedItem().equals("Lecturer Portal")) {
                root =  FXMLLoader.load(getClass().getResource("Lecturer.fxml"));
            } else if(Admin_User.getSelectionModel().getSelectedItem().equals("Admin Portal")) {
                root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.show();

            Admin_User.getScene().getWindow().hide();
        }  catch (Exception e){
            e.printStackTrace();
        }
    }


    public HelloController() {
        users = new ArrayList<>();
        users.add("Admin Portal");
        users.add("Principal Lecturer Portal");
        users.add("Lecturer Portal");
    }

   public <observableList> void SelectUser(){
       List<String> ListU = new ArrayList<>();

       for (String data : users) {
           ListU.add(data);
       }

       ObservableList<String> ListData = FXCollections.observableArrayList(ListU);

       Admin_User.setItems(ListData);

   }





    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SelectUser();

    }
}