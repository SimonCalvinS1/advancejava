package DoctorAppointmentManager.subproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public class DoctorRecordManagerFX extends Application {

    private static final String URL = "jdbc:mysql://localhost:3306/doctor_db";
    private static final String USER = "newuser";
    private static final String PASS = "password123";

    private Connection con;
    private ResultSet rs;

    private TextField docIdField, docNameField, docSpecField, patNameField;
    private ImageView imageView;
    private TextArea metaArea, displayArea;
    private Label networkLabel;
    private String generatedOTP;

    @Override
    public void start(Stage stage) {
        checkNetworkInfo();
        checkRemoteStatus("http://google.com"); // Example: checking connectivity to a web service

        connectDB();

        String cardStyle = "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";
        String btnStyle = "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;";
        String accentBtn = "-fx-background-color: #16a085; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;";

        networkLabel = new Label("Network Status: Initializing...");
        networkLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");

        // DOCTOR CARD
        VBox docCard = new VBox(10);
        docCard.setStyle(cardStyle);
        docCard.setPadding(new Insets(15));
        docIdField = new TextField(); docIdField.setPromptText("ID (Auto)"); docIdField.setEditable(false);
        docNameField = new TextField(); 
        docSpecField = new TextField();
        
        imageView = new ImageView();
        imageView.setFitHeight(100); imageView.setFitWidth(100);
        StackPane imgFrame = new StackPane(imageView);
        imgFrame.setStyle("-fx-border-color: #bdc3c7; -fx-background-color: #f9f9f9;");

        HBox docNav = new HBox(5, createBtn("|<", e->move("first")), createBtn("<", e->move("prev")), 
                                 createBtn(">", e->move("next")), createBtn(">|", e->move("last")));
        
        HBox docActions = new HBox(10, createBtn("Insert", e->insertDoctor(), accentBtn), 
                                      createBtn("Update", e->updateDoctor(), accentBtn),
                                      createBtn("Photo", e->uploadPhoto(stage), "#f39c12"));

        docCard.getChildren().addAll(new Label("DOCTOR MANAGEMENT"), docIdField, docNameField, docSpecField, imgFrame, docNav, docActions);

        // PATIENT CARD
        VBox patCard = new VBox(10);
        patCard.setStyle(cardStyle);
        patCard.setPadding(new Insets(15));
        patNameField = new TextField(); patNameField.setPromptText("Patient Name");
        patCard.getChildren().addAll(new Label("PATIENT REGISTRATION"), patNameField, createBtn("Register Patient", e->insertPatient(), accentBtn));

        // VIEW CARD
        VBox displayCard = new VBox(10);
        displayCard.setStyle(cardStyle);
        displayCard.setPadding(new Insets(15));
        displayArea = new TextArea(); displayArea.setEditable(false); displayArea.setPrefHeight(150);
        
        HBox viewActions = new HBox(10, 
            createBtn("View Doctors", e->validateAndRun(()->showTable("doctors"))),
            createBtn("View Patients", e->validateAndRun(()->showTable("patients"))),
            createBtn("View Schedule", e->validateAndRun(()->showAppointments()))
        );
        displayCard.getChildren().addAll(new Label("RECORDS VIEW"), displayArea, viewActions);

        // METADATA
        metaArea = new TextArea(); metaArea.setEditable(false); metaArea.setPrefHeight(80);

        VBox root = new VBox(15, networkLabel, new HBox(15, docCard, patCard), displayCard, new Label("Metadata"), metaArea);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #ecf0f1;");

        loadInitialData();

        stage.setScene(new Scene(root, 920, 780));
        stage.setTitle("Doctor Management System (Secure Networked)");
        stage.show();
    }

    private void checkNetworkInfo() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String info = "Connected from Host: " + localHost.getHostName() + " (IP: " + localHost.getHostAddress() + ")";
            System.out.println(info);
            // networkLabel will be updated once start() finishes or via Platform.runLater
        } catch (UnknownHostException e) {
            System.err.println("Could not determine local network info.");
        }
    }

    private void checkRemoteStatus(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();
            System.out.println("External Service Status: ONLINE, URL: " + url);
        } catch (Exception e) {
            System.err.println("External Service Status: OFFLINE (" + e.getMessage() + ")");
        }
    }

    // OTP & SECURITY
    private void validateAndRun(Runnable action) {
        generatedOTP = String.format("%04d", new Random().nextInt(10000));
        System.out.println(">> SECURITY ALERT: Your Access OTP is: " + generatedOTP);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("OTP Verification");
        dialog.setHeaderText("Identity Verification Required");
        dialog.setContentText("Enter the 4-digit code shown in the console:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && result.get().equals(generatedOTP)) {
            action.run();
        } else {
            showAlert("Denied", "Access verification failed.");
        }
    }

    // DB OPERATIONS
    private void connectDB() {
        try {
            con = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadInitialData() {
        try {
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = st.executeQuery("SELECT * FROM doctors");
            if (rs.next()) displayCurrentDoctor();
            displayMetaData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void insertDoctor() {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO doctors (name, specialization) VALUES (?, ?)");
            ps.setString(1, docNameField.getText());
            ps.setString(2, docSpecField.getText());
            ps.executeUpdate();
            loadInitialData();
            showAlert("Success", "New Doctor Added.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void insertPatient() {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO patients (name) VALUES (?)");
            ps.setString(1, patNameField.getText());
            ps.executeUpdate();
            showAlert("Success", "Patient Registered.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateDoctor() {
        try {
            if (rs == null || rs.getRow() == 0) return;
            rs.updateString("name", docNameField.getText());
            rs.updateString("specialization", docSpecField.getText());
            rs.updateRow();
            showAlert("Success", "Record Updated.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void uploadPhoto(Stage stage) {
        try {
            File file = new FileChooser().showOpenDialog(stage);
            if (file == null) return;
            rs.updateBinaryStream("photo", new FileInputStream(file), (int) file.length());
            rs.updateRow();
            displayCurrentDoctor();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void move(String dir) {
        try {
            boolean rowExists = switch(dir) {
                case "first" -> rs.first();
                case "last" -> rs.last();
                case "next" -> rs.isLast() ? rs.last() : rs.next();
                case "prev" -> rs.isFirst() ? rs.first() : rs.previous();
                default -> false;
            };
            if (rowExists) displayCurrentDoctor();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void displayCurrentDoctor() throws SQLException {
        docIdField.setText(rs.getString("id"));
        docNameField.setText(rs.getString("name"));
        docSpecField.setText(rs.getString("specialization"));
        Blob b = rs.getBlob("photo");
        if (b != null) imageView.setImage(new Image(new ByteArrayInputStream(b.getBytes(1, (int)b.length()))));
        else imageView.setImage(null);
    }

    private void showTable(String table) {
        try (Statement st = con.createStatement(); ResultSet r = st.executeQuery("SELECT * FROM " + table)) {
            StringBuilder sb = new StringBuilder("--- " + table.toUpperCase() + " ---\n");
            while (r.next()) {
                sb.append("ID: ").append(r.getString(1)).append(" | Name: ").append(r.getString(2)).append("\n");
            }
            displayArea.setText(sb.toString());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAppointments() {
        try {
            String q = "SELECT d.name, p.name, a.date_time FROM appointments a JOIN doctors d ON a.doctor_id = d.id JOIN patients p ON a.patient_id = p.id";
            ResultSet r = con.createStatement().executeQuery(q);
            StringBuilder sb = new StringBuilder("--- APPOINTMENTS ---\n");
            while (r.next()) {
                sb.append("DR: ").append(r.getString(1)).append(" -> PT: ").append(r.getString(2)).append(" (").append(r.getString(3)).append(")\n");
            }
            displayArea.setText(sb.toString());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void displayMetaData() {
        try {
            ResultSetMetaData md = rs.getMetaData();
            metaArea.setText("Total Columns: " + md.getColumnCount() + "\n");
            for(int i=1; i<=md.getColumnCount(); i++) metaArea.appendText(md.getColumnName(i) + "[" + md.getColumnTypeName(i) + "] ");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Button createBtn(String t, javafx.event.EventHandler<javafx.event.ActionEvent> h) {
        return createBtn(t, h, "-fx-background-color: #2980b9; -fx-text-fill: white;");
    }

    private Button createBtn(String t, javafx.event.EventHandler<javafx.event.ActionEvent> h, String s) {
        Button b = new Button(t); b.setStyle(s); b.setOnAction(h); return b;
    }

    private void showAlert(String h, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(h); a.setContentText(m); a.show();
    }

    public static void main(String[] args) { launch(args); }
}