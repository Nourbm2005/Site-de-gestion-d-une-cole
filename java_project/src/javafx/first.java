package javafx;

import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class first implements Initializable {

    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private ComboBox<String> roleComboBox;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button signInButton;
    
    @FXML
    private Button signUpButton;
    
    private boolean signInClicked = false;
    private boolean signUpClicked = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }
    
    @FXML
    public void handleSignUp(ActionEvent event) {
        if (!signUpClicked) {
            signUpButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14; " +
                "-fx-border-width: 0; " +
                "-fx-background-radius: 25; " +
                "-fx-border-radius: 25; " +
                "-fx-cursor: hand;"
            );
            signUpClicked = true;
        } else {
            signUpButton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #667eea; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14; " +
                "-fx-border-color: #667eea; " +
                "-fx-border-width: 2; " +
                "-fx-background-radius: 25; " +
                "-fx-border-radius: 25; " +
                "-fx-cursor: hand;"
            );
            signUpClicked = false;
        }
        
        try {
            Parent root = FXMLLoader.load(getClass().getResource("second.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Erreur lors du chargement de la page d'inscription");
            e.printStackTrace();
        }
    }
    
    @FXML
    public void handleSignIn(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();
        
        // Validation des champs
        if (email.isEmpty() || password.isEmpty() || role == null) {
            showError("Veuillez remplir tous les champs");
            return;
        }
        
        // Authentification avec la base de données
        if (authenticateUser(email, password, role)) {
            // Change button color on successful authentication
            if (!signInClicked) {
                signInButton.setStyle(
                    "-fx-background-color: linear-gradient(to right, #10b981, #059669); " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14; " +
                    "-fx-background-radius: 25; " +
                    "-fx-cursor: hand;"
                );
                signInClicked = true;
            }
            
            // Navigation vers la page appropriée
            try {
                String fxmlFile = "";
                switch (role) {
                    case "Directeur":
                        fxmlFile = "third.fxml";
                        break;
                    case "Enseignant":
                        fxmlFile = "fourth.fxml";
                        break;
                    case "Élève":
                        fxmlFile = "fifth.fxml";
                        break;
                    default:
                        showError("Rôle non reconnu");
                        return;
                }
                
                Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showError("Erreur lors du chargement de la page");
                e.printStackTrace();
            }
        } else {
            // Reset button color on failed authentication
            signInButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14; " +
                "-fx-background-radius: 25; " +
                "-fx-cursor: hand;"
            );
            signInClicked = false;
            showError("Email ou mot de passe incorrect pour ce rôle");
        }
    }
    
    /**
     * Authentifie l'utilisateur en vérifiant ses identifiants dans la table correspondant à son rôle
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @param role Rôle sélectionné (Directeur, Enseignant, ou Élève)
     * @return true si l'authentification réussit, false sinon
     */
    private boolean authenticateUser(String email, String password, String role) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // Obtenir la connexion à la base de données
            conn = Connexion.getConnection();
            
            if (conn == null) {
                showError("Impossible de se connecter à la base de données");
                return false;
            }
            
            // Déterminer la table à interroger en fonction du rôle
            String tableName = "";
            switch (role) {
                case "Directeur":
                    tableName = "directeur";
                    break;
                case "Enseignant":
                    tableName = "enseignant";
                    break;
                case "Élève":
                    tableName = "élève";
                    break;
                default:
                    return false;
            }
            
            // Requête SQL pour vérifier l'email et le mot de passe
            String query = "SELECT * FROM " + tableName + " WHERE email = ? AND password = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            // Exécuter la requête
            rs = stmt.executeQuery();
            
            // Si un résultat est trouvé, l'authentification réussit
            if (rs.next()) {
                System.out.println("Authentification réussie pour: " + email + " en tant que " + role);
                return true;
            } else {
                System.out.println("Échec de l'authentification pour: " + email);
                return false;
            }
            
        } catch (SQLException e) {
            showError("Erreur lors de la vérification des identifiants");
            System.out.println("Erreur SQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Fermer les ressources dans l'ordre inverse de leur création
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources: " + e.getMessage());
            }
        }
    }
    
    /**
     * Affiche un message d'erreur dans le label d'erreur
     * @param message Message à afficher
     */
    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }
    
    /**
     * Efface tous les champs du formulaire
     */
    private void clearFields() {
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
        errorLabel.setVisible(false);
    }
}