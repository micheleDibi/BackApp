import javafx.stage.Stage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.image.Image;

public class LoginController extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlloader = new FXMLLoader(LoginController.class.getResource("fxml/login.fxml"));
        
        Scene scene = new Scene(fxmlloader.load());
        primaryStage.setScene(scene);

        primaryStage.setTitle("BackApp Login");
        primaryStage.getIcons().add(new Image("resources/backup.png"));

        primaryStage.show();

        Button btnAccedi = (Button) scene.lookup("#btnAccedi");

        TextField txtEmail = (TextField) scene.lookup("#inputEmail");
        PasswordField pwdPassword = (PasswordField) scene.lookup("#inputPassword");

        Hyperlink passwordDimenticataHP = (Hyperlink) scene.lookup("#hyperlinkPassowrdDimenticata");
        Hyperlink restistratiHP = (Hyperlink) scene.lookup("#hyperlinkRegistrati");

        passwordDimenticataHP.setOnAction(
            e -> {
                // TODO - impostare link di passowrd dimenticata
            }
        ); 

        restistratiHP.setOnAction(
            e -> {
                // TODO - impostare link di registrazione
            }
        );

        btnAccedi.setOnAction(
            e -> {
                String email = txtEmail.getText();
                String password = pwdPassword.getText();

                try {

                    int response = ConnectionManager.login(email, password);

                    if (response > 0) {
                        
                        // App app = new App(response);
                        
                    }

                } catch (Exception exc) {
                    Alert a = new Alert(AlertType.ERROR);
                    a.setTitle("Errore!");
                    a.setContentText(exc.getMessage());

                    a.showAndWait();
                }
            }
        );
    }

    public static void main(String[] args) {
        launch();
    }
}
