package ru.otus.java.pro.springdatajdbc.repository;


import org.springframework.data.repository.CrudRepository;
import ru.otus.java.pro.springdatajdbc.entity.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
}