package DoctorAppointmentManager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;

public class DoctorPatientManagerFX extends Application {

    Connection con;
    Statement stmt;
    ResultSet rs;

    TextField txtId = new TextField();
    TextField txtName = new TextField();
    TextField txtAge = new TextField();
    ImageView imageView = new ImageView();

    File selectedImage;

    @Override
    public void start(Stage stage) {
        connectDB();
        loadScrollableResultSet();

        Button btnFirst = new Button("First");
        Button btnNext = new Button("Next");
        Button btnPrev = new Button("Previous");
        Button btnLast = new Button("Last");

        Button btnInsert = new Button("Insert");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        Button btnImage = new Button("Choose Image");
        Button btnJoin = new Button("Show Join");

        btnFirst.setOnAction(e -> moveFirst());
        btnNext.setOnAction(e -> moveNext());
        btnPrev.setOnAction(e -> movePrevious());
        btnLast.setOnAction(e -> moveLast());

        btnInsert.setOnAction(e -> insertRecord());
        btnUpdate.setOnAction(e -> updateRecord());
        btnDelete.setOnAction(e -> deleteRecord());
        btnImage.setOnAction(e -> chooseImage(stage));
        btnJoin.setOnAction(e -> performJoin());

        imageView.setFitHeight(120);
        imageView.setFitWidth(120);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Patient ID"), 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(new Label("Name"), 0, 1);
        grid.add(txtName, 1, 1);
        grid.add(new Label("Age"), 0, 2);
        grid.add(txtAge, 1, 2);
        grid.add(imageView, 2, 0, 1, 3);

        HBox nav = new HBox(10, btnFirst, btnPrev, btnNext, btnLast);
        HBox crud = new HBox(10, btnInsert, btnUpdate, btnDelete, btnImage, btnJoin);

        VBox root = new VBox(15, grid, nav, crud);

        displayMetaData();

        Scene scene = new Scene(root, 700, 350);
        stage.setTitle("Doctor Patient Manager - JavaFX");
        stage.setScene(scene);
        stage.show();

        moveFirst();
    }

    /* ---------------- DATABASE CONNECTION ---------------- */
    void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hospital_db",
                    "newuser",
                    "password123");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---------------- SCROLLABLE RESULTSET ---------------- */
    void loadScrollableResultSet() {
        try {
            stmt = con.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery("SELECT * FROM patient");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---------------- NAVIGATION ---------------- */
    void moveFirst() {
        try {
            if (rs.first())
                displayRecord();
        } catch (Exception e) {
        }
    }

    void moveNext() {
        try {
            if (!rs.isLast() && rs.next())
                displayRecord();
        } catch (Exception e) {
        }
    }

    void movePrevious() {
        try {
            if (!rs.isFirst() && rs.previous())
                displayRecord();
        } catch (Exception e) {
        }
    }

    void moveLast() {
        try {
            if (rs.last())
                displayRecord();
        } catch (Exception e) {
        }
    }

    /* ---------------- DISPLAY RECORD ---------------- */
    void displayRecord() {
        try {
            txtId.setText(rs.getString("patient_id"));
            txtName.setText(rs.getString("name"));
            txtAge.setText(rs.getString("age"));

            Blob blob = rs.getBlob("photo");
            if (blob != null && blob.length() > 0) {
                Image img = new Image(blob.getBinaryStream());
                imageView.setImage(img);
            } else {
                imageView.setImage(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---------------- CRUD OPERATIONS ---------------- */
    void insertRecord() {
        try {
            rs.moveToInsertRow();
            rs.updateString("name", txtName.getText());
            rs.updateInt("age", Integer.parseInt(txtAge.getText()));

            if (selectedImage != null) {
                FileInputStream fis = new FileInputStream(selectedImage);
                rs.updateBlob("photo", fis);
            }

            rs.updateInt("doctor_id", 1);
            rs.insertRow();

            rs.moveToCurrentRow();
            rs.last(); // 🔥 VERY IMPORTANT
            displayRecord();

            showAlert("Record inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateRecord() {
        try {
            if (rs == null || rs.isBeforeFirst() || rs.isAfterLast()) {
                showAlert("No record selected to update");
                return;
            }

            rs.updateString("name", txtName.getText());
            rs.updateInt("age", Integer.parseInt(txtAge.getText()));

            // Update image ONLY if a new image is selected
            if (selectedImage != null) {
                FileInputStream fis = new FileInputStream(selectedImage);
                rs.updateBlob("photo", fis);
            }

            rs.updateRow();
            showAlert("Record updated successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deleteRecord() {
        try {
            if (rs == null || rs.isBeforeFirst() || rs.isAfterLast()) {
                showAlert("No record selected to delete");
                return;
            }

            rs.deleteRow();
            showAlert("Record deleted successfully");

            if (rs.next()) {
                displayRecord();
            } else if (rs.previous()) {
                displayRecord();
            } else {
                clearFields();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---------------- IMAGE HANDLING ---------------- */
    void chooseImage(Stage stage) {
        FileChooser fc = new FileChooser();
        selectedImage = fc.showOpenDialog(stage);
        if (selectedImage != null) {
            imageView.setImage(new Image(selectedImage.toURI().toString()));
        }
    }

    /* ---------------- RESULTSET METADATA ---------------- */
    void displayMetaData() {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            System.out.println("Total Columns: " + meta.getColumnCount());

            for (int i = 1; i <= meta.getColumnCount(); i++) {
                System.out.println(
                        meta.getColumnName(i) + " - " + meta.getColumnTypeName(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---------------- JOIN OPERATION ---------------- */
    void performJoin() {
        try {
            Statement s = con.createStatement();
            ResultSet joinRs = s.executeQuery(
                    "SELECT p.name AS Patient, d.name AS Doctor, d.specialization " +
                            "FROM patient p JOIN doctor d ON p.doctor_id = d.doctor_id");

            System.out.println("\n--- JOIN RESULT ---");
            while (joinRs.next()) {
                System.out.println(
                        joinRs.getString("Patient") + " - " +
                                joinRs.getString("Doctor") + " - " +
                                joinRs.getString("specialization"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void clearFields() {
        txtId.clear();
        txtName.clear();
        txtAge.clear();
        imageView.setImage(null);
    }

    void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
