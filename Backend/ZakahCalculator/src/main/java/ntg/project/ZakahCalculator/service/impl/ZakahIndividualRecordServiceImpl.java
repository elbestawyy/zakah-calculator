package ntg.project.ZakahCalculator.service.impl;

import ntg.project.ZakahCalculator.entity.ZakahIndividualRecord;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.repository.ZakahIndividualRecordRepository;
import ntg.project.ZakahCalculator.service.ZakahIndividualRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ZakahIndividualRecordServiceImpl
        implements ZakahIndividualRecordService {

    private final ZakahIndividualRecordRepository zakahIndividualRecordRepository;

    @Override
    public ZakahIndividualRecord save(ZakahIndividualRecord record) {
        return zakahIndividualRecordRepository.save(record);
    }

    @Override
    public Optional<ZakahIndividualRecord> findById(Long id) {
        return zakahIndividualRecordRepository.findById(id);
    }

    @Override
    public List<ZakahIndividualRecord> findByUser(User user) {
        return zakahIndividualRecordRepository.findByUser(user);
    }
}
