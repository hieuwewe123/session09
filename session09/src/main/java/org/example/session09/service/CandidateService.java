package org.example.session09.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.example.session09.dto.CandidateApplyDTO;
import org.example.session09.dto.DuplicateResourceException;
import org.example.session09.entity.Candidate;
import org.example.session09.repository.CandidateRepository;
import org.example.session09.validation.InvalidFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;


    @Transactional(rollbackFor = Exception.class)  // Rollback nếu bất kỳ exception nào
    public Candidate applyCandidate(CandidateApplyDTO dto) {
        // 1. Validate file CV
        MultipartFile cvFile = dto.getCvFile();
        if (cvFile == null || cvFile.isEmpty()) {
            throw new InvalidFileException("File CV không được để trống");
        }

        String contentType = cvFile.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new InvalidFileException("File phải là định dạng PDF");
        }

        if (cvFile.getSize() > 5 * 1024 * 1024) {
            throw new InvalidFileException("File CV không được vượt quá 5MB");
        }

        // 2. Upload lên Cloudinary
        String cvUrl;
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    cvFile.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "folder", "candidates/cvs",
                            "public_id", "cv_" + dto.getEmail().replace("@", "_") + "_" + System.currentTimeMillis()
                    )
            );
            cvUrl = (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload CV lên Cloudinary: " + e.getMessage());
        }



        // 3. Kiểm tra email trùng (nghiệp vụ)
        if (candidateRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Ứng viên", "email", dto.getEmail());
        }

        // 4. Lưu vào DB (nếu đến đây → transaction commit)
        Candidate candidate = Candidate.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .cvUrl(cvUrl)
                .build();

        return candidateRepository.save(candidate);
    }
}
