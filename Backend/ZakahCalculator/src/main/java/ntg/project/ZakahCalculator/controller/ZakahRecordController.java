package ntg.project.ZakahCalculator.controller;


import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.service.ZakahRecordService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Records")
@RequiredArgsConstructor
public class ZakahRecordController {
    private final ZakahRecordService zakahRecordService;
}
