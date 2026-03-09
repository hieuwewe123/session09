package org.example.session09.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.session09.dto.ApiResponse;
import org.example.session09.dto.EmployeeCreateDTO;
import org.example.session09.entity.Employee;
import org.example.session09.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Employee>> createEmployee(
            @Valid @RequestBody EmployeeCreateDTO dto) {

        Employee savedEmployee = employeeService.createEmployee(dto);

        ApiResponse<Employee> response = ApiResponse.success(
                "Thêm nhân viên thành công",
                savedEmployee
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PutMapping("/{id}/avatar")
    public ResponseEntity<ApiResponse<Employee>> updateAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        Employee updatedEmployee = employeeService.updateAvatar(id, file);

        ApiResponse<Employee> response = ApiResponse.success(
                "Cập nhật avatar thành công",
                updatedEmployee
        );

        return ResponseEntity.ok(response);
    }
}