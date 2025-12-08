package com.devsuperior.demo.controllers;


import com.devsuperior.demo.dto.DepartmentDTO;
import com.devsuperior.demo.dto.EmployeeDTO;
import com.devsuperior.demo.services.DepartmentService;
import com.devsuperior.demo.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<Page<EmployeeDTO>> findAll(Pageable pageable){
        Page<EmployeeDTO> list = employeeService.findAll(pageable);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> insert(@RequestBody EmployeeDTO dto){
        dto = employeeService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
}
