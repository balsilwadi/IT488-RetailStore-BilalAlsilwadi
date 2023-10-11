package app.sprint1;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HomePage extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20; -fx-background-color: #f8f8f8;");

        // Configure columns to each take up 20% of the grid's width
        for (int i = 0; i < 5; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(20);
            grid.getColumnConstraints().add(column);
        }

        // Header
        Label header = new Label("Homepage");
        header.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setSpacing(20);
        headerBox.setStyle("-fx-padding: 15 0 30 0;");
        grid.add(headerBox, 0, 0, 5, 1); // Span across five columns

        Connection connection = DBConnection.getConnection();
        int column = 0;
        int row = 1; // Start from the second row as the first row is for the header
        if (connection != null) {
            String query = "SELECT * FROM products LIMIT 10";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String productName = resultSet.getString("name");
                double productPrice = resultSet.getDouble("price");

                VBox productBox = new VBox(5);
                productBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #ffffff; -fx-padding: 15;");

                Label nameLabel = new Label(productName);
                nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

                Label priceLabel = new Label("$" + productPrice);
                priceLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #2e8b57;");

                Button addToCartButton = new Button("Buy Now");
                addToCartButton.setStyle("-fx-background-color: #2e8b57; -fx-text-fill: white; -fx-border-radius: 5;");

                productBox.getChildren().addAll(nameLabel, priceLabel, addToCartButton);
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

        Scene scene = new Scene(grid, 800, 450); // Adjusted window size
        primaryStage.setTitle("Retail Website");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
