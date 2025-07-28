
package backend.backend.event; 
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class VoucherUsedUpEvent extends ApplicationEvent {
    private final String voucherCode;

    public VoucherUsedUpEvent(Object source, String voucherCode) {
        super(source);
        this.voucherCode = voucherCode;
    }
}
