package com.example.Notification_service.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
