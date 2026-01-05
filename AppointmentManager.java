package DoctorAppointmentManager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;

public class AppointmentManager extends Application {

    // Database constants
    private static final String URL = "jdbc:mysql://localhost:3306/doctor_db";
    private static final String USER = "newuser";
    private static final String PASSWORD = "password123"; // Update with your password

    private Connection connection;
    private ResultSet resultSet;

    // UI Components
    private TextField txtId = new TextField();
    private TextField txtName = new TextField();
    private TextField txtSpecialization = new TextField();
    private ImageView imageView = new ImageView();
    private TextArea txtMetadata = new TextArea();
    private TableView<String[]> joinTable = new TableView<>();

    @Override
    public void start(Stage primaryStage) {
        initializeDB();

        // Main Layout
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // --- 1. Record Management Section ---
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("ID:"), 0, 0);
        form.add(txtId, 1, 0);
        txtId.setEditable(false);
        form.add(new Label("Doctor Name:"), 0, 1);
        form.add(txtName, 1, 1);
        form.add(new Label("Specialization:"), 0, 2);
        form.add(txtSpecialization, 1, 2);

        // Image Display
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);
        VBox imgBox = new VBox(5, new Label("Doctor Photo:"), imageView);

        // --- 2. Navigation Controls ---
        HBox navButtons = new HBox(10);
        Button btnFirst = new Button("<< First");
        Button btnPrev = new Button("< Prev");
        Button btnNext = new Button("Next >");
        Button btnLast = new Button("Last >>");
        navButtons.getChildren().addAll(btnFirst, btnPrev, btnNext, btnLast);
        navButtons.setAlignment(Pos.CENTER);

        // --- 3. CRUD & Image Controls ---
        HBox actionButtons = new HBox(10);
        Button btnUpdate = new Button("Update Record");
        Button btnUpload = new Button("Upload Image");
        actionButtons.getChildren().addAll(btnUpdate, btnUpload);
        actionButtons.setAlignment(Pos.CENTER);

        // --- 4. Join & Metadata Section ---
        Button btnShowJoin = new Button("Show Full Schedule (JOIN)");
        txtMetadata.setPrefHeight(100);
        txtMetadata.setEditable(false);

        // Layout Assembly
        root.getChildren().addAll(new Label("Doctor Records"), form, imgBox, navButtons, actionButtons,
                new Separator(), new Label("Metadata"), txtMetadata, btnShowJoin);

        // --- Event Handlers ---
        btnFirst.setOnAction(e -> moveCursor("first"));
        btnLast.setOnAction(e -> moveCursor("last"));
        btnNext.setOnAction(e -> moveCursor("next"));
        btnPrev.setOnAction(e -> moveCursor("previous"));

        btnUpdate.setOnAction(e -> updateRecord());
        btnUpload.setOnAction(e -> uploadImage(primaryStage));
        btnShowJoin.setOnAction(e -> performJoinOperation());

        // Load Initial Data
        loadRecords();

        Scene scene = new Scene(root, 600, 800);
        primaryStage.setTitle("Doctor Meeting Scheduler - Admin");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeDB() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRecords() {
        try {
            // Requirement 3: Scrollable and Updatable ResultSet
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            resultSet = stmt.executeQuery("SELECT id, name, specialization, photo FROM doctors");

            // Requirement 4: Metadata
            displayMetadata();

            if (resultSet.next()) {
                displayCurrentRecord();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayCurrentRecord() {
        try {
            txtId.setText(resultSet.getString("id"));
            txtName.setText(resultSet.getString("name"));
            txtSpecialization.setText(resultSet.getString("specialization"));

            // Requirement 5: Retrieve BLOB Image
            Blob blob = resultSet.getBlob("photo");
            if (blob != null) {
                InputStream is = blob.getBinaryStream();
                imageView.setImage(new Image(is));
            } else {
                imageView.setImage(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void moveCursor(String direction) {
        try {
            boolean hasRecord = false;
            switch (direction) {
                case "first" -> hasRecord = resultSet.first();
                case "last" -> hasRecord = resultSet.last();
                case "next" -> {
                    if (!resultSet.isLast())
                        hasRecord = resultSet.next();
                    else
                        hasRecord = true;
                }
                case "previous" -> {
                    if (!resultSet.isFirst())
                        hasRecord = resultSet.previous();
                    else
                        hasRecord = true;
                }
            }
            if (hasRecord)
                displayCurrentRecord();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRecord() {
        try {
            // Safety check: Ensure the cursor is on a valid row
            if (resultSet == null || resultSet.isAfterLast() || resultSet.isBeforeFirst()) {
                showAlert("Error", "No record selected to update. Please add a record to the database first.");
                return;
            }

            resultSet.updateString("name", txtName.getText());
            resultSet.updateString("specialization", txtSpecialization.getText());
            resultSet.updateRow();
            showAlert("Success", "Record updated in database!");
        } catch (SQLException e) {
            // If the table is empty, updateRow will throw an exception
            showAlert("Database Error", "Cannot update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void uploadImage(Stage stage) {
        // Safety check
        try {
            if (resultSet == null || resultSet.isAfterLast() || resultSet.isBeforeFirst()) {
                showAlert("Error", "No record selected. Cannot upload image to a non-existent record.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    resultSet.updateBinaryStream("photo", fis, (int) file.length());
                    resultSet.updateRow();
                    displayCurrentRecord();
                    showAlert("Success", "Image uploaded and saved!");
                } catch (Exception e) {
                    showAlert("File Error", "Error reading file: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayMetadata() {
        try {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            StringBuilder sb = new StringBuilder();
            int columnCount = rsmd.getColumnCount();
            sb.append("Total Columns: ").append(columnCount).append("\n");
            for (int i = 1; i <= columnCount; i++) {
                sb.append(rsmd.getColumnName(i)).append(" (")
                        .append(rsmd.getColumnTypeName(i)).append(")\n");
            }
            txtMetadata.setText(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void performJoinOperation() {
        // Requirement 6: JOIN Operation
        String query = "SELECT d.name AS Doctor, p.name AS Patient, a.date_time " +
                "FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "JOIN patients p ON a.patient_id = p.id";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            StringBuilder results = new StringBuilder("--- SCHEDULE ---\n");
            while (rs.next()) {
                results.append(String.format("Dr. %s with Patient %s on %s\n",
                        rs.getString("Doctor"), rs.getString("Patient"), rs.getString("date_time")));
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Joined Records");
            alert.setHeaderText("Appointment Schedule");
            alert.setContentText(results.toString());
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}