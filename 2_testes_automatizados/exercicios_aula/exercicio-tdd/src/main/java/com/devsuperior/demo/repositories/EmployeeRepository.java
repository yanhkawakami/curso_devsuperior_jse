package com.devsuperior.demo.repositories;

import com.devsuperior.demo.entities.Department;
import com.devsuperior.demo.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
