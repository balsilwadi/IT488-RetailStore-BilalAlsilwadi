package sprint4;

import app.sprint1.DBConnection;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.YearMonth;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class HomePage extends Application {
    private int loggedInUserId = -1;
    private Label statusLabel = new Label();
    private UserManager userManager;
    private boolean isLoggedIn = false;

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
        grid.add(headerBox, 0, 0, 5, 1);

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
                int productId = resultSet.getInt("id"); // Defining productId here
                String productName = resultSet.getString("name");
                double productPrice = resultSet.getDouble("price");
                VBox productBox = createProductBox(productId, productName, productPrice);
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

    private VBox createProductBox(int productId, String productName, double productPrice) {
        VBox productBox = new VBox(5);
        productBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #ffffff; -fx-padding: 15;");

        Label nameLabel = new Label(productName);
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        Label priceLabel = new Label("$" + productPrice);
        priceLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #2e8b57;");
        Button addToCartButton = new Button("Buy Now");
        addToCartButton.setStyle("-fx-background-color: #2e8b57; -fx-text-fill: white; -fx-border-radius: 5;");
        addToCartButton.setOnAction(event -> handleBuyNow(productId));

        productBox.getChildren().addAll(nameLabel, priceLabel, addToCartButton);
        return productBox;
    }

    private void handleBuyNow(int productId) {
        if (isLoggedIn) {
            // Assuming you can get the product's ID from the UI or another method

            boolean purchaseSuccess = userManager.addPurchase(loggedInUserId, productId);

            if (purchaseSuccess) {
                showPaymentPopup();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to record the purchase. Please try again.", ButtonType.OK);
                alert.showAndWait();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please log in to proceed with the purchase.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void showPaymentPopup() {
        Dialog<String> paymentDialog = new Dialog<>();
        paymentDialog.setTitle("Enter Payment Details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField cardField = new TextField();
        cardField.setPromptText("Credit Card Number");
        TextField expiryField = new TextField();
        expiryField.setPromptText("MM/YY");
        TextField cvvField = new TextField();
        cvvField.setPromptText("CVV");

        grid.add(new Label("Credit Card:"), 0, 0);
        grid.add(cardField, 1, 0);
        grid.add(new Label("Expiry Date:"), 0, 1);
        grid.add(expiryField, 1, 1);
        grid.add(new Label("CVV:"), 0, 2);
        grid.add(cvvField, 1, 2);

        paymentDialog.getDialogPane().setContent(grid);
        paymentDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        paymentDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (isValidCreditCardDetails(cardField.getText(), expiryField.getText(), cvvField.getText())) {
                    return "VALID";
                }
                return "INVALID";
            }
            return null;
        });

        String result = paymentDialog.showAndWait().orElse("");
        if ("VALID".equals(result)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Thank you for shopping with us!", ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
        } else if ("INVALID".equals(result)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid payment details. Please try again.", ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    private boolean isValidCreditCardDetails(String card, String expiry, String cvv) {
        return card.matches("\\d{16}") && isValidExpiryDate(expiry) && cvv.matches("\\d{3}");
    }

    private boolean isValidExpiryDate(String expiry) {
        Pattern pattern = Pattern.compile("^(0[1-9]|1[0-2])\\/([0-9]{2})$");
        Matcher matcher = pattern.matcher(expiry);
        if (!matcher.matches()) {
            return false;
        }
        int month = Integer.parseInt(matcher.group(1));
        int year = Integer.parseInt(matcher.group(2)) + 2000;

        return YearMonth.now().isBefore(YearMonth.of(year, month));
    }

    private void handlePurchaseHistory() {
        Stage historyStage = new Stage();
        historyStage.setTitle("Purchase History");

        ListView<String> listView = new ListView<String>();
        ObservableList<String> items = FXCollections.observableArrayList();

        List<Product> purchasedProducts = userManager.getUserPurchases(loggedInUserId); // assuming loggedInUserId is the ID of the logged-in user
        if (purchasedProducts.isEmpty()) {
            items.add("No purchases");
        } else {
            for (Product product : purchasedProducts) {
                items.add(product.getName() + " - $" + product.getPrice());
            }
        }

        listView.setItems(items);
        Scene scene = new Scene(listView, 400, 300);
        historyStage.setScene(scene);
        historyStage.show();
    }

    private VBox createHeaderBox() {
        VBox headerBox = new VBox(20);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setStyle("-fx-padding: 15 0 30 0;");
        Button purchaseHistoryButton = new Button("Show Purchase History");
        purchaseHistoryButton.setOnAction(event -> handlePurchaseHistory());

        headerBox.getChildren().addAll(createHeaderLabel(), setupLoginUI(), purchaseHistoryButton);
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
        int userId = userManager.authenticateUser(usernameField.getText(), passwordField.getText());
        if (userId != -1) {
            statusLabel.setText("Logged in as " + usernameField.getText());
            isLoggedIn = true;
            loggedInUserId = userId;
        } else {
            statusLabel.setText("Invalid credentials.");
            isLoggedIn = false;
        }
    }

    private void handleSignup(TextField usernameField, PasswordField passwordField) {
        // Logic for signing up the user using 'usernameField' and 'passwordField'.
        // This method has been left empty as you did not provide details on it earlier.
    }

    public static void main(String[] args) {
        launch(args);
    }
}
