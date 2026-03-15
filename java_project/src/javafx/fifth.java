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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class fifth implements Initializable{

    @FXML private Button btnViewGrades, btnViewProfile, btnLogout;
    
    @FXML private Label sidebarNameLabel, sidebarClassLabel;
    @FXML private Label moyenneGeneraleLabel, totalSubjectsLabel, bestGradeLabel;
    @FXML private Label nameLabel, surnameLabel, classLabel, emailLabel;
    @FXML private Label profileMoyenneLabel, rankLabel, successRateLabel;

    @FXML private AnchorPane contentPane;
    @FXML private VBox gradesPane, profilePane;

    @FXML private ComboBox<String> subjectFilter;
    
    @FXML private TableView<StudentGrade> gradesTable;
    @FXML private TableColumn<StudentGrade, String> subjectColumn, commentColumn;
    @FXML private TableColumn<StudentGrade, Double> gradeColumn, coeffColumn;

    private ObservableList<StudentGrade> grades = FXCollections.observableArrayList();
    private ObservableList<StudentGrade> allGrades = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPanes();
        setupTableColumns();
        setupComboBox();
        loadStudentProfile();
        loadStudentGrades();
        calculateStatistics();
    }

    private void setupPanes() {
        if (contentPane != null) contentPane.setVisible(true);
        if (gradesPane != null) gradesPane.setVisible(false);
        if (profilePane != null) profilePane.setVisible(false);
    }

    private void setupTableColumns() {
        if (subjectColumn != null) {
            subjectColumn.setCellValueFactory(cell -> cell.getValue().subjectProperty());
        }
        if (gradeColumn != null) {
            gradeColumn.setCellValueFactory(cell -> cell.getValue().gradeProperty().asObject());
            gradeColumn.setCellFactory(column -> new TableCell<StudentGrade, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.format("%.1f", item));
                        
                        if (item >= 16) {
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                        } else if (item >= 14) {
                            setStyle("-fx-background-color: #cfe2ff; -fx-text-fill: #084298; -fx-font-weight: bold;");
                        } else if (item >= 10) {
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #664d03; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #842029; -fx-font-weight: bold;");
                        }
                    }
                }
            });
        }
        if (coeffColumn != null) {
            coeffColumn.setCellValueFactory(cell -> cell.getValue().coefficientProperty().asObject());
        }
        if (commentColumn != null) {
            commentColumn.setCellValueFactory(cell -> cell.getValue().commentProperty());
        }
    }

    private void setupComboBox() {
        if (subjectFilter != null) {
            subjectFilter.setItems(FXCollections.observableArrayList("Toutes les matières"));
            subjectFilter.setValue("Toutes les matières");
            subjectFilter.setOnAction(e -> filterGrades());
        }
    }

    private void loadStudentProfile() {
        String nom = "Martin";
        String prenom = "Jean";
        String classe = "3ème année";
        String email = "jean.martin@ecole.com";
        
        if (sidebarNameLabel != null) sidebarNameLabel.setText(prenom + " " + nom);
        if (sidebarClassLabel != null) sidebarClassLabel.setText(classe);
        
        if (nameLabel != null) nameLabel.setText(nom);
        if (surnameLabel != null) surnameLabel.setText(prenom);
        if (classLabel != null) classLabel.setText(classe);
        if (emailLabel != null) emailLabel.setText(email);
    }

    private void loadStudentGrades() {
        allGrades.clear();
        allGrades.add(new StudentGrade("Mathématiques", 16.5, 4.0, "Très bon travail, continuez ainsi"));
        allGrades.add(new StudentGrade("Physique", 15.0, 3.0, "Bon effort, quelques points à améliorer"));
        allGrades.add(new StudentGrade("Chimie", 14.5, 2.0, "Bien, mais attention aux détails"));
        allGrades.add(new StudentGrade("Français", 17.0, 3.0, "Excellent! Très bonne maîtrise"));
        allGrades.add(new StudentGrade("Anglais", 18.0, 2.0, "Outstanding performance!"));
        allGrades.add(new StudentGrade("Histoire", 13.5, 2.0, "Peut mieux faire"));
        allGrades.add(new StudentGrade("Géographie", 15.5, 2.0, "Très satisfaisant"));
        allGrades.add(new StudentGrade("Sport", 16.0, 1.0, "Excellente participation"));
        
        grades = FXCollections.observableArrayList(allGrades);
        
        if (gradesTable != null) {
            gradesTable.setItems(grades);
        }
        
        ObservableList<String> subjects = FXCollections.observableArrayList("Toutes les matières");
        subjects.addAll(allGrades.stream().map(StudentGrade::getSubject).toList());
        if (subjectFilter != null) {
            subjectFilter.setItems(subjects);
        }
    }

    private void calculateStatistics() {
        if (allGrades.isEmpty()) return;
        
        double totalWeighted = allGrades.stream()
            .mapToDouble(g -> g.getGrade() * g.getCoefficient())
            .sum();
        
        double totalCoeff = allGrades.stream()
            .mapToDouble(StudentGrade::getCoefficient)
            .sum();
        
        double moyenne = totalWeighted / totalCoeff;
        
        double bestGrade = allGrades.stream()
            .mapToDouble(StudentGrade::getGrade)
            .max()
            .orElse(0.0);
        
        int totalSubjects = allGrades.size();
        
        long passedSubjects = allGrades.stream()
            .filter(g -> g.getGrade() >= 10)
            .count();
        
        double successRate = (passedSubjects * 100.0) / totalSubjects;
        
        if (moyenneGeneraleLabel != null) {
            moyenneGeneraleLabel.setText(String.format("%.1f", moyenne));
        }
        if (totalSubjectsLabel != null) {
            totalSubjectsLabel.setText(String.valueOf(totalSubjects));
        }
        if (bestGradeLabel != null) {
            bestGradeLabel.setText(String.format("%.1f", bestGrade));
        }
        if (profileMoyenneLabel != null) {
            profileMoyenneLabel.setText(String.format("%.1f/20", moyenne));
        }
        if (rankLabel != null) {
            rankLabel.setText("5/30");
        }
        if (successRateLabel != null) {
            successRateLabel.setText(String.format("%.0f%%", successRate));
        }
    }

    @FXML
    public void showGrades(ActionEvent event) {
        // Réinitialiser tous les boutons
        resetAllButtons();
        
        // Colorer le bouton actif
        if (btnViewGrades != null) {
            btnViewGrades.setStyle("-fx-background-color: linear-gradient(to right, #10b981, #059669); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        }
        
        hideAllPanes();
        if (gradesPane != null) {
            gradesPane.setVisible(true);
            filterGrades();
        }
    }

    @FXML
    public void showProfile(ActionEvent event) {
        // Réinitialiser tous les boutons
        resetAllButtons();
        
        // Colorer le bouton actif
        if (btnViewProfile != null) {
            btnViewProfile.setStyle("-fx-background-color: linear-gradient(to right, #10b981, #059669); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        }
        
        hideAllPanes();
        if (profilePane != null) {
            profilePane.setVisible(true);
        }
    }

    // ✨ RÉINITIALISER TOUS LES BOUTONS AU STYLE PAR DÉFAUT
    private void resetAllButtons() {
        if (btnViewGrades != null) {
            btnViewGrades.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #667eea; " +
                "-fx-font-weight: bold; -fx-font-size: 13; -fx-background-radius: 10; " +
                "-fx-border-color: #667eea; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        }
        if (btnViewProfile != null) {
            btnViewProfile.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #667eea; " +
                "-fx-font-weight: bold; -fx-font-size: 13; -fx-background-radius: 10; " +
                "-fx-border-color: #667eea; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
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
        }
    }

    private void filterGrades() {
        String selectedSubject = subjectFilter.getValue();
        
        if (selectedSubject == null || selectedSubject.equals("Toutes les matières")) {
            grades = FXCollections.observableArrayList(allGrades);
        } else {
            grades = FXCollections.observableArrayList(
                allGrades.stream()
                    .filter(g -> g.getSubject().equals(selectedSubject))
                    .toList()
            );
        }
        
        if (gradesTable != null) {
            gradesTable.setItems(grades);
        }
    }

    private void hideAllPanes() {
        if (contentPane != null) contentPane.setVisible(false);
        if (gradesPane != null) gradesPane.setVisible(false);
        if (profilePane != null) profilePane.setVisible(false);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class StudentGrade {
        private javafx.beans.property.SimpleStringProperty subject;
        private javafx.beans.property.SimpleDoubleProperty grade;
        private javafx.beans.property.SimpleDoubleProperty coefficient;
        private javafx.beans.property.SimpleStringProperty comment;

        public StudentGrade(String subject, double grade, double coefficient, String comment) {
            this.subject = new javafx.beans.property.SimpleStringProperty(subject);
            this.grade = new javafx.beans.property.SimpleDoubleProperty(grade);
            this.coefficient = new javafx.beans.property.SimpleDoubleProperty(coefficient);
            this.comment = new javafx.beans.property.SimpleStringProperty(comment);
        }

        public String getSubject() { return subject.get(); }
        public void setSubject(String value) { subject.set(value); }
        public javafx.beans.property.StringProperty subjectProperty() { return subject; }

        public double getGrade() { return grade.get(); }
        public void setGrade(double value) { grade.set(value); }
        public javafx.beans.property.DoubleProperty gradeProperty() { return grade; }

        public double getCoefficient() { return coefficient.get(); }
        public void setCoefficient(double value) { coefficient.set(value); }
        public javafx.beans.property.DoubleProperty coefficientProperty() { return coefficient; }

        public String getComment() { return comment.get(); }
        public void setComment(String value) { comment.set(value); }
        public javafx.beans.property.StringProperty commentProperty() { return comment; }
    }
}