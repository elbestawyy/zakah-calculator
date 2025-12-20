package ntg.project.ZakahCalculator.controller;


import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.service.ZakahIndividualRecordService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Record/Individual")
@RequiredArgsConstructor
public class IndividualRecordController {
    private final ZakahIndividualRecordService zakahIndividualRecordService;
}
