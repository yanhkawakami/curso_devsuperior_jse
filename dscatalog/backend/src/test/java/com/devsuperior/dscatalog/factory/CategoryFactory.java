package com.devsuperior.dscatalog.factory;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

import java.time.Instant;

public class CategoryFactory {


    public static Category createCategory() {
        Category category = new Category(1L, "Livros");
        return category;
    }

}
