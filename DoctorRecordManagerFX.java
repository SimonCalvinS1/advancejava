package DoctorAppointmentManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;

public class DoctorRecordManagerFX extends Application {

    // DB
    private Connection con;
    private ResultSet rs;

    // UI Controls
    private TextField idField, nameField, specField;
    private TextArea metaArea, joinArea;
    private ImageView imageView;

    // Navigation
    private Button firstBtn, nextBtn, prevBtn, lastBtn, updateBtn, uploadBtn, joinBtn;

    @Override
    public void start(Stage stage) {
        connectDB();
        loadScrollableResultSet();

        idField = new TextField();
        nameField = new TextField();
        specField = new TextField();

        imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        metaArea = new TextArea();
        metaArea.setEditable(false);
        metaArea.setPrefHeight(120);

        joinArea = new TextArea();
        joinArea.setEditable(false);

        firstBtn = new Button("First");
        nextBtn = new Button("Next");
        prevBtn = new Button("Previous");
        lastBtn = new Button("Last");
        updateBtn = new Button("Update Record");
        uploadBtn = new Button("Upload Image");
        joinBtn = new Button("Show Appointments (JOIN)");

        firstBtn.setOnAction(e -> move("first"));
        nextBtn.setOnAction(e -> move("next"));
        prevBtn.setOnAction(e -> move("prev"));
        lastBtn.setOnAction(e -> move("last"));
        updateBtn.setOnAction(e -> updateRecord());
        uploadBtn.setOnAction(e -> uploadImage(stage));
        joinBtn.setOnAction(e -> showJoin());

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);

        form.addRow(0, new Label("Doctor ID:"), idField);
        form.addRow(1, new Label("Name:"), nameField);
        form.addRow(2, new Label("Specialization:"), specField);
        form.add(imageView, 2, 0, 1, 4);

        HBox nav = new HBox(10, firstBtn, prevBtn, nextBtn, lastBtn, updateBtn, uploadBtn);
        VBox root = new VBox(10,
                new Label("Doctor Record Manager (Scrollable ResultSet)"),
                form,
                nav,
                new Label("ResultSetMetaData"),
                metaArea,
                joinBtn,
                new Label("JOIN Result (Doctors + Patients)"),
                joinArea
        );

        root.setStyle("-fx-padding:15");
        try {
            displayRecord();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        displayMetaData();

        stage.setScene(new Scene(root, 900, 650));
        stage.setTitle("Advanced JavaFX + JDBC Demo");
        stage.show();
    }

    // ---------------- DB CONNECTION ----------------
    private void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/doctor_appointment",
                    "newuser",
                    "password123"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- SCROLLABLE RESULTSET ----------------
    private void loadScrollableResultSet() {
        try {
            Statement st = con.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            );
            rs = st.executeQuery("SELECT * FROM doctors");
            rs.first();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- NAVIGATION ----------------
    private void move(String type) {
        try {
            switch (type) {
                case "first" -> rs.first();
                case "last" -> rs.last();
                case "next" -> rs.next();
                case "prev" -> rs.previous();
            }
            displayRecord();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- DISPLAY RECORD ----------------
    private void displayRecord() throws SQLException {
        idField.setText(rs.getString("id"));
        nameField.setText(rs.getString("name"));
        specField.setText(rs.getString("specialization"));

        Blob blob = rs.getBlob("photo");
        if (blob != null) {
            byte[] data = blob.getBytes(1, (int) blob.length());
            imageView.setImage(new Image(new ByteArrayInputStream(data)));
        } else {
            imageView.setImage(null);
        }
    }

    // ---------------- UPDATE RECORD ----------------
    private void updateRecord() {
        try {
            rs.updateString("name", nameField.getText());
            rs.updateString("specialization", specField.getText());
            rs.updateRow();
            showAlert("Record updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- IMAGE UPLOAD (BLOB) ----------------
    private void uploadImage(Stage stage) {
        try {
            FileChooser fc = new FileChooser();
            File file = fc.showOpenDialog(stage);
            if (file == null) return;

            FileInputStream fis = new FileInputStream(file);
            rs.updateBinaryStream("photo", fis, (int) file.length());
            rs.updateRow();

            imageView.setImage(new Image(new FileInputStream(file)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- RESULTSET METADATA ----------------
    private void displayMetaData() {
        try {
            ResultSetMetaData md = rs.getMetaData();
            StringBuilder sb = new StringBuilder();
            sb.append("Total Columns: ").append(md.getColumnCount()).append("\n\n");

            for (int i = 1; i <= md.getColumnCount(); i++) {
                sb.append(md.getColumnName(i))
                        .append(" : ")
                        .append(md.getColumnTypeName(i))
                        .append("\n");
            }
            metaArea.setText(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- JOIN OPERATION ----------------
    private void showJoin() {
        try {
            String q = """
                    SELECT d.name AS doctor, p.name AS patient, a.date_time
                    FROM appointments a
                    JOIN doctors d ON a.doctor_id = d.id
                    JOIN patients p ON a.patient_id = p.id
                    """;

            Statement st = con.createStatement();
            ResultSet r = st.executeQuery(q);

            StringBuilder sb = new StringBuilder();
            while (r.next()) {
                sb.append("Doctor: ").append(r.getString("doctor"))
                        .append(" | Patient: ").append(r.getString("patient"))
                        .append(" | Date: ").append(r.getString("date_time"))
                        .append("\n");
            }
            joinArea.setText(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.show();
    }

    public static void main(String[] args) {
        DoctorRecordManagerFX app = new DoctorRecordManagerFX();
        app.connectDB();
        launch(args);
    }
}
