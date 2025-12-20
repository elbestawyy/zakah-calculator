package ntg.project.ZakahCalculator.controller;


import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.service.ZakahCompanyRecordService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Records/Company")
@RequiredArgsConstructor
public class CompanyRecordController {
    private final ZakahCompanyRecordService zakahCompanyRecordService;
}
