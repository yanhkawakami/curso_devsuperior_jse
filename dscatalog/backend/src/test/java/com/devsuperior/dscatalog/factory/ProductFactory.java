package com.devsuperior.dscatalog.factory;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

import java.time.Instant;

public class ProductFactory {


    public static Product createdProduct() {
        Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://fakeurl.com", Instant.now());
        product.addCategory(new Category(2L, "Electronics"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        return new ProductDTO(createdProduct());
    }
}
