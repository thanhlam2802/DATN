package backend.backend.entity;

import backend.backend.dto.auth.OtpType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_transactions")
@Data
public class OTPTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp_code")
    private String otpCode;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_in_minute")
    private Long expiredInMinute;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OtpType type;
}
