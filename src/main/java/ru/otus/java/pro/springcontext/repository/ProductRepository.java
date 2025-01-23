package ru.otus.java.pro.springcontext.repository;


import org.springframework.stereotype.Repository;
import ru.otus.java.pro.springcontext.model.Product;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {
    private final List<Product> products = new ArrayList<>();

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 10; i++) {
            products.add(new Product(i, "Product " + i, i * 10.0));
        }
        products.forEach(System.out::println);
    }

    public List<Product> findAll() {
        return products;
    }

    public Optional<Product> findById(int id) {
        return products.stream().filter(product -> product.getId() == id).findFirst();
    }
}