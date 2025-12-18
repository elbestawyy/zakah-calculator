package ntg.project.ZakahCalculator.service.impl;

import ntg.project.ZakahCalculator.entity.ZakahRecord;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.repository.ZakahRecordRepository;
import ntg.project.ZakahCalculator.service.ZakahRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZakahRecordServiceImpl implements ZakahRecordService {

    private final ZakahRecordRepository zakahRecordRepository;

    @Override
    public ZakahRecord save(ZakahRecord record) {
        return zakahRecordRepository.save(record);
    }

    @Override
    public List<ZakahRecord> findByUser(User user) {
        return zakahRecordRepository.findByUser(user);
    }
}
