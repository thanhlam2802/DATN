package com.example.Notification_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;

public interface NotificationService {


    @KafkaListener(topics = "send-otp-mail", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    void listenOtpMail(byte[] payload);

    @KafkaListener(topics = "send-payment-success-mail", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    void listenPaymentSuccessMail(byte[] payload);

    @KafkaListener(topics = "send-refund-success-mail", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    void listenRefundSuccessMail(byte[] payload);

    @KafkaListener(topics = "send-booking-mail", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    void listenBookingMail(byte[] payload);
}
