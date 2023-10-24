package sprint3;


import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScene {

    private TextField usernameField;
    private PasswordField passwordField;
    private TextField emailField; // for signup
    private Button loginButton;
    private Button signupButton;

    public Scene createLoginScene(Stage stage) {
        VBox vbox = new VBox(10);

        Label label = new Label("Login");
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        loginButton = new Button("Login");

        Label signupLabel = new Label("Signup");
        emailField = new TextField();
        emailField.setPromptText("Email");
        signupButton = new Button("Signup");

        vbox.getChildren().addAll(label, usernameField, passwordField, loginButton, signupLabel, emailField, signupButton);

        return new Scene(vbox, 300, 250);
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public TextField getEmailField() {
        return emailField;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public Button getSignupButton() {
        return signupButton;
    }
}
