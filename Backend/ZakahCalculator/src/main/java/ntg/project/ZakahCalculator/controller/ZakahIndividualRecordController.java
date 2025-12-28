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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/zakah/individual")
@RequiredArgsConstructor
@Slf4j
public class ZakahIndividualRecordController {

    private final ZakahIndividualRecordService zakahIndividualRecordService;
    //Get detailed individual zakah record by ID.
    @GetMapping("/{id}")
    public ResponseEntity<ZakahIndividualRecordResponse> getZakahRecordById(@PathVariable Long id) {

        log.info("Fetching detailed individual zakah record with id: {}", id);

        ZakahIndividualRecordResponse response = zakahIndividualRecordService.findByIdAndUserId(id);

        return ResponseEntity.ok(response);
    }

    //Get summary of individual zakah record by ID.
    @GetMapping("/summary/{id}")
    public ResponseEntity<ZakahIndividualRecordSummaryResponse> getZakahRecordSummaryById(
            @PathVariable Long id) {

        log.info("Fetching individual zakah record summary with id: {}", id);

        ZakahIndividualRecordSummaryResponse response = zakahIndividualRecordService.findSummaryByIdAndUserId(id);

        return ResponseEntity.ok(response);
    }

    //Get all detailed individual zakah records
    @GetMapping("/all")
    public ResponseEntity<List<ZakahIndividualRecordResponse>> getAllZakahRecords() {

        log.info("Fetching all detailed individual zakah records for authenticated user");

        List<ZakahIndividualRecordResponse> responses = zakahIndividualRecordService.findAllByUserId();

        return ResponseEntity.ok(responses);
    }

    //Get all individual zakah record summaries
    @GetMapping("/all/summaries")
    public ResponseEntity<List<ZakahIndividualRecordSummaryResponse>> getAllZakahRecordSummaries() {

        log.info("Fetching all individual zakah record summaries for authenticated user");

        List<ZakahIndividualRecordSummaryResponse> responses = zakahIndividualRecordService.findAllSummariesByUserId();

        return ResponseEntity.ok(responses);
    }

    //Get the latest detailed individual zakah record
    @GetMapping("/latest")
    public ResponseEntity<ZakahIndividualRecordResponse> getLatestZakahRecord() {

        log.info("Fetching latest individual zakah record for authenticated user");

        ZakahIndividualRecordResponse response = zakahIndividualRecordService.findLatestByUserId();

        return ResponseEntity.ok(response);
    }

    //Get the latest individual zakah record summary
    @GetMapping("/latest/summary")
    public ResponseEntity<ZakahIndividualRecordSummaryResponse> getLatestZakahRecordSummary() {

        log.info("Fetching latest individual zakah record summary for authenticated user");

        ZakahIndividualRecordSummaryResponse response = zakahIndividualRecordService.findLatestSummaryByUserId();

        return ResponseEntity.ok(response);
    }


    //Delete individual zakah record by ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZakahRecord(@PathVariable Long id) {

        log.info("Deleting individual zakah record with id: {}", id);

        zakahIndividualRecordService.deleteByIdAndUserId(id);

        log.info("Successfully deleted individual zakah record with id: {}", id);

        return ResponseEntity.noContent().build();
    }

    //Calculate and save individual zakah record
    @PostMapping("/calculate")
    public ResponseEntity<ZakahIndividualRecordResponse> calculateAndSaveZakah(
            @Valid @RequestBody ZakahIndividualRecordRequest request) {

        log.info("Received request to calculate individual zakah for user: {}", request.getUserId());

        ZakahIndividualRecordResponse response = zakahIndividualRecordService.save(request);

        log.info("Individual zakah calculation completed successfully with id: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Update individual zakah record
    @PutMapping("/{id}")
    public ResponseEntity<ZakahIndividualRecordResponse> updateZakahRecord(
            @PathVariable Long id,
            @Valid @RequestBody ZakahIndividualRecordRequest request) {

        log.info("Received request to update individual zakah record with id: {}", id);

        ZakahIndividualRecordResponse response = zakahIndividualRecordService.update(id, request);

        log.info("Individual zakah record updated successfully with id: {}", id);

        return ResponseEntity.ok(response);
    }
}