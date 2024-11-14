package ru.otus.java.pro.patterns.part1;

import java.util.Arrays;
import java.util.Iterator;

public class Application {
    public static void main(String[] args) {
        Matryoshka red = new Matryoshka(Arrays.asList("red0", "red1", "red2", "red3", "red4", "red5", "red6", "red7", "red8", "red9"));
        Matryoshka green = new Matryoshka(Arrays.asList("green0", "green1", "green2", "green3", "green4", "green5", "green6", "green7", "green8", "green9"));
        Matryoshka blue = new Matryoshka(Arrays.asList("blue0", "blue1", "blue2", "blue3", "blue4", "blue5", "blue6", "blue7", "blue8", "blue9"));
        Matryoshka magenta = new Matryoshka(Arrays.asList("magenta0", "magenta1", "magenta2", "magenta3", "magenta4", "magenta5", "magenta6", "magenta7", "magenta8", "magenta9"));

        Box box = new Box(red, green, blue, magenta);

        System.out.println("Small First Iterator:");
        Iterator<String> smallFirstIterator = box.getSmallFirstIterator();
        while (smallFirstIterator.hasNext()) {
            System.out.println(smallFirstIterator.next());
        }

        System.out.println("\nColor First Iterator:");
        Iterator<String> colorFirstIterator = box.getColorFirstIterator();
        while (colorFirstIterator.hasNext()) {
            System.out.println(colorFirstIterator.next());
        }
    }
}