package com.devsuperior.demo.services;

import com.devsuperior.demo.dto.DepartmentDTO;
import com.devsuperior.demo.dto.EmployeeDTO;
import com.devsuperior.demo.entities.Department;
import com.devsuperior.demo.entities.Employee;
import com.devsuperior.demo.repositories.DepartmentRepository;
import com.devsuperior.demo.repositories.EmployeeRepository;
import com.devsuperior.demo.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public Page<EmployeeDTO> findAll(Pageable pageable){

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name"));

        Page<Employee> page = employeeRepository.findAll(pageRequest);
        return page.map(EmployeeDTO::new);
    }

    @Transactional
    public EmployeeDTO insert(EmployeeDTO dto){
        Employee entity = new Employee();
        copyDtoToEntity(entity, dto);
        entity = employeeRepository.save(entity);
        return new EmployeeDTO(entity);
    }

    private void copyDtoToEntity(Employee entity, EmployeeDTO dto){
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        Department department = new Department(dto.getDepartmentId(), null);
        entity.setDepartment(department);
    }
}
