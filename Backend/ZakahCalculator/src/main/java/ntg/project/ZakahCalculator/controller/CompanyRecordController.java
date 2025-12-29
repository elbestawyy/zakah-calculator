package ntg.project.ZakahCalculator.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntg.project.ZakahCalculator.dto.request.ZakahCompanyRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordSummaryResponse;
import ntg.project.ZakahCalculator.service.ZakahCompanyRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/records/company")
@RequiredArgsConstructor
@Slf4j
public class CompanyRecordController {

    private final ZakahCompanyRecordService zakahCompanyRecordService;

    // ================= CREATE =================

    @PostMapping("/calculate")
    public ResponseEntity<ZakahCompanyRecordSummaryResponse> calculateAndSaveZakah(
            @Valid @RequestBody ZakahCompanyRecordRequest request) {
        ZakahCompanyRecordSummaryResponse response =
                zakahCompanyRecordService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================= READ =================

    // Get all summaries for authenticated user
    @GetMapping("/summaries")
    public ResponseEntity<List<ZakahCompanyRecordSummaryResponse>> getAllSummaries() {
        return ResponseEntity.ok(
                zakahCompanyRecordService.findAllSummariesByUserId()
        );
    }

    // Get detailed record by id
    @GetMapping("/{id}")
    public ResponseEntity<ZakahCompanyRecordResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                zakahCompanyRecordService.findById(id)
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        zakahCompanyRecordService.deleteByIdAndUserId(id);
        return ResponseEntity.noContent().build();
    }
}
