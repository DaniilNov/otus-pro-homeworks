package ru.otus.java.pro.springcontext.service;


import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(Cart.class);

    private final ProductRepository productRepository;
    @Getter
    private List<Product> items = new ArrayList<>();

    @Autowired
    public Cart(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProductById(int id) {
        productRepository.findById(id).ifPresentOrElse(
                items::add,
                () -> logger.error("Product with id {} not found", id)
        );
    }

    public void removeProductById(int id) {
        items.removeIf(product -> product.getId() == id);
    }

}
