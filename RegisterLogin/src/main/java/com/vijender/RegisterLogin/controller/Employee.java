package com.vijender.RegisterLogin.controller;

import com.vijender.RegisterLogin.dto.LoginDto.LoginDtoRequest;
import com.vijender.RegisterLogin.dto.LoginDto.LoginDtoResponse;
import com.vijender.RegisterLogin.dto.ResponseHandler;
import com.vijender.RegisterLogin.model.EmployeeModel;
import com.vijender.RegisterLogin.repo.EmployeeRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class Employee {
    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/employee")
    public ResponseEntity<List<EmployeeModel>> getAllEmployees() {
        List<EmployeeModel> employeeData = employeeRepo.findAll();
        if (!employeeData.isEmpty()) {
            return new ResponseEntity<>(employeeData, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
        }
    }

    //    @PostMapping("/employee")
//    public ResponseEntity<CustomResponse<EmployeeModel>> addUser(@RequestBody EmployeeModel EmployeeModel) {
//        EmployeeModel users = employeeRepo.findByEmail(EmployeeModel.getEmail());
//        if (users == null) {
//            EmployeeModel.setPassword(bCryptPasswordEncoder.encode(EmployeeModel.getPassword()));
//            return new ResponseEntity<>(
//                    new CustomResponse<>("success", "User created successfully", employeeRepo.save(EmployeeModel)),
//                    HttpStatus.CREATED
//            );
//        } else {
//            return new ResponseEntity<>(
//                    new CustomResponse<>("error", "User already exists with email: " + EmployeeModel.getEmail(), null),
//                    HttpStatus.CONFLICT
//            );
//        }
//    }
    @PostMapping("/employee")
    public ResponseEntity<Object> addUser(@RequestBody EmployeeModel employeeModel) {
        EmployeeModel user = employeeRepo.findByEmail(employeeModel.getEmail());
        if (user == null) {
            employeeModel.setPassword(bCryptPasswordEncoder.encode(employeeModel.getPassword()));
            EmployeeModel savedUser = employeeRepo.save(employeeModel);
            return ResponseHandler.responseBuilder(
                    "User created successfully", HttpStatus.CREATED, savedUser
            );
        } else {
            return ResponseHandler.responseBuilder(
                    "User already exists with email: " + employeeModel.getEmail(),
                    HttpStatus.CONFLICT,
                    null
            );
        }
    }


    //    update(put),delete -- /user/{id}
//    @PutMapping("/employee/{name}")
//    public ResponseEntity<EmployeeModel> updateUser(@PathVariable String name, @RequestBody EmployeeModel updateUser) {
//        EmployeeModel users = employeeRepo.findByName(name);
    @PutMapping("/employee")
    public ResponseEntity<EmployeeModel> updateUser(@RequestParam String email, @RequestBody EmployeeModel updateUser) {
        EmployeeModel users = employeeRepo.findByEmail(email);
        System.out.println("USER DATA::::" + users);
        if (users != null) {
            users.setAge(updateUser.getAge() != 0 ? updateUser.getAge() : users.getAge());
            users.setOccupation(updateUser.getOccupation() != null ? updateUser.getOccupation() : users.getOccupation());
            users.setEmail(updateUser.getEmail() != null ? updateUser.getEmail() : users.getEmail());
            if (updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()) {
                users.setPassword(bCryptPasswordEncoder.encode(updateUser.getPassword()));
            }
            final var update = employeeRepo.save(users);

            return new ResponseEntity<>(update, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/employee")
    @Transactional
    public ResponseEntity<String> delete(@RequestParam String email) {
        employeeRepo.deleteByEmail(email);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDtoRequest loginRequest) {
        EmployeeModel user = employeeRepo.findByEmail(loginRequest.getEmail());
        if (user != null && bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            LoginDtoResponse loginDtoResponse = LoginDtoResponse.builder()
                    .occupation(user.getOccupation()).email(user.getEmail()).age(user.getAge()).build();
            return ResponseHandler.responseBuilder(
                    "Login Successful", HttpStatus.OK, loginDtoResponse
            );
        } else {
            return ResponseHandler.responseBuilder(
                    "Invalid email or password: " + loginRequest.getEmail(),
                    HttpStatus.UNAUTHORIZED,
                    null
            );
        }
    }

}
