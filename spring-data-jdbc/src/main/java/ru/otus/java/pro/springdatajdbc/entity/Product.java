package ru.otus.java.pro.springdatajdbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("products")
public class Product {
    @Id
    private Long id;
    private String name;
    private Double price;
}