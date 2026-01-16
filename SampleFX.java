package DoctorAppointmentManager;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.Region;
import javafx.scene.effect.DropShadow;

public class SampleFX extends Application {

    @Override
    public void start(Stage stage) {

        // Title Label
        Label title = new Label("User Registration Form");
        title.setFont(Font.font("Arial", 24));
        title.setTextFill(Color.DARKBLUE);
        title.setEffect(new DropShadow(5, Color.GRAY));

        // Name
        Label nameLabel = new Label("Name:");
        nameLabel.setFont(Font.font(16));
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        nameField.setPrefWidth(200);
        HBox nameBox = new HBox(10, nameLabel, nameField);
        nameBox.setAlignment(Pos.CENTER);

        // Email
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font(16));
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefWidth(200);
        HBox emailBox = new HBox(10, emailLabel, emailField);
        emailBox.setAlignment(Pos.CENTER);

        // Submit Button
        Button submitBtn = new Button("Submit");
        submitBtn.setFont(Font.font(16));
        submitBtn.setStyle("-fx-background-color: #0077ff; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 8;");
        submitBtn.setEffect(new DropShadow(5, Color.GRAY));

        // Hover effect
        submitBtn.setOnMouseEntered(e -> submitBtn.setStyle("-fx-background-color: #005ec7; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 8;"));
        submitBtn.setOnMouseExited(e -> submitBtn.setStyle("-fx-background-color: #0077ff; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 8;"));

        // Output message
        Label message = new Label();
        message.setFont(Font.font(16));
        message.setTextFill(Color.DARKGREEN);

        // Submit Action
        submitBtn.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();

            if (name.isEmpty() || email.isEmpty()) {
                message.setTextFill(Color.RED);
                message.setText("⚠ Please fill in all fields.");
            } else {
                message.setTextFill(Color.DARKGREEN);
                message.setText("✔ Submitted: " + name + " (" + email + ")");
            }
        });

        // Layout
        VBox root = new VBox(15, title, nameBox, emailBox, submitBtn, message);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #eef6ff;");
        root.setPrefSize(400, 350);

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Beautiful JavaFX Form");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
