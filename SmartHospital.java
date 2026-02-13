import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Comparator;
import java.util.stream.Collectors;
public class SmartHospital extends Application {
    private ObservableList<Patient> patients = FXCollections.observableArrayList();
    private ListView<String> listView = new ListView<>();
    public static class Patient {
        int id;
        String name;
        String dept;
        int cost;
        String status;
        Patient(int id, String name, String dept, int cost, String status) {
            this.id = id;
            this.name = name;
        this.dept = dept;
        this.cost = cost;
            this.status = status;
        }
        public String toString() {return "ID: " + id + " | Name: " + name + " | Dept: " + dept + " | Cost: ₹" + cost + " | Discharged: " + status;
        }
    }
    private void refreshList() {
        listView.getItems().setAll(patients.stream().map(Patient::toString).collect(Collectors.toList())
        );
    }
    @Override
    public void start(Stage stage) {
        TextField id = new TextField();
        id.setPromptText("ID");
        TextField name = new TextField();
        name.setPromptText("Name");
        ComboBox<String> dept = new ComboBox<>();
        dept.getItems().addAll("ICU","General","Emergency","Cardiology","Neurology");
        TextField cost = new TextField();
        cost.setPromptText("Cost");
        TextField status = new TextField();
        status.setPromptText("Yes/No");
        Button add = new Button("Add");
        Button sort = new Button("Sort by Cost");
        Button filter = new Button("Cost > 200");
        Button showAll = new Button("Show All");
        add.setOnAction(e -> {
            patients.add(new Patient(
                    Integer.parseInt(id.getText()),
                    name.getText(),
                dept.getValue(),
            Integer.parseInt(cost.getText()),
                status.getText()
            ));
            refreshList();
            id.clear();
        name.clear();
            cost.clear();
        status.clear();
        });
        sort.setOnAction(e -> {
         patients.sort(Comparator.comparingInt(p -> -p.cost));
    refreshList();
        });
        filter.setOnAction(e -> { listView.getItems().setAll(patients.stream().filter(p -> p.cost > 200).map(Patient::toString).collect(Collectors.toList()));
        });
        showAll.setOnAction(e -> refreshList());
        HBox form = new HBox(10, id, name, dept, cost, status, add);
        HBox actions = new HBox(10, sort, filter, showAll);
        listView.setPrefHeight(300);
        VBox root = new VBox(15, form, actions, listView);
        root.setPadding(new Insets(20));
        stage.setScene(new Scene(root, 850, 400));
        stage.setTitle("Smart Hospital");
        stage.show();
    }
    public static void main(String[] args) { launch(); }
}
