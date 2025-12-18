package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.entity.ZakahRecord;
import ntg.project.ZakahCalculator.entity.User;

import java.util.List;

public interface ZakahRecordService {

    ZakahRecord save(ZakahRecord record);

    List<ZakahRecord> findByUser(User user);
}
