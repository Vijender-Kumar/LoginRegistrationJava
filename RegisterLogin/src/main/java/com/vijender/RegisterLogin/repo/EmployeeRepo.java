package com.vijender.RegisterLogin.repo;

import com.vijender.RegisterLogin.model.EmployeeModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<EmployeeModel, Integer> {
    EmployeeModel findByName(String name);
    void deleteByName(String name);
    EmployeeModel findByEmail(String email);
    void deleteByEmail(String email);
}
