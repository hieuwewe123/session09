package org.example.session09.service;

import lombok.RequiredArgsConstructor;
import org.example.session09.dto.DuplicateResourceException;
import org.example.session09.dto.EmployeeCreateDTO;
import org.example.session09.dto.ResourceNotFoundException;
import org.example.session09.entity.Department;
import org.example.session09.entity.Employee;
import org.example.session09.repository.DepartmentRepository;
import org.example.session09.repository.EmployeeRepository;
import org.example.session09.validation.InvalidFileException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public Employee createEmployee(EmployeeCreateDTO dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "Nhân viên",
                    "email",
                    dto.getEmail()
            );
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phòng ban",
                        dto.getDepartmentId()
                ));

        Employee employee = Employee.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .salary(dto.getSalary())
                .department(department)
                .build();

        return employeeRepository.save(employee);
    }
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${app.api-base-url}")
    private String apiBaseUrl;

    public Employee updateAvatar(Long employeeId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File avatar không được để trống");
        }

        // Validate kích thước < 2MB (2097152 bytes)
        long maxSize = 2 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new InvalidFileException("Kích thước file không được vượt quá 2MB");
        }

        // Validate định dạng
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png"))) {
            throw new InvalidFileException("Định dạng file không hợp lệ. Chỉ chấp nhận .jpg, .jpeg, .png");
        }

        // Tìm employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", employeeId));

        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID() + extension;

        // Tạo thư mục nếu chưa có
        Path uploadPath = Paths.get(uploadDir);
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload: " + e.getMessage());
        }

        // Lưu file
        Path filePath = uploadPath.resolve(filename);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
        }

        // Tạo URL public (sẽ phục vụ file sau)
        String avatarUrl = apiBaseUrl + "/api/v1/files/avatars/" + filename;

        // Update DB
        employee.setAvatarUrl(avatarUrl);
        return employeeRepository.save(employee);
    }
}
