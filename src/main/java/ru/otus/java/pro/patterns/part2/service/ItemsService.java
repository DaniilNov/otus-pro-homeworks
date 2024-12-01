package ru.otus.java.pro.patterns.part2.service;

import ru.otus.java.pro.patterns.part2.dao.ItemsDao;
import ru.otus.java.pro.patterns.part2.model.Item;

import java.sql.SQLException;
import java.util.List;

public class ItemsService {
    private final ItemsDao itemDao = new ItemsDao();

    public void saveItems() throws SQLException {
        for (int i = 1; i <= 100; i++) {
            Item item = new Item(i, "Item " + i, i * 10.0);
            itemDao.saveItem(item);
        }
    }

    public void doublePrices() throws SQLException {
        List<Item> items = itemDao.getAllItems();
        for (Item item : items) {
            itemDao.updateItemPrice(item.getId(), item.getPrice() * 2);
        }
    }

    public List<Item> getAllItems() throws SQLException {
        return itemDao.getAllItems();
    }

    public void printAllItems() throws SQLException {
        List<Item> items = getAllItems();
        for (Item item : items) {
            System.out.println(item);
        }
    }
}
