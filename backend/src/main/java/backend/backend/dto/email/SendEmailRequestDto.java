package backend.backend.dto.email;

import lombok.Data;

@Data
public class SendEmailRequestDto {
    private String to;
    private String subject;
    private String body;
}
