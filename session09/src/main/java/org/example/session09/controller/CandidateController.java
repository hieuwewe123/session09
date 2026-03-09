package org.example.session09.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.session09.dto.ApiResponse;
import org.example.session09.dto.CandidateApplyDTO;
import org.example.session09.entity.Candidate;
import org.example.session09.service.CandidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Candidate>> apply(
            @Valid @ModelAttribute CandidateApplyDTO dto) {

        Candidate savedCandidate = candidateService.applyCandidate(dto);

        return new ResponseEntity<>(
                ApiResponse.success("Nộp hồ sơ ứng tuyển thành công", savedCandidate),
                HttpStatus.CREATED
        );
    }
}
