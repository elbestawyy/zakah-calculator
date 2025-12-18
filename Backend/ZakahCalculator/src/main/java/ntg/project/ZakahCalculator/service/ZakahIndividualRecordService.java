package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.entity.ZakahIndividualRecord;
import ntg.project.ZakahCalculator.entity.User;

import java.util.List;
import java.util.Optional;

public interface ZakahIndividualRecordService {

    ZakahIndividualRecord save(ZakahIndividualRecord record);

    Optional<ZakahIndividualRecord> findById(Long id);

    List<ZakahIndividualRecord> findByUser(User user);
}
