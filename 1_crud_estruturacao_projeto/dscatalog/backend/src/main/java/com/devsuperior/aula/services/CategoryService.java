package com.devsuperior.aula.services;

import com.devsuperior.aula.dto.CategoryDTO;
import com.devsuperior.aula.entities.Category;
import com.devsuperior.aula.repositories.CategoryRepository;
import com.devsuperior.aula.services.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
        List<Category> categories = repository.findAll();
        return categories.stream().map(CategoryDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Optional<Category> obj = repository.findById(id);
        Category category = obj.orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada!"));
        return new CategoryDTO(category);
    }

    public CategoryDTO insert (CategoryDTO dto){
        Category entity = new Category();
        copyDtoToEntity(entity, dto);
        entity = repository.save(entity);
        return new CategoryDTO(entity);
    }

    public void copyDtoToEntity(Category category, CategoryDTO dto) {
        category.setName(dto.getName());
    }
}
