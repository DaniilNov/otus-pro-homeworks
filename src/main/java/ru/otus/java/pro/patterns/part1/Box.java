package ru.otus.java.pro.patterns.part1;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class Box {
    private final Matryoshka red;
    private final Matryoshka green;
    private final Matryoshka blue;
    private final Matryoshka magenta;

    public Box(Matryoshka red, Matryoshka green, Matryoshka blue, Matryoshka magenta) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.magenta = magenta;
    }

    public Iterator<String> getSmallFirstIterator() {
        return new SmallFirstIterator(List.of(red, green, blue, magenta));
    }

    public Iterator<String> getColorFirstIterator() {
        return new ColorFirstIterator(List.of(red, green, blue, magenta));
    }

    /**
     * Итератор для обхода частей матрёшек, начиная с самых маленьких всех цветов
     */
    private static class SmallFirstIterator implements Iterator<String> {
        private final List<Matryoshka> matryoshkas;
        private int currentSize = 0;
        private int currentColor = 0;

        public SmallFirstIterator(List<Matryoshka> matryoshkas) {
            this.matryoshkas = matryoshkas;
        }

        @Override
        public boolean hasNext() {
            return currentSize < 10;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            String result = matryoshkas.get(currentColor).getItems().get(currentSize);
            currentColor++;
            if (currentColor == matryoshkas.size()) {
                currentColor = 0;
                currentSize++;
            }
            return result;
        }
    }

    /**
     * Итератор для обхода частей одной матрёшки от маленькой до большой, затем другой и так далее
     */
    private static class ColorFirstIterator implements Iterator<String> {
        private final List<Matryoshka> matryoshkas;
        private int currentMatryoshka = 0;
        private int currentSize = 0;

        public ColorFirstIterator(List<Matryoshka> matryoshkas) {
            this.matryoshkas = matryoshkas;
        }

        @Override
        public boolean hasNext() {
            return currentMatryoshka < matryoshkas.size() && currentSize < 10;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            String result = matryoshkas.get(currentMatryoshka).getItems().get(currentSize);
            currentSize++;
            if (currentSize == 10) {
                currentSize = 0;
                currentMatryoshka++;
            }
            return result;
        }
    }
}
