package com.example.Notification_service.service.impl;

import com.example.Notification_service.dto.MailOtpDto;
import com.example.Notification_service.dto.MailPaymentSuccessDto;
import com.example.Notification_service.dto.MailRefundSuccessDto;
import com.example.Notification_service.dto.MailResultDto;
import com.example.Notification_service.dto.MailBookingDto;
import com.example.Notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.internet.MimeMessage;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String mailFrom;

    // Gửi OTP cho 5 ngân hàng
    public void sendOtpMail(MailOtpDto dto) {
        String template = switch (dto.getBankName().toLowerCase()) {
            case "mb bank" -> "otp-mbbank";
            case "techcombank" -> "otp-techcombank";
            case "vp bank", "vpbank" -> "otp-vpbank";
            case "sacombank" -> "otp-sacombank";
            case "agri bank", "agribank" -> "otp-agribank";
            default -> "otp-mbbank";
        };
        Context context = new Context();
        context.setVariable("recipientName", dto.getRecipientName());
        context.setVariable("otp", dto.getOtp());
        context.setVariable("amount", dto.getAmount());
        context.setVariable("transactionId", dto.getTransactionId());
        context.setVariable("time", dto.getTime());
        context.setVariable("expiredTime", dto.getExpiredTime());
        String htmlContent = templateEngine.process(template, context);
        sendSimpleMail(dto.getRecipient(), "Mã OTP giao dịch từ " + dto.getBankName(), htmlContent);
    }

    // Gửi thông báo thanh toán thành công
    public void sendPaymentSuccessMail(MailPaymentSuccessDto dto) {
        Context context = new Context();
        context.setVariable("recipientName", dto.getRecipientName());
        context.setVariable("bankName", dto.getBankName());
        context.setVariable("amount", dto.getAmount());
        context.setVariable("transactionId", dto.getTransactionId());
        context.setVariable("time", dto.getTime());
        context.setVariable("bankLogo", getBankLogo(dto.getBankName()));
        String htmlContent = templateEngine.process("payment-success", context);
        sendSimpleMail(dto.getRecipient(), "Thanh toán thành công", htmlContent);
    }

    // Gửi thông báo hoàn tiền thành công
    public void sendRefundSuccessMail(MailRefundSuccessDto dto) {
        Context context = new Context();
        context.setVariable("recipientName", dto.getRecipientName());
        context.setVariable("bankName", dto.getBankName());
        context.setVariable("amount", dto.getAmount());
        context.setVariable("refundId", dto.getRefundId());
        context.setVariable("time", dto.getTime());
        context.setVariable("bankLogo", getBankLogo(dto.getBankName()));
        String htmlContent = templateEngine.process("refund-success", context);
        sendSimpleMail(dto.getRecipient(), "Hoàn tiền thành công", htmlContent);
    }

    // Gửi thông báo booking cho backend
    public void sendBookingMail(MailBookingDto dto) {
        Context context = new Context();
        context.setVariable("recipientName", dto.getRecipientName());
        context.setVariable("subject", dto.getSubject());
        context.setVariable("content", dto.getContent());
        String htmlContent = templateEngine.process("booking-success", context);
        sendSimpleMail(dto.getRecipient(), dto.getSubject(), htmlContent);
    }

    // Hàm gửi mail đơn giản
    private void sendSimpleMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Đã gửi mail tới {} với subject {}", to, subject);
        } catch (Exception e) {
            log.error("Lỗi khi gửi mail tới {}: {}", to, e.getMessage(), e);
        }
    }

    // Hàm lấy logo ngân hàng (có thể mở rộng)
    private String getBankLogo(String bankName) {
        if (bankName == null) return null;
        return switch (bankName.trim().toLowerCase()) {
            case "mb bank" -> "https://upload.wikimedia.org/wikipedia/commons/6/6b/Logo_MB_new.png";
            case "techcombank" -> "https://img-cache.coccoc.com/image2?i=1&l=13/327960052";
            case "vp bank", "vpbank" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/VPBank_logo.svg/2560px-VPBank_logo.svg.png";
            case "sacombank" -> "https://upload.wikimedia.org/wikipedia/commons/2/2e/Logo-Sacombank-new.png";
            case "agri bank", "agribank" -> "https://upload.wikimedia.org/wikipedia/vi/thumb/3/3d/Argibank_logo.svg/2560px-Argibank_logo.svg.png";
            default -> null;
        };
    }
    @Override
    @KafkaListener(topics = "send-otp-mail", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenOtpMail(byte[] payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MailOtpDto dto = objectMapper.readValue(payload, MailOtpDto.class);
            log.info("[Kafka] Nhận yêu cầu gửi OTP cho {} ngân hàng {}", dto.getRecipient(), dto.getBankName());
            sendOtpMail(dto);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý OTP mail: {}", e.getMessage(), e);
        }
    }
    @Override
    @KafkaListener(topics = "send-payment-success-mail", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenPaymentSuccessMail(byte[] payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MailPaymentSuccessDto dto = objectMapper.readValue(payload, MailPaymentSuccessDto.class);
            log.info("[Kafka] Nhận yêu cầu gửi mail thanh toán thành công cho {} ngân hàng {}", dto.getRecipient(), dto.getBankName());
            sendPaymentSuccessMail(dto);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý payment success mail: {}", e.getMessage(), e);
        }
    }
    @Override
    @KafkaListener(topics = "send-refund-success-mail", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenRefundSuccessMail(byte[] payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MailRefundSuccessDto dto = objectMapper.readValue(payload, MailRefundSuccessDto.class);
            log.info("[Kafka] Nhận yêu cầu gửi mail hoàn tiền thành công cho {} ngân hàng {}", dto.getRecipient(), dto.getBankName());
            sendRefundSuccessMail(dto);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý refund success mail: {}", e.getMessage(), e);
        }
    }
    @Override
    @KafkaListener(topics = "send-booking-mail", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenBookingMail(byte[] payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MailBookingDto dto = objectMapper.readValue(payload, MailBookingDto.class);
            log.info("[Kafka] Nhận yêu cầu gửi mail booking cho {} với subject {}", dto.getRecipient(), dto.getSubject());
            sendBookingMail(dto);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý booking mail: {}", e.getMessage(), e);
        }
    }
    @Override
    @KafkaListener(topics = "send-result-mail", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenResultMail(byte[] payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MailResultDto dto = objectMapper.readValue(payload, MailResultDto.class);
            log.info("[Kafka] Nhận yêu cầu gửi mail kết quả cho {}", dto.getRecipient());
            sendResultMail(dto);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý result mail: {}", e.getMessage(), e);
        }
    }

    public void sendResultMail(MailResultDto dto) {
        Context context = new Context();
        context.setVariable("applicantName", dto.getApplicantName());
        context.setVariable("businessName", dto.getBusinessName());
        context.setVariable("decision", dto.getDecision());
        context.setVariable("dashboardLink", dto.getDashboardLink());
        context.setVariable("contactEmail", dto.getContactEmail());
        context.setVariable("hotline", dto.getHotline());
        context.setVariable("address", dto.getAddress());
        String subject = "Kết quả đăng ký nhà cung cấp";
        String htmlContent = templateEngine.process("result-mail", context);
        sendSimpleMail(dto.getRecipient(), subject, htmlContent);
    }



}

