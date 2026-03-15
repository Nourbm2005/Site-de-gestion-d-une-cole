/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafx;

import java.sql.Connection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("first.fxml"));

            Scene scene = new Scene(root);

            stage.setTitle("Système de Gestion Scolaire");
            stage.setScene(scene);
            stage.setResizable(false);

            stage.show();

            System.out.println("Application started successfully!");

        } catch (Exception e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        testDatabaseConnection();

        launch(args);
    }

    private static void testDatabaseConnection() {
        try {

            
            Connection conn = Connexion.getConnection();
            if (conn != null) {
                System.out.println("✓ Database connection successful!");
                conn.close();
            } else {
                System.out.println("✗ Database connection failed!");
            }
             
            System.out.println("Database connection test skipped (not implemented yet)");
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    @Override
    public void stop() {

        System.out.println("Application closing...");

    }}
