package ru.otus.java.pro.patterns.part2.service;

import ru.otus.java.pro.patterns.part2.datasource.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ItemsServiceProxy {
    private final ItemsService itemsService = new ItemsService();
    private final DataSource dataSource = DataSource.getInstance();

    public void saveItems() {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                itemsService.saveItems();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Transaction failed, rolled back", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error managing transaction", e);
        }
    }

    public void doublePrices() {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                itemsService.doublePrices();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Transaction failed, rolled back", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error managing transaction", e);
        }
    }

    public void printAllItems() {
        try {
            itemsService.printAllItems();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching items", e);
        }
    }
}