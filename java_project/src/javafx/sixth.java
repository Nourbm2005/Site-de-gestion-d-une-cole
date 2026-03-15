/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class sixth{

    /**
     * Initializes the controller class.
     */
     @FXML
    private Button btnViewGrades;
    @FXML
    private Button btnViewProfile;
    @FXML
    private Button btnLogout;

    @FXML
    private AnchorPane contentPane;
    @FXML
    private VBox gradesPane;
    @FXML
    private VBox profilePane;

    @FXML
    private TableView<StudentGrade> gradesTable;
    @FXML
    private TableColumn<StudentGrade, String> subjectColumn;
    @FXML
    private TableColumn<StudentGrade, Double> gradeColumn;
    @FXML
    private TableColumn<StudentGrade, String> commentColumn;

    @FXML
    private Label nameLabel;
    @FXML
    private Label surnameLabel;
    @FXML
    private Label classLabel;
    @FXML
    private Label emailLabel;

    private ObservableList<StudentGrade> grades = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (contentPane != null) {
            contentPane.setVisible(true);
        }
        if (gradesPane != null) {
            gradesPane.setVisible(false);
        }
        if (profilePane != null) {
            profilePane.setVisible(false);
        }

        if (subjectColumn != null && gradeColumn != null && commentColumn != null) {
            subjectColumn.setCellValueFactory(cell -> cell.getValue().subjectProperty());
            gradeColumn.setCellValueFactory(cell -> cell.getValue().gradeProperty().asObject());
            commentColumn.setCellValueFactory(cell -> cell.getValue().commentProperty());
        }

        loadStudentProfile();
        loadStudentGrades();

        if (gradesTable != null) {
            gradesTable.setRowFactory(tv -> {
                TableRow<StudentGrade> row = new TableRow<>();
                row.setOnMouseEntered(event -> {
                    StudentGrade g = row.getItem();
                    if (g != null) {
                        Tooltip tooltip = new Tooltip("Matière: " + g.getSubject() + "\nNote: " + g.getGrade());
                        Tooltip.install(row, tooltip);
                    }
                });
                return row;
            });
        }

        System.out.println("Student Dashboard initialized");
    }

    @FXML
    public void showGrades(ActionEvent event) {
        hideAllPanes();
        if (gradesPane != null) {
            gradesPane.setVisible(true);
            System.out.println("Showing grades");
            loadStudentGrades();
        }
    }

    @FXML
    public void showProfile(ActionEvent event) {
        hideAllPanes();
        if (profilePane != null) {
            profilePane.setVisible(true);
            System.out.println("Showing profile");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Déconnexion");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir vous déconnecter?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                Parent root = FXMLLoader.load(getClass().getResource("first.fxml"));
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            showError("Erreur lors de la déconnexion");
            e.printStackTrace();
        }
    }

    private void loadStudentProfile() {
        // TODO: Fetch student data from database
        // Exemple
        if (nameLabel != null) {
            nameLabel.setText("Dupont");
        }
        if (surnameLabel != null) {
            surnameLabel.setText("Jean");
        }
        if (classLabel != null) {
            classLabel.setText("3A");
        }
        if (emailLabel != null) {
            emailLabel.setText("jean.dupont@ecole.com");
        }

        System.out.println("Student profile loaded");
    }

    private void loadStudentGrades() {
        // TODO: Fetch grades from database
        // Exemple
        grades.clear();
        grades.add(new StudentGrade("Mathématiques", 16.0, "Très bon travail"));
        grades.add(new StudentGrade("Physique", 14.5, "Bon effort"));
        grades.add(new StudentGrade("Anglais", 17.0, "Excellent"));
        grades.add(new StudentGrade("Histoire", 15.0, "Bien"));
        grades.add(new StudentGrade("Français", 13.5, "Peut mieux faire"));

        if (gradesTable != null) {
            gradesTable.setItems(grades);
        }

        System.out.println("Student grades loaded: " + grades.size() + " subjects");
    }

    private double calculateAverage() {
        return grades.stream()
                .mapToDouble(StudentGrade::getGrade)
                .average()
                .orElse(0.0);
    }

    private void hideAllPanes() {
        if (contentPane != null) {
            contentPane.setVisible(false);
        }
        if (gradesPane != null) {
            gradesPane.setVisible(false);
        }
        if (profilePane != null) {
            profilePane.setVisible(false);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

class StudentGrade {

    private final javafx.beans.property.SimpleStringProperty subject;
    private final javafx.beans.property.SimpleDoubleProperty grade;
    private final javafx.beans.property.SimpleStringProperty comment;

    public StudentGrade(String subject, double grade, String comment) {
        this.subject = new javafx.beans.property.SimpleStringProperty(subject);
        this.grade = new javafx.beans.property.SimpleDoubleProperty(grade);
        this.comment = new javafx.beans.property.SimpleStringProperty(comment);
    }

    public String getSubject() {
        return subject.get();
    }

    public void setSubject(String value) {
        subject.set(value);
    }

    public javafx.beans.property.StringProperty subjectProperty() {
        return subject;
    }

    public double getGrade() {
        return grade.get();
    }

    public void setGrade(double value) {
        grade.set(value);
    }

    public javafx.beans.property.DoubleProperty gradeProperty() {
        return grade;
    }

    public String getComment() {
        return comment.get();
    }

    public void setComment(String value) {
        comment.set(value);
    }

    public javafx.beans.property.StringProperty commentProperty() {
        return comment;
    }
}

class Student {

    private final javafx.beans.property.SimpleStringProperty name;
    private final javafx.beans.property.SimpleStringProperty studentClass;

    public Student(String name, String studentClass) {
        this.name = new javafx.beans.property.SimpleStringProperty(name);
        this.studentClass = new javafx.beans.property.SimpleStringProperty(studentClass);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public javafx.beans.property.StringProperty nameProperty() {
        return name;
    }

    public String getStudentClass() {
        return studentClass.get();
    }

    public void setStudentClass(String value) {
        studentClass.set(value);
    }

    public javafx.beans.property.StringProperty classProperty() {
        return studentClass;
    } 
    
}
