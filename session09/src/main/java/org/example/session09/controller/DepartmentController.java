package org.example.session09.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.session09.dto.ApiResponse;
import org.example.session09.dto.DepartmentDTO;
import org.example.session09.entity.Department;
import org.example.session09.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Department>> createDepartment(
            @Valid @RequestBody DepartmentDTO departmentDTO) {

        Department savedDepartment = departmentService.createDepartment(departmentDTO);

        ApiResponse<Department> response = ApiResponse.success(
                "Tạo phòng ban thành công",
                savedDepartment
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
