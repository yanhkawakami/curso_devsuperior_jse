package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;

public class ProductFactory {
    public static Product createProduct(){
        Category category = CategoryFactory.createCategory();
        Product product = new Product(1L, "iPhone", "Celular da Apple", 10000.0, "https://bucket/img-iphone");
        product.getCategories().add(category);
        return product;
    }

    public static Product createProduct(Long id, String name, String description, Double price, String imgUrl, Category category){
        Product product = new Product(id, name, description, price, imgUrl);
        product.getCategories().add(category);
        return product;
    }
}
