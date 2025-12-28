package ntg.project.ZakahCalculator.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntg.project.ZakahCalculator.dto.request.ZakahIndividualRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahIndividualRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahIndividualRecordSummaryResponse;
import ntg.project.ZakahCalculator.service.ZakahIndividualRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/zakah/individual")
@RequiredArgsConstructor
@Slf4j
public class ZakahIndividualRecordController {

    private final ZakahIndividualRecordService zakahIndividualRecordService;

    // Get detailed individual zakah record by ID
    @GetMapping("/{id}")
    public ResponseEntity<ZakahIndividualRecordResponse> getZakahRecordById(@PathVariable Long id) {
        ZakahIndividualRecordResponse response = zakahIndividualRecordService.findByIdAndUserId(id);
        return ResponseEntity.ok(response);
    }

    // Get all individual zakah record summaries
    @GetMapping("/all/summaries")
    public ResponseEntity<List<ZakahIndividualRecordSummaryResponse>> getAllZakahRecordSummaries() {
        List<ZakahIndividualRecordSummaryResponse> responses = zakahIndividualRecordService.findAllSummariesByUserId();
        return ResponseEntity.ok(responses);
    }

    // Delete individual zakah record by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZakahRecord(@PathVariable Long id) {
        zakahIndividualRecordService.deleteByIdAndUserId(id);
        return ResponseEntity.noContent().build();
    }

    // Calculate and save individual zakah record
    @PostMapping("/calculate")
    public ResponseEntity<ZakahIndividualRecordSummaryResponse> calculateAndSaveZakah(
            @Valid @RequestBody ZakahIndividualRecordRequest request) {
        ZakahIndividualRecordSummaryResponse response = zakahIndividualRecordService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}