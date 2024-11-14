package ru.otus.java.pro.patterns.part2.dao;

import ru.otus.java.pro.patterns.part2.datasource.DataSource;
import ru.otus.java.pro.patterns.part2.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemsDao {
    private final DataSource dataSource = DataSource.getInstance();

    public void saveItem(Item item) throws SQLException {
        String sql = "INSERT INTO items (title, price) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, item.getTitle());
            statement.setDouble(2, item.getPrice());
            statement.executeUpdate();
        }
    }

    public List<Item> getAllItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT id, title, price FROM items";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                double price = resultSet.getDouble("price");
                items.add(new Item(id, title, price));
            }
        }
        return items;
    }

    public void updateItemPrice(int id, double newPrice) throws SQLException {
        String sql = "UPDATE items SET price = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, newPrice);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }
}
