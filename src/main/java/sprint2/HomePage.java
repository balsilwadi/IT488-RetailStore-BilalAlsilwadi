package sprint2;

import app.sprint1.DBConnection;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HomePage extends Application {

    private Label statusLabel = new Label();
    private UserManager userManager; // Assuming UserManager class is in the same package

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane grid = initializeGrid();
        userManager = new UserManager(DBConnection.getConnection());

        populateProductGrid(grid);
        Scene scene = new Scene(grid, 800, 450);

        primaryStage.setTitle("Retail Website");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane initializeGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20; -fx-background-color: #f8f8f8;");

        VBox headerBox = createHeaderBox();
        grid.add(headerBox, 0, 0, 5, 1); // Span across five columns

        for (int i = 0; i < 5; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(20);
            grid.getColumnConstraints().add(column);
        }
        return grid;
    }

    private void populateProductGrid(GridPane grid) throws Exception {
        Connection connection = DBConnection.getConnection();
        int column = 0;
        int row = 1;
        if (connection != null) {
            String query = "SELECT * FROM products LIMIT 10";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String productName = resultSet.getString("name");
                double productPrice = resultSet.getDouble("price");
                VBox productBox = createProductBox(productName, productPrice);
                grid.add(productBox, column, row);

                column++;
                if (column > 4) {
                    column = 0;
                    row++;
                }
            }
            resultSet.close();
            statement.close();
        }
    }

    private VBox createProductBox(String productName, double productPrice) {
        VBox productBox = new VBox(5);
        productBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #ffffff; -fx-padding: 15;");

        Label nameLabel = new Label(productName);
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        Label priceLabel = new Label("$" + productPrice);
        priceLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #2e8b57;");
        Button addToCartButton = new Button("Buy Now");
        addToCartButton.setStyle("-fx-background-color: #2e8b57; -fx-text-fill: white; -fx-border-radius: 5;");

        productBox.getChildren().addAll(nameLabel, priceLabel, addToCartButton);
        return productBox;
    }

    private VBox createHeaderBox() {
        VBox headerBox = new VBox(20);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setStyle("-fx-padding: 15 0 30 0;");
        headerBox.getChildren().addAll(createHeaderLabel(), setupLoginUI());
        return headerBox;
    }

    private Label createHeaderLabel() {
        Label header = new Label("Homepage");
        header.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        return header;
    }

    private HBox setupLoginUI() {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> handleLogin(usernameField, passwordField));

        Button signupButton = new Button("Signup");
        signupButton.setOnAction(event -> handleSignup(usernameField, passwordField));

        HBox loginBox = new HBox(10);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.getChildren().addAll(usernameField, passwordField, loginButton, signupButton, statusLabel);

        return loginBox;
    }

    private void handleLogin(TextField usernameField, PasswordField passwordField) {
        if (userManager.authenticateUser(usernameField.getText(), passwordField.getText())) {
            statusLabel.setText("Logged in as " + usernameField.getText());
        } else {
            statusLabel.setText("Invalid credentials.");
        }
    }

    private void handleSignup(TextField usernameField, PasswordField passwordField) {
        if (userManager.registerUser(usernameField.getText(), passwordField.getText(), "example@email.com")) {
            statusLabel.setText("Registered successfully! Please login.");
        } else {
            statusLabel.setText("Error during registration.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
