package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factory.ProductFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        countTotalProducts = 25L; // Número de objetos do banco
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(1L);

        assert result.isEmpty();
    }

    @Test
    public void saveShouldPersistsWithAutoIncrementWhenIdIsNull() {
        Product product = ProductFactory.createProduct();
        product.setId(null);
        repository.save(product);

        assert product.getId() != null;
        assert product.getId().equals(countTotalProducts + 1L);
    }

    @Test
    public void findByIdShouldReturnOptionalWhenIdExists() {
        Optional<Product> result = repository.findById(1L);

        assert result.isPresent();
    }

    @Test
    public void findByIdShouldReturnNullWhenIdDoesNotExists() {
        Optional<Product> result = repository.findById(26L);

        assert result.isEmpty();
    }


}
