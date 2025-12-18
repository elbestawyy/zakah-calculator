package ntg.project.ZakahCalculator.repository;

import ntg.project.ZakahCalculator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

/*-------------------------Soft Delete---------------------------------*/
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isDeleted = true, u.deletionDate = :deletionDate WHERE u.id = :id")
    void softDelete(@Param("id") Long id, @Param("deletionDate") LocalDateTime deletionDate);


/*--------------------------Restore------------------------------------*/
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isDeleted = false, u.deletionDate = null WHERE u.id = :id")
    void restore(@Param("id") Long id);
    }
