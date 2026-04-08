package com.btgpactual.service;

import com.btgpactual.domain.NotificationPreference;
import com.btgpactual.domain.User;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(User user, String message) {
        if (user.getNotificationPreference() == NotificationPreference.EMAIL) {
            sendEmail(user.getEmail(), message);
        } else if (user.getNotificationPreference() == NotificationPreference.SMS) {
            sendSms(user.getPhone(), message);
        }
    }

    private void sendEmail(String email, String message) {
        // Simulando envío de email
        System.out.println("Enviando EMAIL a " + email + ": " + message);
    }

    private void sendSms(String phone, String message) {
        // Simulando envío de SMS
        System.out.println("Enviando SMS a " + phone + ": " + message);
    }
}
