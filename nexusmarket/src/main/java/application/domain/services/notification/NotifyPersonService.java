package application.domain.services.notification;

import application.domain.enums.NotificationChannel;
import application.domain.models.Notification;
import application.domain.models.Person;
import application.domain.ports.out.NotificationPort;

/**
 * Sends a notification to a person (buyer, seller or user) through the NotificationPort.
 */
public class NotifyPersonService {

    private final NotificationPort notificationPort;

    public NotifyPersonService(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    public void execute(Person recipient, String subject, String message) {
        notificationPort.send(new Notification(recipient, NotificationChannel.EMAIL, subject, message));
    }
}
