package ru.otus.java.pro.springcontext.service;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.otus.java.pro.springcontext.model.Product;
import ru.otus.java.pro.springcontext.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class Cart {
    private final ProductRepository productRepository;
    @Getter
    private List<Product> items = new ArrayList<>();

    @Autowired
    public Cart(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProductById(int id) {
        productRepository.findById(id).ifPresent(items::add);
    }

    public void removeProductById(int id) {
        items.removeIf(product -> product.getId() == id);
    }

}
