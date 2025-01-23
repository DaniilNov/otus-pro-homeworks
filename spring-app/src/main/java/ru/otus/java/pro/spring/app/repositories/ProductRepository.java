package ru.otus.java.pro.spring.app.repositories;

import org.springframework.stereotype.Component;
import ru.otus.java.pro.spring.app.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ProductRepository {
    private final List<Product> products = new ArrayList<>();

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public Optional<Product> findById(Long id) {
        return products.stream().filter(product -> product.getId().equals(id)).findFirst();
    }

    public void save(Product product) {
        products.add(product);
    }
}