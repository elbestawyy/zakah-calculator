package ntg.project.ZakahCalculator.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DeletedUserCleanupService {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupDeletedUsers() {

        log.info("Deleting deleted users...");

        List<User> deletedUser = userRepository.findAllDeletedUsers(LocalDateTime.now().minusDays(30));

        if (!deletedUser.isEmpty()) {
            deletedUser.forEach(user -> log.info("- ID: {}, Username: {}, Email: {}",
                    user.getId(), user.getName(), user.getEmail()));

            log.info("Number of users to Delete : {}", deletedUser.size());

            userRepository.deleteAll(deletedUser);

            log.info("✅ Successfully deleted {} users from database", deletedUser.size());



        }
    }


}
