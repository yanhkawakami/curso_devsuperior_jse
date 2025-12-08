package com.devsuperior.demo.services;

import com.devsuperior.demo.dto.DepartmentDTO;
import com.devsuperior.demo.entities.Department;
import com.devsuperior.demo.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Transactional
    public List<DepartmentDTO> findAll(){
        List<Department> list = departmentRepository.findAllByOrderByNameAsc();
        // List<Department> list2 = departmentRepository.findAll(Sort.by("name")); <- Outra forma de fazer
        return list.stream().map(DepartmentDTO::new).collect(Collectors.toList());
    }
}
