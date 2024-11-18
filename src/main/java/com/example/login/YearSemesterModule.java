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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class YearSemesterModule implements Initializable {

    @FXML
    private ComboBox<String> combo_newyear;

    @FXML
    private Button btn_back;

    @FXML
    private Button btn_add;

    @FXML
    private Label year;

    @FXML
    private ComboBox<String> Newsemester;

    @FXML
    private ComboBox<String> semesterStart;

    @FXML
    private ComboBox<String> semesterEnd;

    @FXML
    private Label semester;

    @FXML
    private Button btn_save;

    @FXML
    private Button Btn_Add;


    @FXML
    private TextField start_date;

    @FXML
    private TextField end_date;


    @FXML
    private ComboBox<String> combo_AddModule;

    @FXML
    private TextField txt_Credits;

    private Connection connect;
    private Alert alert;

    private final String[] modules = {
            "Java Programming II",
            "Strategic Marketing Management",
            "Internet Payment",
            "Software Project Management",
            "Decision Support System",
            "E-Commerce Systems",
            "Concepts of Organisation",
            "Major Project"
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

    @FXML
    private void addModule() {
        String selectedModule = combo_AddModule.getSelectionModel().getSelectedItem();
        String creditsStr = txt_Credits.getText();

        if (selectedModule == null || creditsStr.isEmpty()) {
            ErrorMessage("Please select a module and enter credits.");
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsStr);
        } catch (NumberFormatException e) {
            ErrorMessage("Invalid credits value. Please enter a number.");
            return;
        }


        String insertModuleSQL = "INSERT INTO modules (module_name, credits) VALUES (?, ?)";

        try {
            connect = Connect.connectDb();
            PreparedStatement prepare = connect.prepareStatement(insertModuleSQL);
            prepare.setString(1, selectedModule);
            prepare.setInt(2, credits);
            int result = prepare.executeUpdate();

            if (result > 0) {
                SuccessMessage("Module added successfully: " + selectedModule);

                combo_AddModule.getSelectionModel().clearSelection();

                txt_Credits.clear();
            } else {
                ErrorMessage("Failed to add the module.");
            }
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

    @FXML
    private void addYear() {
        String selectedYear = combo_newyear.getValue();

        if (selectedYear != null) {

            if (isDataDuplicate(selectedYear)) {
                ErrorMessage("Year already exists!");
                return;
            } else {
                saveYearToDatabase(selectedYear);
            }
        } else {
            ErrorMessage("Please select a year to add.");
        }
    }

    private boolean isDataDuplicate(String year) {
        String query = "SELECT COUNT(*) FROM years WHERE year = ?";
        try {
            connect = Connect.connectDb();
            PreparedStatement prepare = connect.prepareStatement(query);
            prepare.setString(1, year);
            ResultSet resultSet = prepare.executeQuery();


            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveYearToDatabase(String year) {
        String query = "INSERT INTO years (year) VALUES (?)";
        try {
            connect = Connect.connectDb();
            PreparedStatement prepare = connect.prepareStatement(query);
            prepare.setString(1, year);
            prepare.executeUpdate();
            SuccessMessage("Year added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void saveSemesterData() {
        String semester = Newsemester.getValue();
        String startdate = start_date.getText();
        String enddate = end_date.getText();


        if (semester == null || startdate.isEmpty() || enddate.isEmpty()) {
            ErrorMessage("Please select a semester and enter start and end date for that semester.");
            return;
        }

        if(isDataDuplicate(startdate) || isDataDuplicate(enddate)){
            ErrorMessage("that start and end date for that semester already exists!!!");
        }

        String semesterdate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate date = null;
        boolean validDate = false;

        try{
            date = LocalDate.parse(startdate, formatter);
            date = LocalDate.parse(enddate, formatter);
            validDate = true;
        }catch (DateTimeParseException e) {
            ErrorMessage("Invalid date format. Please use yyyy-MM-dd.");
        }


        String query = "INSERT INTO semester (semester_name, start_date, end_date) VALUES (?, ?, ?)";

        try (Connection connect = Connect.connectDb();
             PreparedStatement prepare = connect.prepareStatement(query)) {

            prepare.setString(1, semester);
            prepare.setString(2, startdate);
            prepare.setString(3, enddate);

            int rowsInserted = prepare.executeUpdate();
            if (rowsInserted > 0) {
                SuccessMessage("Semester data saved successfully!");

                start_date.clear();
                end_date.clear();
            } else {
                ErrorMessage("Failed to save semester data.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> moduleList = FXCollections.observableArrayList(modules);
        combo_AddModule.setItems(moduleList);


        Btn_Add.setOnAction(event -> addModule());

        btn_back.setOnAction(event -> loadPrevious());
        btn_add.setOnAction(event -> addYear());


        for (int startYear = 2010; startYear <= 2030; startYear++) {
            int endYear = startYear + 1;
            combo_newyear.getItems().add(startYear + "/" + endYear);
        }

        ObservableList<String> semesterOptions = FXCollections.observableArrayList("S1", "S2");
        Newsemester.setItems(semesterOptions);

        btn_save.setOnAction(event -> saveSemesterData());
    }
}
