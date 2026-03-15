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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class third implements Initializable {

    @FXML private Button btnDashboard, btnViewTeachers, btnViewStudents, btnLogout;
    @FXML private Button btnAddTeacher, btnUpdateTeacher, btnDeleteTeacher;
    @FXML private Button btnAddStudent, btnUpdateStudent, btnDeleteStudent;
    @FXML private Button btnSaveTeacher, btnCancelTeacher, btnSaveStudent, btnCancelStudent;

    @FXML private VBox dashboardPane, teacherPane, studentPane, addTeacherPane, addStudentPane;

    @FXML private Label totalTeachersLabel, totalStudentsLabel, averageGradesLabel;
    @FXML private Label teacherFormError, studentFormError;
    @FXML private BarChart<String, Number> classDistributionChart;

    @FXML private ComboBox<String> teacherMatiereFilter, studentClassFilter;
    @FXML private ComboBox<String> teacherMatiereCombo, studentClassCombo;
    
    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, String> teacherNameColumn, teacherEmailColumn, teacherMatiereColumn;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> studentNameColumn, studentEmailColumn, studentClassColumn;
    @FXML private TableColumn<Student, Double> studentGradeColumn;

    @FXML private TextField teacherNomField, teacherPrenomField, teacherEmailField;
    @FXML private TextField studentNomField, studentPrenomField, studentEmailField;
    @FXML private DatePicker studentDatePicker;
    @FXML private TextField studentMoyenneField;


    private ObservableList<Teacher> teacherList = FXCollections.observableArrayList();
    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private Teacher selectedTeacher = null;
    private Student selectedStudent = null;
    private boolean isUpdateMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPanes();
        setupTableColumns();
        setupComboBoxes();
        loadTeachersFromDB();
        loadStudentsFromDB();
        loadDashboardStats();
    }

    private void loadTeachersFromDB() {
        teacherList.clear();
        try {
            Connection conn = Connexion.getConnection();
            String query = "SELECT nom_complet, email, matiere FROM d_enseignant";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String nomComplet = rs.getString("nom_complet");
                String email = rs.getString("email");
                String matiere = rs.getString("matiere");
                teacherList.add(new Teacher(nomComplet, email, matiere));
            }
            
            rs.close();
            pstmt.close();
            System.out.println("✅ " + teacherList.size() + " enseignants chargés");
            
        } catch (SQLException e) {
            System.err.println("Erreur chargement enseignants: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStudentsFromDB() {
        studentList.clear();
        try {
            Connection conn = Connexion.getConnection();
            String query = "SELECT nom, prenom, email, classe, moyenne FROM d_eleve";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String nomComplet = nom + " " + prenom;
                String email = rs.getString("email");
                String classe = rs.getString("classe");

                double moyenne = rs.getDouble("moyenne");
                if (rs.wasNull()) {
                    moyenne = 0.0;
                }

                studentList.add(new Student(nomComplet, email, classe, moyenne));
            }
            
            rs.close();
            pstmt.close();
            System.out.println("✅ " + studentList.size() + " élèves chargés");
            
        } catch (SQLException e) {
            System.err.println("Erreur chargement élèves: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupPanes() {
        if (dashboardPane != null) dashboardPane.setVisible(true);
        if (teacherPane != null) teacherPane.setVisible(false);
        if (studentPane != null) studentPane.setVisible(false);
        if (addTeacherPane != null) addTeacherPane.setVisible(false);
        if (addStudentPane != null) addStudentPane.setVisible(false);
    }

    private void setupTableColumns() {
        if (teacherNameColumn != null) {
            teacherNameColumn.setCellValueFactory(cell -> 
                cell.getValue().nameProperty());
        }
        if (teacherEmailColumn != null) {
            teacherEmailColumn.setCellValueFactory(cell -> 
                cell.getValue().emailProperty());
        }
        if (teacherMatiereColumn != null) {
            teacherMatiereColumn.setCellValueFactory(cell -> 
                cell.getValue().matiereProperty());
        }

        if (studentNameColumn != null) {
            studentNameColumn.setCellValueFactory(cell -> 
                cell.getValue().nameProperty());
        }
        if (studentEmailColumn != null) {
            studentEmailColumn.setCellValueFactory(cell -> 
                cell.getValue().emailProperty());
        }
        if (studentClassColumn != null) {
            studentClassColumn.setCellValueFactory(cell -> 
                cell.getValue().classeProperty());
        }
        if (studentGradeColumn != null) {
            studentGradeColumn.setCellValueFactory(cell -> 
                cell.getValue().moyenneProperty().asObject());
        }
    }

    private void setupComboBoxes() {
        ObservableList<String> matieres = FXCollections.observableArrayList(
            "Mathématiques", "Physique", "Chimie", "Français", "Anglais",
            "Histoire", "Géographie", "Sport", "Philosophie", "Informatique"
        );
        
        ObservableList<String> classes = FXCollections.observableArrayList(
            "1ère année", "2ème année", "3ème année", "4ème année", "Terminale"
        );

        if (teacherMatiereCombo != null) teacherMatiereCombo.setItems(matieres);
        if (teacherMatiereFilter != null) {
            teacherMatiereFilter.setItems(FXCollections.observableArrayList("Toutes les matières"));
            teacherMatiereFilter.getItems().addAll(matieres);
            teacherMatiereFilter.setValue("Toutes les matières");
        }
        
        if (studentClassCombo != null) studentClassCombo.setItems(classes);
        if (studentClassFilter != null) {
            studentClassFilter.setItems(FXCollections.observableArrayList("Toutes les classes"));
            studentClassFilter.getItems().addAll(classes);
            studentClassFilter.setValue("Toutes les classes");
        }
    }

    @FXML
    public void showDashboard(ActionEvent event) {
        resetAllButtons();
        
        if (btnDashboard != null) {
            btnDashboard.setStyle("-fx-background-color: linear-gradient(to right, #10b981, #059669); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        }
        
        hideAllPanes();
        if (dashboardPane != null) {
            dashboardPane.setVisible(true);
            loadDashboardStats();
        }
    }

    @FXML
    public void gererEnseignants(ActionEvent event) {
        resetAllButtons();
        
        if (btnViewTeachers != null) {
            btnViewTeachers.setStyle("-fx-background-color: linear-gradient(to right, #10b981, #059669); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        }
        
        hideAllPanes();
        isUpdateMode = false;
        selectedTeacher = null;
        if (teacherPane != null) {
            teacherPane.setVisible(true);
            if (teacherTable != null) teacherTable.setItems(teacherList);
        }
    }

    @FXML
    public void gererEleves(ActionEvent event) {
        // Réinitialiser tous les boutons
        resetAllButtons();
        
        // Colorer le bouton actif
        if (btnViewStudents != null) {
            btnViewStudents.setStyle("-fx-background-color: linear-gradient(to right, #10b981, #059669); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        }
        
        hideAllPanes();
        isUpdateMode = false;
        selectedStudent = null;
        if (studentPane != null) {
            studentPane.setVisible(true);
            if (studentTable != null) studentTable.setItems(studentList);
        }
    }

    // ✨ RÉINITIALISER TOUS LES BOUTONS AU STYLE PAR DÉFAUT
    private void resetAllButtons() {
        if (btnDashboard != null) {
            btnDashboard.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #667eea; " +
                "-fx-font-weight: bold; -fx-font-size: 13; -fx-background-radius: 10; " +
                "-fx-border-color: #667eea; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        }
        if (btnViewTeachers != null) {
            btnViewTeachers.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #667eea; " +
                "-fx-font-weight: bold; -fx-font-size: 13; -fx-background-radius: 10; " +
                "-fx-border-color: #667eea; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        }
        if (btnViewStudents != null) {
            btnViewStudents.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #667eea; " +
                "-fx-font-weight: bold; -fx-font-size: 13; -fx-background-radius: 10; " +
                "-fx-border-color: #667eea; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        }
    }

    @FXML
    public void showAddTeacherForm(ActionEvent event) {
        hideAllPanes();
        isUpdateMode = false;
        clearTeacherForm();
        if (addTeacherPane != null) addTeacherPane.setVisible(true);
    }

    @FXML
    public void showAddStudentForm(ActionEvent event) {
        hideAllPanes();
        isUpdateMode = false;
        clearStudentForm();
        if (addStudentPane != null) addStudentPane.setVisible(true);
    }

    @FXML
    public void showUpdateTeacherForm(ActionEvent event) {
        selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            showWarning("Veuillez sélectionner un enseignant à modifier");
            return;
        }
        
        isUpdateMode = true;
        hideAllPanes();
        teacherNomField.setText(selectedTeacher.getName().split(" ")[0]);
        teacherPrenomField.setText(selectedTeacher.getName().split(" ")[1]);
        teacherEmailField.setText(selectedTeacher.getEmail());
        teacherMatiereCombo.setValue(selectedTeacher.getMatiere());
        if (addTeacherPane != null) addTeacherPane.setVisible(true);
    }

    @FXML
    public void showUpdateStudentForm(ActionEvent event) {
        selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showWarning("Veuillez sélectionner un élève à modifier");
            return;
        }
        
        isUpdateMode = true;
        hideAllPanes();
        studentNomField.setText(selectedStudent.getName().split(" ")[0]);
        studentPrenomField.setText(selectedStudent.getName().split(" ")[1]);
        studentEmailField.setText(selectedStudent.getEmail());
        studentClassCombo.setValue(selectedStudent.getClasse());
        if (addStudentPane != null) addStudentPane.setVisible(true);
    }

    @FXML
    public void saveTeacher(ActionEvent event) {
        String nom = teacherNomField.getText().trim();
        String prenom = teacherPrenomField.getText().trim();
        String email = teacherEmailField.getText().trim();
        String matiere = teacherMatiereCombo.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || matiere == null) {
            showTeacherFormError("Veuillez remplir tous les champs");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showTeacherFormError("Format d'email invalide");
            return;
        }

        try {
            Connection conn = Connexion.getConnection();
            
            if (isUpdateMode && selectedTeacher != null) {
                String query = "UPDATE d_enseignant SET nom_complet = ?, email = ?, matiere = ? WHERE email = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, nom + " " + prenom);
                pstmt.setString(2, email);
                pstmt.setString(3, matiere);
                pstmt.setString(4, selectedTeacher.getEmail());
                
                int rows = pstmt.executeUpdate();
                pstmt.close();
                
                if (rows > 0) {
                    selectedTeacher.setName(nom + " " + prenom);
                    selectedTeacher.setEmail(email);
                    selectedTeacher.setMatiere(matiere);
                    teacherTable.refresh();
                    showSuccess("Enseignant modifié avec succès");
                }
                
            } else {
                String query = "INSERT INTO d_enseignant (nom_complet, email, matiere) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, nom + " " + prenom);
                pstmt.setString(2, email);
                pstmt.setString(3, matiere);
                
                int rows = pstmt.executeUpdate();
                pstmt.close();
                
                if (rows > 0) {
                    teacherList.add(new Teacher(nom + " " + prenom, email, matiere));
                    showSuccess("Enseignant ajouté avec succès");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            showError("Erreur lors de l'enregistrement: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        gererEnseignants(event);
    }

    @FXML
    public void saveStudent(ActionEvent event) {
        String nom = studentNomField.getText().trim();
        String prenom = studentPrenomField.getText().trim();
        String email = studentEmailField.getText().trim();
        String classe = studentClassCombo.getValue();
        String moyenneText = studentMoyenneField.getText().trim();
        double moyenne = 0.0;

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || classe == null) {
            showStudentFormError("Veuillez remplir tous les champs");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showStudentFormError("Format d'email invalide");
            return;
        }

        if (!moyenneText.isEmpty()) {
            try {
                moyenne = Double.parseDouble(moyenneText);
            } catch (NumberFormatException e) {
                showStudentFormError("La moyenne doit être un nombre");
                return;
            }
        }

        try (Connection conn = Connexion.getConnection()) {
            if (isUpdateMode && selectedStudent != null) {
                String query = "UPDATE d_eleve SET nom=?, prenom=?, email=?, classe=?, moyenne=? WHERE email=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, nom);
                pstmt.setString(2, prenom);
                pstmt.setString(3, email);
                pstmt.setString(4, classe);
                pstmt.setDouble(5, moyenne);
                pstmt.setString(6, selectedStudent.getEmail());

                if (pstmt.executeUpdate() > 0) {
                    selectedStudent.setName(nom + " " + prenom);
                    selectedStudent.setEmail(email);
                    selectedStudent.setClasse(classe);
                    selectedStudent.setMoyenne(moyenne);
                    studentTable.refresh();
                    showSuccess("Élève modifié avec succès");
                }
                pstmt.close();

            } else {
                String query = "INSERT INTO d_eleve (nom, prenom, email, classe, moyenne) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, nom);
                pstmt.setString(2, prenom);
                pstmt.setString(3, email);
                pstmt.setString(4, classe);
                pstmt.setDouble(5, moyenne);

                if (pstmt.executeUpdate() > 0) {
                    studentList.add(new Student(nom + " " + prenom, email, classe, moyenne));
                    showSuccess("Élève ajouté avec succès");
                }
                pstmt.close();
            }

        } catch (SQLException e) {
            showError("Erreur: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        gererEleves(event);
    }

    @FXML
    public void deleteTeacher(ActionEvent event) {
        Teacher selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner un enseignant à supprimer");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'enseignant");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getName() + " ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = Connexion.getConnection();
                String query = "DELETE FROM d_enseignant WHERE email = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, selected.getEmail());
                
                int rows = pstmt.executeUpdate();
                pstmt.close();
                
                if (rows > 0) {
                    teacherList.remove(selected);
                    showSuccess("Enseignant supprimé avec succès");
                }
                
            } catch (SQLException e) {
                System.err.println("Erreur suppression: " + e.getMessage());
                showError("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    @FXML
    public void deleteStudent(ActionEvent event) {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner un élève à supprimer");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'élève");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getName() + " ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = Connexion.getConnection();
                String query = "DELETE FROM d_eleve WHERE email = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, selected.getEmail());
                
                int rows = pstmt.executeUpdate();
                pstmt.close();
                
                if (rows > 0) {
                    studentList.remove(selected);
                    showSuccess("Élève supprimé avec succès");
                }
                
            } catch (SQLException e) {
                System.err.println("Erreur suppression: " + e.getMessage());
                showError("Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de déconnexion");
            alert.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
            alert.setContentText("Vous serez redirigé vers la page de connexion.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("first.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            showError("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

   private void loadDashboardStats() {
    System.out.println("Loading dashboard statistics...");
    
    if (totalTeachersLabel != null) {
        totalTeachersLabel.setText(String.valueOf(teacherList.size()));
        System.out.println("Total teachers: " + teacherList.size());
    }
    
    if (totalStudentsLabel != null) {
        totalStudentsLabel.setText(String.valueOf(studentList.size()));
        System.out.println("Total students: " + studentList.size());
    }
    
    if (!studentList.isEmpty()) {
        double avg = studentList.stream()
            .mapToDouble(Student::getMoyenne)
            .average()
            .orElse(0.0);
        
        if (averageGradesLabel != null) {
            averageGradesLabel.setText(String.format("%.1f", avg));
            System.out.println("Average grade: " + String.format("%.1f", avg));
        }
    } else {
        if (averageGradesLabel != null) {
            averageGradesLabel.setText("0.0");
            System.out.println(" No students found, average set to 0.0");
        }
    }
    
    loadChartData();
    System.out.println("✅ Dashboard stats loaded successfully");
}

private void loadChartData() {
    if (classDistributionChart == null) {
        System.err.println("❌ Chart is null!");
        return;
    }
    
    System.out.println("🔄 Loading chart data...");
    classDistributionChart.getData().clear();
    
    try {
        Connection conn = Connexion.getConnection();
        
        String query = "SELECT classe, COUNT(*) as count FROM d_eleve GROUP BY classe ORDER BY classe";
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre d'élèves");
        
        boolean hasData = false;
        while (rs.next()) {
            String classe = rs.getString("classe");
            int count = rs.getInt("count");
            
            String shortName = classe.split(" ")[0];
            
            series.getData().add(new XYChart.Data<>(shortName, count));
            System.out.println("  📊 " + shortName + ": " + count + " élèves");
            hasData = true;
        }
        
        rs.close();
        pstmt.close();
        
        if (hasData) {
            classDistributionChart.getData().add(series);
            classDistributionChart.setLegendVisible(false);
            System.out.println("✅ Chart loaded with real data");
        } else {
            System.out.println(" No class data found, using placeholder");
            series.getData().add(new XYChart.Data<>("1ère", 0));
            series.getData().add(new XYChart.Data<>("2ème", 0));
            series.getData().add(new XYChart.Data<>("3ème", 0));
            series.getData().add(new XYChart.Data<>("4ème", 0));
            classDistributionChart.getData().add(series);
            classDistributionChart.setLegendVisible(false);
        }
        
    } catch (SQLException e) {
        System.err.println("Error loading chart data: " + e.getMessage());
        e.printStackTrace();
        
       
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre d'élèves");
        series.getData().add(new XYChart.Data<>("1ère", 0));
        series.getData().add(new XYChart.Data<>("2ème", 0));
        series.getData().add(new XYChart.Data<>("3ème", 0));
        series.getData().add(new XYChart.Data<>("4ème", 0));
        classDistributionChart.getData().add(series);
        classDistributionChart.setLegendVisible(false);
    }
}

    private void clearTeacherForm() {
        if (teacherNomField != null) teacherNomField.clear();
        if (teacherPrenomField != null) teacherPrenomField.clear();
        if (teacherEmailField != null) teacherEmailField.clear();
        if (teacherMatiereCombo != null) teacherMatiereCombo.setValue(null);
        if (teacherFormError != null) teacherFormError.setVisible(false);
    }

    private void clearStudentForm() {
        if (studentNomField != null) studentNomField.clear();
        if (studentPrenomField != null) studentPrenomField.clear();
        if (studentEmailField != null) studentEmailField.clear();
        if (studentDatePicker != null) studentDatePicker.setValue(null);
        if (studentClassCombo != null) studentClassCombo.setValue(null);
        if (studentFormError != null) studentFormError.setVisible(false);
    }

    private void hideAllPanes() {
        if (dashboardPane != null) dashboardPane.setVisible(false);
        if (teacherPane != null) teacherPane.setVisible(false);
        if (studentPane != null) studentPane.setVisible(false);
        if (addTeacherPane != null) addTeacherPane.setVisible(false);
        if (addStudentPane != null) addStudentPane.setVisible(false);
    }

    private void showTeacherFormError(String msg) {
        if (teacherFormError != null) {
            teacherFormError.setText(msg);
            teacherFormError.setVisible(true);
        }
    }

    private void showStudentFormError(String msg) {
        if (studentFormError != null) {
            studentFormError.setText(msg);
            studentFormError.setVisible(true);
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static class Teacher {
        private javafx.beans.property.SimpleStringProperty name;
        private javafx.beans.property.SimpleStringProperty email;
        private javafx.beans.property.SimpleStringProperty matiere;

        public Teacher(String name, String email, String matiere) {
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.email = new javafx.beans.property.SimpleStringProperty(email);
            this.matiere = new javafx.beans.property.SimpleStringProperty(matiere);
        }

        public String getName() { return name.get(); }
        public void setName(String value) { name.set(value); }
        public javafx.beans.property.StringProperty nameProperty() { return name; }

        public String getEmail() { return email.get(); }
        public void setEmail(String value) { email.set(value); }
        public javafx.beans.property.StringProperty emailProperty() { return email; }

        public String getMatiere() { return matiere.get(); }
        public void setMatiere(String value) { matiere.set(value); }
        public javafx.beans.property.StringProperty matiereProperty() { return matiere; }
    }

    public static class Student {
        private javafx.beans.property.SimpleStringProperty name;
        private javafx.beans.property.SimpleStringProperty email;
        private javafx.beans.property.SimpleStringProperty classe;
        private javafx.beans.property.SimpleDoubleProperty moyenne;

        public Student(String name, String email, String classe, double moyenne) {
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.email = new javafx.beans.property.SimpleStringProperty(email);
            this.classe = new javafx.beans.property.SimpleStringProperty(classe);
            this.moyenne = new javafx.beans.property.SimpleDoubleProperty(moyenne);
        }

        public String getName() { return name.get(); }
        public void setName(String value) { name.set(value); }
        public javafx.beans.property.StringProperty nameProperty() { return name; }

        public String getEmail() { return email.get(); }
        public void setEmail(String value) { email.set(value); }
        public javafx.beans.property.StringProperty emailProperty() { return email; }

        public String getClasse() { return classe.get(); }
        public void setClasse(String value) { classe.set(value); }
        public javafx.beans.property.StringProperty classeProperty() { return classe; }

        public double getMoyenne() { return moyenne.get(); }
        public void setMoyenne(double value) { moyenne.set(value); }
        public javafx.beans.property.DoubleProperty moyenneProperty() { return moyenne; }
    }
}