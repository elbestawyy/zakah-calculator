package ntg.project.ZakahCalculator.service.impl;

import ntg.project.ZakahCalculator.entity.ZakahCompanyRecord;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.repository.ZakahCompanyRecordRepository;
import ntg.project.ZakahCalculator.service.ZakahCompanyRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ZakahCompanyRecordServiceImpl
        implements ZakahCompanyRecordService {

    private final ZakahCompanyRecordRepository zakahCompanyRecordRepository;

    @Override
    public ZakahCompanyRecord save(ZakahCompanyRecord record) {
        return zakahCompanyRecordRepository.save(record);
    }

    @Override
    public Optional<ZakahCompanyRecord> findById(Long id) {
        return zakahCompanyRecordRepository.findById(id);
    }

    @Override
    public List<ZakahCompanyRecord> findByUser(User user) {
        return zakahCompanyRecordRepository.findByUser(user);
    }
}
