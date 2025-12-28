package ntg.project.ZakahCalculator.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntg.project.ZakahCalculator.dto.request.ZakahCompanyRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordSummaryResponse;
import ntg.project.ZakahCalculator.exception.ErrorCode;
import ntg.project.ZakahCalculator.service.ZakahCompanyRecordService;
import ntg.project.ZakahCalculator.service.impl.ZakahCompanyRecordServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("Records/Company")
@RequiredArgsConstructor
@Slf4j
public class CompanyRecordController {
    private final ZakahCompanyRecordService zakahCompanyRecordService;

    //Get all balance sheet records by user id
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getZakahHistory(@PathVariable Long userId) {
        log.info("Fetching all detailed zakah records for authenticated user");

        List<ZakahCompanyRecordResponse> responses = zakahCompanyRecordService.findAllByUserId();

        return ResponseEntity.ok(responses);
    }

    //Get all summaries of zakah record
    @GetMapping("/all/summaries/{id}")
    public ResponseEntity<List<ZakahCompanyRecordSummaryResponse>> getAllZakahRecordSummaries(@PathVariable Long id) {

        log.info("Fetching all zakah record summaries for authenticated user");

        List<ZakahCompanyRecordSummaryResponse> responses = zakahCompanyRecordService.findAllSummariesByUserId();

        return ResponseEntity.ok(responses);
    }

    //Get latest balance sheet record by user id
    @GetMapping("/latest/{userId}")
    public ResponseEntity<ZakahCompanyRecordResponse> getLatestZakahRecord(@PathVariable Long userId) {

        log.info("Fetching latest zakah record for authenticated user");

        ZakahCompanyRecordResponse response = zakahCompanyRecordService.findLatestByUserId();

        return ResponseEntity.ok(response);
    }

    //Get the latest zakah record summary
    @GetMapping("/latest/summary")
    public ResponseEntity<ZakahCompanyRecordSummaryResponse> getLatestZakahRecordSummary() {

        log.info("Fetching latest zakah record summary for authenticated user");

        ZakahCompanyRecordSummaryResponse response = zakahCompanyRecordService.findLatestSummaryByUserId();

        return ResponseEntity.ok(response);
    }


    //Get detailed zakah record by ID
    @GetMapping("/detailedRecord/{id}")
    public ResponseEntity<ZakahCompanyRecordResponse> getZakahRecordById(@PathVariable Long id) {

        log.info("Fetching detailed zakah record with id: {}", id);

        ZakahCompanyRecordResponse response = zakahCompanyRecordService.findByIdAndUserId(id);

        return ResponseEntity.ok(response);
    }

    //Get summary of zakah record by ID
    @GetMapping("/summary/{id}")
    public ResponseEntity<ZakahCompanyRecordSummaryResponse> getZakahRecordSummaryById(
            @PathVariable Long id) {

        log.info("Fetching zakah record summary with id: {}", id);

        ZakahCompanyRecordSummaryResponse response = zakahCompanyRecordService.findSummaryByIdAndUserId(id);

        return ResponseEntity.ok(response);
    }

    //Calculate zakah
    @PostMapping("/calculate")
    public ResponseEntity<ZakahCompanyRecordResponse> calculateAndSaveZakah(
            @Valid @RequestBody ZakahCompanyRecordRequest request) {

        log.info("Received request to calculate zakah for user: {}", request.getUserId());

        ZakahCompanyRecordResponse response = zakahCompanyRecordService.save(request);

        log.info("Zakah calculation completed successfully with id: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZakahRecord(@PathVariable Long id) {

        log.info("Deleting zakah record with id: {}", id);

        zakahCompanyRecordService.deleteByIdAndUserId(id);

        log.info("Successfully deleted zakah record with id: {}", id);

        return ResponseEntity.noContent().build();
    }



}
