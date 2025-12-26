package ntg.project.ZakahCalculator.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.dto.request.ZakahCompanyRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordResponse;
import ntg.project.ZakahCalculator.exception.ErrorCode;
import ntg.project.ZakahCalculator.service.ZakahCompanyRecordService;
import ntg.project.ZakahCalculator.service.impl.ZakahCompanyRecordServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("Records/Company")
@RequiredArgsConstructor
public class CompanyRecordController {
    private final ZakahCompanyRecordService zakahCompanyRecordService;

    //Get all balance sheet records by user id
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getZakahHistory(@PathVariable Long userId) {
        try {
            var history = zakahCompanyRecordService.findAllByUserId();
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorCode.INTERNAL_EXCEPTION.getDefaultMessage());
        }
    }

    //Get latest balance sheet record by user id
    @GetMapping("/latest/{userId}")
    public ResponseEntity<?> getLatestZakahRecord(@PathVariable Long userId) {
        try {
            var record = zakahCompanyRecordService.findTopByUserIdOrderByBalanceSheetDateDesc(userId);
            if (record != null) {
                return ResponseEntity.ok(record);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ErrorCode.RECORD_NOT_FOUND.getDefaultMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorCode.INTERNAL_EXCEPTION.getDefaultMessage());
        }
    }


    //Calculate zakah
    @PostMapping("/calculate")
    public ResponseEntity<ZakahCompanyRecordResponse> calculateZakah(
            @Valid @RequestBody ZakahCompanyRecordRequest requestDTO) {

        try {
            ZakahCompanyRecordResponse response = zakahCompanyRecordService.calculateZakah(requestDTO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Delete balance sheet record by record id
    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteZakahRecord(@PathVariable Long recordId) {
        try {
            zakahCompanyRecordService.deleteByIdAndUserId(recordId);
            return ResponseEntity.ok(("Zakah record deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorCode.INTERNAL_EXCEPTION.getDefaultMessage());
        }
    }



}
