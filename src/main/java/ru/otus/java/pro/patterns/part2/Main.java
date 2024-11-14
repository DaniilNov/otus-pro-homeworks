package ru.otus.java.pro.patterns.part2;

import ru.otus.java.pro.patterns.part2.service.ItemsServiceProxy;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {

        //Чего-то с кодировкой. Добавил для вывода русских букв в консоль
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        ItemsServiceProxy itemsServiceProxy = new ItemsServiceProxy();

        try {
            System.out.println("Демонстрация сохранения 100 новых Items");
            itemsServiceProxy.saveItems();
            System.out.println("100 новых Items сохранены успешно");
        } catch (RuntimeException e) {
            System.err.println("Неуспешное сохранение Items: " + e.getMessage());
        }

        System.out.println("Вывод всех Item после сохранения:");
        itemsServiceProxy.printAllItems();

        try {
            System.out.println("Демонстрация удвоения цен всех Items");
            itemsServiceProxy.doublePrices();
            System.out.println("Операция удвоения цен для всех элементов в базе данных прошла успешно");
        } catch (RuntimeException e) {
            System.err.println("Операция удвоения цен для всех элементов в базе данных прошла неуспешно: " + e.getMessage());
        }

        System.out.println("Вывод всех Item после удвоения цен:");
        itemsServiceProxy.printAllItems();
    }
}
