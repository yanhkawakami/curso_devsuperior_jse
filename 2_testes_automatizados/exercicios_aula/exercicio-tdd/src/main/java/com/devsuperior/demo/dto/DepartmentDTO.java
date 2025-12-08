package com.devsuperior.demo.dto;

import com.devsuperior.demo.entities.Department;

public class DepartmentDTO {
    private Long id;
    private String name;

    public DepartmentDTO() {
    }

    public DepartmentDTO(Department entity){
        id = entity.getId();
        name = entity.getName();
    }

    public DepartmentDTO(Long id, String name) {
        this.id = id;
        this.name = name;
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
