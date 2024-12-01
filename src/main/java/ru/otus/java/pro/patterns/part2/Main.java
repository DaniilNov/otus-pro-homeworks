package ru.otus.java.pro.patterns.part2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.pro.patterns.part2.service.ItemsServiceProxy;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        ItemsServiceProxy itemsServiceProxy = new ItemsServiceProxy();

        try {
            log.info("Демонстрация сохранения 100 новых Items");
            itemsServiceProxy.saveItems();
            log.info("100 новых Items сохранены успешно");
        } catch (RuntimeException e) {
            log.error("Неуспешное сохранение Items: {}", e.getMessage());
        }

        log.info("Вывод всех Item после сохранения:");
        itemsServiceProxy.printAllItems();

        try {
            log.info("Демонстрация удвоения цен всех Items");
            itemsServiceProxy.doublePrices();
            log.info("Операция удвоения цен для всех элементов в базе данных прошла успешно");
        } catch (RuntimeException e) {
            log.info("Операция удвоения цен для всех элементов в базе данных прошла неуспешно: " + e.getMessage());
        }

        log.error("Вывод всех Item после удвоения цен:");
        itemsServiceProxy.printAllItems();
    }
}
