package com.btgpactual.service;

import com.btgpactual.domain.NotificationPreference;
import com.btgpactual.domain.User;
import org.junit.jupiter.api.Test;

public class NotificationServiceTest {

    private final NotificationService notificationService = new NotificationService();

    @Test
    void testSendNotificationEmail() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setNotificationPreference(NotificationPreference.EMAIL);
        
        notificationService.sendNotification(user, "test msg");
    }

    @Test
    void testSendNotificationSms() {
        User user = new User();
        user.setPhone("1234567890");
        user.setNotificationPreference(NotificationPreference.SMS);
        
        notificationService.sendNotification(user, "test msg");
    }
    
    @Test
    void testSendNotificationNone() {
        User user = new User();
        user.setNotificationPreference(null);
        
        notificationService.sendNotification(user, "test msg");
    }
}
