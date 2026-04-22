package com.vdmcreation.TradeAlert.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, int otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Trade Alert - Your OTP Code");
        message.setText(
                "Hello,\n\n" +
                "Your OTP for Trade Alert login is: " + otp + "\n\n" +
                "This OTP is valid for 5 minutes.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Regards,\nTrade Alert Team"
        );
        mailSender.send(message);
    }
}
