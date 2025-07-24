package backend.backend.repository;

import backend.backend.dto.auth.OtpType;
import backend.backend.entity.OTPTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPTransactionRepository extends JpaRepository<OTPTransaction, Integer> {

    Optional<OTPTransaction> findFirstByUserIdAndTypeOrderByCreatedAtDesc(Integer userId, OtpType type);
}
