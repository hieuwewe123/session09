package org.example.session09.service;


import lombok.RequiredArgsConstructor;
import org.example.session09.dto.DepartmentDTO;
import org.example.session09.entity.Department;
import org.example.session09.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public Department createDepartment(DepartmentDTO dto) {
        Department department = Department.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        return departmentRepository.save(department);
    }
}
