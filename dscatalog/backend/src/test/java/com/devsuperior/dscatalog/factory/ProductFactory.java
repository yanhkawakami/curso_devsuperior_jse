package com.devsuperior.dscatalog.factory;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.projections.ProductProjection;

import java.time.Instant;

public class ProductFactory {


    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://fakeurl.com", Instant.now());
        product.addCategory(new Category(1L, "Livros"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        return new ProductDTO(createProduct());
    }

    public static ProductProjection createProductProjection() {
        return new ProductProjection() {
            public Long getId() {
                return 1L;
            }

            public String getName() {
                return "Phone";
            }

        };
    }
}
