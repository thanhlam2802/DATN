package backend.backend.entity;



import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
class UserVoucherId implements Serializable {
    private Integer userId;
    private Integer voucherId;
}
@Data
@Entity
@Table(name = "user_vouchers")
public class UserVoucher {
    @EmbeddedId
    private UserVoucherId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("voucherId")
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Column(name = "claimed_at", nullable = false, updatable = false)
    private LocalDateTime claimedAt = LocalDateTime.now();

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}
