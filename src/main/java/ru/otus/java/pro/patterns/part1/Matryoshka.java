package ru.otus.java.pro.patterns.part1;

import java.util.List;

public final class Matryoshka {
    private final List<String> items;

    public Matryoshka(List<String> items) {
        this.items = items;
    }

    public List<String> getItems() {
        return items;
    }
}
