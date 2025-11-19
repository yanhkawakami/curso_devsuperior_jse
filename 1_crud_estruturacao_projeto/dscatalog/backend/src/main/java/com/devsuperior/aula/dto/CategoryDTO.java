package com.devsuperior.aula.dto;

import com.devsuperior.aula.entities.Category;
import com.devsuperior.aula.repositories.CategoryRepository;

public class CategoryDTO {

    private Long id;
    private String name;

    CategoryDTO(){}

    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryDTO(Category entity){
        this.id = entity.getId();
        this.name = entity.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
