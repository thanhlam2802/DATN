package backend.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailResultDto {
    private String recipient;
    private String applicantName;
    private String businessName;
    private String decision; 
    private String dashboardLink;
    private String contactEmail;
    private String hotline;
    private String address;
}
