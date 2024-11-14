package ru.otus.java.pro.patterns.part2.service;

import ru.otus.java.pro.patterns.part2.dao.ItemsDao;
import ru.otus.java.pro.patterns.part2.model.Item;

import java.sql.SQLException;
import java.util.List;

public class ItemsService {
    private final ItemsDao itemsDao = new ItemsDao();

    public void saveItems() throws SQLException {
        for (int i = 1; i <= 100; i++) {
            Item item = new Item(i, "Item " + i, i * 10.0);
            itemsDao.saveItem(item);
        }
    }

    public void doublePrices() throws SQLException {
        List<Item> items = itemsDao.getAllItems();
        for (Item item : items) {
            double newPrice = item.getPrice() * 2;
            itemsDao.updateItemPrice(item.getId(), newPrice);
        }
    }

    public List<Item> getAllItems() throws SQLException {
        return itemsDao.getAllItems();
    }

    public void printAllItems() throws SQLException {
        List<Item> items = getAllItems();
        for (Item item : items) {
            System.out.println(item);
        }
    }
}
