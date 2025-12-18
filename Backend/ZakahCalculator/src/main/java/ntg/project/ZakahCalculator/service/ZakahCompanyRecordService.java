package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.entity.ZakahCompanyRecord;
import ntg.project.ZakahCalculator.entity.User;

import java.util.List;
import java.util.Optional;

public interface ZakahCompanyRecordService {

    ZakahCompanyRecord save(ZakahCompanyRecord record);

    Optional<ZakahCompanyRecord> findById(Long id);

    List<ZakahCompanyRecord> findByUser(User user);
}
