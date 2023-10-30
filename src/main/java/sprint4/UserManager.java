package sprint4;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private Connection connection;

    public UserManager(Connection connection) {
        this.connection = connection;
    }

    public boolean registerUser(String username, String password, String email) {
        try {
            String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password); // Directly storing plaintext password
            statement.setString(3, email);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int authenticateUser(String username, String password) {
        try {
            String query = "SELECT id, password FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                if (password.equals(rs.getString("password"))) {
                    return rs.getInt("id");
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public boolean addPurchase(int userId, int productId) {
        try {
            String query = "INSERT INTO purchase_history (user_id, product_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> getUserPurchases(int userId) {
        List<Product> purchasedProducts = new ArrayList<>();
        try {
            String query = "SELECT p.* FROM products p INNER JOIN purchase_history ph ON p.id = ph.product_id WHERE ph.user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Product product = new Product(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"));
                purchasedProducts.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchasedProducts;
    }
}

