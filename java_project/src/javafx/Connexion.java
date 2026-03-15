/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafx;

/**
 *
 * @author DELL
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Charger le driver MySQL (obligatoire pour MySQL 8)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // URL de connexion
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/projet?serverTimezone=UTC",
                "root",
                "" // mot de passe vide par défaut dans Wamp/XAMPP
            );
            System.out.println("Connexion réussie à la base de données !");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL non trouvé !");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
        return conn;
    }
}
