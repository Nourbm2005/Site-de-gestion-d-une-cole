/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafx;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class fourth implements Initializable {

    @FXML private Button btnViewStudents, btnViewGrades, btnLogout;
    @FXML private Button btnAddGrade, btnUpdateGrades, btnDeleteGrade;
    
    @FXML private Label teacherNameLabel, teacherMatiereLabel, classMoyenneLabel;
    
    @FXML private AnchorPane contentPane;
    @FXML private VBox studentPane, gradesPane;

    @FXML private ComboBox<String> studentClassFilter, gradeClassFilter;
    
    @FXML private TableView<StudentInfo> studentTable;
    @FXML private TableColumn<StudentInfo, String> studentNameColumn, studentClassColumn, studentEmailColumn;

    @FXML private TableView<GradeEntry> gradesTable;
    @FXML private TableColumn<GradeEntry, String> gradesStudentColumn, gradesClassColumn, commentColumn;
    @FXML private TableColumn<GradeEntry, Double> gradesColumn;

    private ObservableList<StudentInfo> studentList = FXCollections.observableArrayList();
    private ObservableList<GradeEntry> gradesList = FXCollections.observableArrayList();
    private ObservableList<GradeEntry> allGrades = FXCollections.observableArrayList();

    private String currentTeacherMatiere = "Mathématiques";
    private String currentTeacherEmail = ""; // À remplir avec l'email du prof connecté

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPanes();
        setupTeacherInfo();
        setupTableColumns();
        setupComboBoxes();
        loadStudentsFromDB(); 
        setupEditableColumns();
    }

    private void loadStudentsFromDB() {
        studentList.clear();
        try {
            Connection conn = Connexion.getConnection();
            String query = "SELECT nom, prenom, email, classe FROM d_eleve";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String nomComplet = nom + " " + prenom;
                String email = rs.getString("email");
                String classe = rs.getString("classe");
                
                studentList.add(new StudentInfo(nomComplet, classe, email));
            }
            
            rs.close();
            pstmt.close();
            System.out.println("✅ " + studentList.size() + " élèves chargés");
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement élèves: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadGradesFromDB() {
        allGrades.clear();
        try {
            Connection conn = Connexion.getConnection();
            
            String query = "SELECT e.nom, e.prenom, e.classe, e.email, n.note, n.commentaire, n.id_note " +
                          "FROM d_eleve e " +
                          "INNER JOIN d_note n ON e.email = n.email_eleve AND n.matiere = ? " +
                          "ORDER BY e.classe, e.nom";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, currentTeacherMatiere);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String nomComplet = nom + " " + prenom;
                String classe = rs.getString("classe");
                String email = rs.getString("email");
                
                Double note = rs.getObject("note", Double.class);
                String commentaire = rs.getString("commentaire");
                if (commentaire == null) {
                    commentaire = "";
                }
                
                Integer idNote = rs.getObject("id_note", Integer.class);
                int idNoteValue = (idNote != null) ? idNote : 0;
                
                allGrades.add(new GradeEntry(nomComplet, classe, note, commentaire, idNoteValue, email));
            }
            
            rs.close();
            pstmt.close();
            System.out.println("✅ " + allGrades.size() + " notes chargées");
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement notes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupPanes() {
        if (contentPane != null) contentPane.setVisible(true);
        if (studentPane != null) studentPane.setVisible(false);
        if (gradesPane != null) gradesPane.setVisible(false);
    }

    private void setupTeacherInfo() {
        if (teacherNameLabel != null) teacherNameLabel.setText("Marie Dupont");
        if (teacherMatiereLabel != null) teacherMatiereLabel.setText(currentTeacherMatiere);
    }

    private void setupTableColumns() {
        if (studentNameColumn != null) {
            studentNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        }
        if (studentClassColumn != null) {
            studentClassColumn.setCellValueFactory(cell -> cell.getValue().classeProperty());
        }
        if (studentEmailColumn != null) {
            studentEmailColumn.setCellValueFactory(cell -> cell.getValue().emailProperty());
        }

        if (gradesStudentColumn != null) {
            gradesStudentColumn.setCellValueFactory(cell -> cell.getValue().studentNameProperty());
        }
        if (gradesClassColumn != null) {
            gradesClassColumn.setCellValueFactory(cell -> cell.getValue().classeProperty());
        }
        if (gradesColumn != null) {
            gradesColumn.setCellValueFactory(cell -> cell.getValue().noteProperty());
        }
        if (commentColumn != null) {
            commentColumn.setCellValueFactory(cell -> cell.getValue().commentaireProperty());
        }
    }

    private void setupEditableColumns() {
        if (gradesTable != null) {
            gradesTable.setEditable(true);
        }

        if (gradesColumn != null) {
            gradesColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            gradesColumn.setOnEditCommit(event -> {
                GradeEntry grade = event.getRowValue();
                Double newValue = event.getNewValue();
                
                if (newValue == null || newValue < 0 || newValue > 20) {
                    showWarning("La note doit être entre 0 et 20");
                    gradesTable.refresh();
                    return;
                }
                
                updateNoteInDB(grade, newValue, grade.getCommentaire());
            });
        }

        if (commentColumn != null) {
            commentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            commentColumn.setOnEditCommit(event -> {
                GradeEntry grade = event.getRowValue();
                String newComment = event.getNewValue();
                
                Double currentNote = grade.getNote();
                if (currentNote == null) {
                    showWarning("Veuillez d'abord ajouter une note");
                    gradesTable.refresh();
                    return;
                }
                updateNoteInDB(grade, currentNote, newComment);
            });
        }
    }

    private void updateNoteInDB(GradeEntry grade, Double note, String commentaire) {
        try {
            Connection conn = Connexion.getConnection();
            
            if (grade.getIdNote() > 0) {
                String query = "UPDATE d_note SET note = ?, commentaire = ? WHERE id_note = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setDouble(1, note);
                pstmt.setString(2, commentaire);
                pstmt.setInt(3, grade.getIdNote());
                
                int rows = pstmt.executeUpdate();
                pstmt.close();
                
                if (rows > 0) {
                    grade.setNote(note);
                    grade.setCommentaire(commentaire);
                    gradesTable.refresh();
                    updateClassAverage();
                    showInfo("Note modifiée avec succès");
                }
            } else {
   
                String query = "INSERT INTO d_note (email_eleve, note, commentaire, matiere) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, grade.getEmailEleve());
                pstmt.setDouble(2, note);
                pstmt.setString(3, commentaire);
                pstmt.setString(4, currentTeacherMatiere);
                
                int rows = pstmt.executeUpdate();
                
                if (rows > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        grade.setIdNote(generatedKeys.getInt(1));
                    }
                    grade.setNote(note);
                    grade.setCommentaire(commentaire);
                    gradesTable.refresh();
                    updateClassAverage();
                    showInfo("Note ajoutée avec succès");
                }
                pstmt.close();
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur mise à jour note: " + e.getMessage());
            showError("Erreur lors de la mise à jour: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupComboBoxes() {
        ObservableList<String> classes = FXCollections.observableArrayList(
            "Toutes les classes", "1ère année", "2ème année", "3ème année", "4ème année", "Terminale"
        );

        if (studentClassFilter != null) {
            studentClassFilter.setItems(classes);
            studentClassFilter.setValue("Toutes les classes");
            studentClassFilter.setOnAction(e -> filterStudents());
        }

        if (gradeClassFilter != null) {
            gradeClassFilter.setItems(classes);
            gradeClassFilter.setValue("Toutes les classes");
            gradeClassFilter.setOnAction(e -> filterGrades());
        }
    }
    
    @FXML
    public void gererEleves(ActionEvent event) {

        resetAllButtons();

        if (btnViewStudents != null) {
            btnViewStudents.setStyle("-fx-background-color: linear-gradient(to right, #10b981, #059669); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        }
        
        hideAllPanes();
        if (studentPane != null) {
            studentPane.setVisible(true);
            if (studentTable != null) {
                studentTable.setItems(studentList);
            }
        }
    }

    @FXML
    public void showGrades(ActionEvent event) {
        resetAllButtons();
        
        if (btnViewGrades != null) {
            btnViewGrades.setStyle("-fx-background-color: linear-gradient(to right, #10b981, #059669); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        }
        
        hideAllPanes();
        if (gradesPane != null) {
            gradesPane.setVisible(true);
            loadGradesFromDB();  
            filterGrades();
            updateClassAverage();
        }
    }
    
   
    private void resetAllButtons() {
        if (btnViewStudents != null) {
            btnViewStudents.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #667eea; " +
                "-fx-font-weight: bold; -fx-font-size: 13; -fx-background-radius: 10; " +
                "-fx-border-color: #667eea; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        }
        if (btnViewGrades != null) {
            btnViewGrades.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #667eea; " +
                "-fx-font-weight: bold; -fx-font-size: 13; -fx-background-radius: 10; " +
                "-fx-border-color: #667eea; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        }
    }

    @FXML
    public void showAddGradeDialog(ActionEvent event) {
        Dialog<GradeEntry> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une note");
        dialog.setHeaderText("Nouvelle note pour " + currentTeacherMatiere);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        ComboBox<String> studentCombo = new ComboBox<>();
        studentCombo.setItems(FXCollections.observableArrayList(
            studentList.stream().map(StudentInfo::getName).toList()
        ));
        studentCombo.setPromptText("Sélectionnez un élève");

        TextField noteField = new TextField();
        noteField.setPromptText("Note /20");

        TextArea commentField = new TextArea();
        commentField.setPromptText("Commentaire");
        commentField.setPrefRowCount(3);

        grid.add(new Label("Élève:"), 0, 0);
        grid.add(studentCombo, 1, 0);
        grid.add(new Label("Note:"), 0, 1);
        grid.add(noteField, 1, 1);
        grid.add(new Label("Commentaire:"), 0, 2);
        grid.add(commentField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String studentName = studentCombo.getValue();
                    double note = Double.parseDouble(noteField.getText());
                    String comment = commentField.getText();

                    if (studentName == null || studentName.isEmpty()) {
                        showWarning("Veuillez sélectionner un élève");
                        return null;
                    }

                    if (note < 0 || note > 20) {
                        showWarning("La note doit être entre 0 et 20");
                        return null;
                    }

                    StudentInfo student = studentList.stream()
                        .filter(s -> s.getName().equals(studentName))
                        .findFirst()
                        .orElse(null);

                    if (student != null) {
                        return new GradeEntry(studentName, student.getClasse(), note, comment, 0, student.getEmail());
                    }
                } catch (NumberFormatException e) {
                    showWarning("Format de note invalide");
                }
            }
            return null;
        });

        Optional<GradeEntry> result = dialog.showAndWait();
        result.ifPresent(grade -> {
            addGradeToDB(grade);
        });
    }

    private void addGradeToDB(GradeEntry grade) {
        try {
            Connection conn = Connexion.getConnection();
            String query = "INSERT INTO d_note (email_eleve, note, commentaire, matiere) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, grade.getEmailEleve());
            pstmt.setDouble(2, grade.getNote());
            pstmt.setString(3, grade.getCommentaire());
            pstmt.setString(4, currentTeacherMatiere);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    grade.setIdNote(generatedKeys.getInt(1));
                }
                
                loadGradesFromDB();
                filterGrades();
                updateClassAverage();
                showSuccess("Note ajoutée avec succès");
            }
            
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout note: " + e.getMessage());
            showError("Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUpdateGrades(ActionEvent event) {
        showSuccess("Toutes les notes ont été enregistrées avec succès!");
        updateClassAverage();
    }

    @FXML
    public void deleteGrade(ActionEvent event) {
        GradeEntry selected = gradesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner une note à supprimer");
            return;
        }

        if (selected.getIdNote() == 0) {
            showWarning("Cette note n'existe pas encore dans la base de données");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la note");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la note de " + selected.getStudentName() + " ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            deleteGradeFromDB(selected);
        }
    }

    private void deleteGradeFromDB(GradeEntry grade) {
        try {
            Connection conn = Connexion.getConnection();
            String query = "DELETE FROM d_note WHERE id_note = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, grade.getIdNote());
            
            int rows = pstmt.executeUpdate();
            pstmt.close();
            
            if (rows > 0) {
                allGrades.remove(grade);
                gradesList.remove(grade);
                
                gradesTable.setItems(null);
                gradesTable.setItems(gradesList);
                
                showSuccess("Note supprimée avec succès");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression note: " + e.getMessage());
            showError("Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
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

    private void filterStudents() {
        String selectedClass = studentClassFilter.getValue();
        if (selectedClass == null || selectedClass.equals("Toutes les classes")) {
            studentTable.setItems(studentList);
        } else {
            ObservableList<StudentInfo> filtered = FXCollections.observableArrayList(
                studentList.stream()
                    .filter(s -> s.getClasse().equals(selectedClass))
                    .toList()
            );
            studentTable.setItems(filtered);
        }
    }

    private void filterGrades() {
        String selectedClass = gradeClassFilter.getValue();
        if (selectedClass == null || selectedClass.equals("Toutes les classes")) {
            gradesList = FXCollections.observableArrayList(allGrades);
        } else {
            gradesList = FXCollections.observableArrayList(
                allGrades.stream()
                    .filter(g -> g.getClasse().equals(selectedClass))
                    .toList()
            );
        }
        gradesTable.setItems(gradesList);
    }

    private void updateClassAverage() {
        if (gradesList.isEmpty()) {
            if (classMoyenneLabel != null) classMoyenneLabel.setText("--");
            return;
        }

        double average = gradesList.stream()
            .filter(g -> g.getNote() != null)
            .mapToDouble(GradeEntry::getNote)
            .average()
            .orElse(0.0);

        if (classMoyenneLabel != null) {
            classMoyenneLabel.setText(String.format("%.1f", average));
        }
    }

    private void hideAllPanes() {
        if (contentPane != null) contentPane.setVisible(false);
        if (studentPane != null) studentPane.setVisible(false);
        if (gradesPane != null) gradesPane.setVisible(false);
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

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static class StudentInfo {
        private javafx.beans.property.SimpleStringProperty name;
        private javafx.beans.property.SimpleStringProperty classe;
        private javafx.beans.property.SimpleStringProperty email;

        public StudentInfo(String name, String classe, String email) {
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.classe = new javafx.beans.property.SimpleStringProperty(classe);
            this.email = new javafx.beans.property.SimpleStringProperty(email);
        }

        public String getName() { return name.get(); }
        public javafx.beans.property.StringProperty nameProperty() { return name; }

        public String getClasse() { return classe.get(); }
        public javafx.beans.property.StringProperty classeProperty() { return classe; }

        public String getEmail() { return email.get(); }
        public javafx.beans.property.StringProperty emailProperty() { return email; }
    }

    public static class GradeEntry {
        private javafx.beans.property.SimpleStringProperty studentName;
        private javafx.beans.property.SimpleStringProperty classe;
        private javafx.beans.property.SimpleObjectProperty<Double> note;
        private javafx.beans.property.SimpleStringProperty commentaire;
        private int idNote;
        private String emailEleve;

        public GradeEntry(String studentName, String classe, Double note, String commentaire, int idNote, String emailEleve) {
            this.studentName = new javafx.beans.property.SimpleStringProperty(studentName);
            this.classe = new javafx.beans.property.SimpleStringProperty(classe);
            this.note = new javafx.beans.property.SimpleObjectProperty<>(note);
            this.commentaire = new javafx.beans.property.SimpleStringProperty(commentaire);
            this.idNote = idNote;
            this.emailEleve = emailEleve;
        }

        public String getStudentName() { return studentName.get(); }
        public javafx.beans.property.StringProperty studentNameProperty() { return studentName; }

        public String getClasse() { return classe.get(); }
        public javafx.beans.property.StringProperty classeProperty() { return classe; }

        public Double getNote() { return note.get(); }
        public void setNote(Double value) { note.set(value); }
        public javafx.beans.property.ObjectProperty<Double> noteProperty() { return note; }

        public String getCommentaire() { return commentaire.get(); }
        public void setCommentaire(String value) { commentaire.set(value); }
        public javafx.beans.property.StringProperty commentaireProperty() { return commentaire; }
        
        public int getIdNote() { return idNote; }
        public void setIdNote(int value) { idNote = value; }
        
        public String getEmailEleve() { return emailEleve; }
    }
}