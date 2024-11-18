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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {

    @FXML
    private Button btn_register;

    @FXML
    private TextField new_name;

    @FXML
    private TextField new_id;

    @FXML
    private TextField new_password;

    @FXML
    private PasswordField confirm_Password;

    @FXML
    private ComboBox<String> select_account;

    @FXML
    private ComboBox<String> select_role;

    private Alert alert;
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private String loggedInUsername;
    private String loggedInPassword;

    private final String[] roles = {
            "admin",
            "principal lecturer",
            "lecturer"
    };

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

   private boolean isDataDuplicate(String Id) {
        String query = "SELECT COUNT(*) FROM lecturers WHERE lecturer_id = ?";
        try {
            connect = Connect.connectDb();
            PreparedStatement prepare = connect.prepareStatement(query);
            prepare.setString(1,Id);
            ResultSet resultSet = prepare.executeQuery();


            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isDuplicatePassword(String password) {
        String query = "SELECT COUNT(*) FROM lecturers WHERE password = ?";
        try {
            connect = Connect.connectDb();
            PreparedStatement prepare = connect.prepareStatement(query);
            prepare.setString(1, password);
            ResultSet resultSet = prepare.executeQuery();

            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isDuplicateIDPrincipal(String Id){
        String query = "SELECT COUNT(*) FROM principal WHERE principalID = ?";
        try{
            connect = Connect.connectDb();
            PreparedStatement prepare = connect.prepareStatement(query);
            prepare.setString(1, Id);
            ResultSet resultSet = prepare.executeQuery();

            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean isDuplicatePasswordPrincipal(String password) {
        String query = "SELECT COUNT(*) FROM principal WHERE Password = ?";
        try {
            connect = Connect.connectDb();
            PreparedStatement prepare = connect.prepareStatement(query);
            prepare.setString(1, password);
            ResultSet resultSet = prepare.executeQuery();

            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isDuplicateAdminID(String Id) {
        String query = "SELECT COUNT(*) FROM admin WHERE admin_id = ?";
        try {
            connect = Connect.connectDb();
            PreparedStatement prepare = connect.prepareStatement(query);
            prepare.setString(1,Id);
            ResultSet resultSet = prepare.executeQuery();


            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean isDuplicateAdmin(String password) {
        String query = "SELECT COUNT(*) FROM admin WHERE Password = ?";
        try {
            connect = Connect.connectDb();
            PreparedStatement prepare = connect.prepareStatement(query);
            prepare.setString(1, password);
            ResultSet resultSet = prepare.executeQuery();

            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void registerAccount() {
        String Id = new_id.getText().trim();
        String password = new_password.getText().trim();

        if (new_name.getText().isEmpty() || Id.isEmpty() || password.isEmpty() || confirm_Password.getText().isEmpty() || select_role.getSelectionModel().isEmpty()) {
            ErrorMessage("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirm_Password.getText())) {
            ErrorMessage("Passwords do not match.");
            return;
        }


        if (isDataDuplicate(Id)) {
            ErrorMessage("ID already exists in the Lecturers table.");
            return;
        }

        if (isDuplicatePassword(password)) {
            ErrorMessage("Password already exists in the Lecturers table.");
            return;
        }

        if (isDuplicateIDPrincipal(Id)) {
            ErrorMessage("ID already exists in the Principals table.");
            return;
        }

        if (isDuplicatePasswordPrincipal(password)) {
            ErrorMessage("Password already exists in the Principals table.");
            return;
        }

        if (isDuplicateAdminID(Id)) {
            ErrorMessage("ID already exists in the Admins table.");
            return;
        }

        if (isDuplicateAdmin(password)) {
            ErrorMessage("Password already exists in the Admins table.");
            return;
        }

        String selectedRole = select_role.getValue();
        String insertData;

        switch (selectedRole.toLowerCase()) {
            case "lecturer":
                insertData = "INSERT INTO lecturers (name, password, lecturer_id) VALUES (?, ?, ?)";
                break;
            case "admin":
                insertData = "INSERT INTO admin (UserName, Password, admin_id) VALUES (?, ?, ?)";
                break;
            case "principal lecturer":
                insertData = "INSERT INTO principal (username, Password, principalID) VALUES (?, ?, ?)";
                break;
            default:
                ErrorMessage("Invalid role selected.");
                return;
        }

        connect = Connect.connectDb();

        try {
            prepare = connect.prepareStatement(insertData);
            prepare.setString(1, new_name.getText());
            prepare.setString(2, new_password.getText());
            prepare.setString(3, new_id.getText());

            int rowsAffected = prepare.executeUpdate();

            if (rowsAffected > 0) {
                SuccessMessage("Registration successful");

                logLogin(new_name.getText());

                String fxmlFile = "";
                String title = "";

                switch (selectedRole.toLowerCase()) {
                    case "lecturer":
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("LecturerFillForm.fxml"));
                        Parent root = loader.load();

                        LecturerFillForm lecturerFillFormController = loader.getController();
                        lecturerFillFormController.setLoginDetails(new_name.getText(), new_id.getText(), new_password.getText());

                        Stage stage = new Stage();
                        stage.setTitle("Lecturer form");
                        stage.setScene(new Scene(root));
                        stage.show();

                        btn_register.getScene().getWindow().hide();
                        break;

                    case "admin":
                        loggedInUsername = new_name.getText();
                        loggedInPassword = new_password.getText();

                        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("admin.fxml"));
                        Parent root1 = loader1.load();

                        admin dashboardController = loader1.getController();
                        dashboardController.setLoggedInCredentials(loggedInUsername, loggedInPassword);

                        Stage stage1 = new Stage();
                        stage1.setTitle("admin form");
                        stage1.setScene(new Scene(root1));
                        stage1.show();

                        btn_register.getScene().getWindow().hide();
                        break;

                    case "principal lecturer":
                        fxmlFile = "Principaldash.fxml";
                        title = "Principal Lecturer Dashboard";
                        break;
                }

                Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                Stage stage = new Stage();
                stage.setTitle(title);
                stage.setScene(new Scene(root));
                stage.show();

                btn_register.getScene().getWindow().hide();

            } else {
                ErrorMessage("Registration failed. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void switchform() {
        try {
            Parent root = null;
            if (select_account.getSelectionModel().getSelectedItem().equals("Lecturer Portal")) {
                root = FXMLLoader.load(getClass().getResource("Lecturer.fxml"));
            } else if (select_account.getSelectionModel().getSelectedItem().equals("Admin Portal")) {
                root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            } else if (select_account.getSelectionModel().getSelectedItem().equals("Principal Lecturer Portal")) {
                root = FXMLLoader.load(getClass().getResource("PrincipalLecturerPortal.fxml"));
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            select_account.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private java.util.List<String> users;

    public RegistrationController() {
        users = new ArrayList<>();
        users.add("Admin Portal");
        users.add("Principal Lecturer Portal");
        users.add("Lecturer Portal");
    }

    public void selectuser() {
        List<String> ListU = new ArrayList<>();

        for (String data : users) {
            ListU.add(data);
        }

        ObservableList<String> ListData = FXCollections.observableArrayList(ListU);
        select_account.setItems(ListData);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> moduleList = FXCollections.observableArrayList(roles);
        select_role.setItems(moduleList);
        selectuser();
    }
}
