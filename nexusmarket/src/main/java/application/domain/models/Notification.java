package application.domain.models;

import application.domain.enums.NotificationChannel;

/**
 * Message addressed to a user through an external channel (sent through the NotificationPort).
 */
public class Notification {

    private Person recipient;
    private NotificationChannel channel = NotificationChannel.EMAIL;
    private String subject;
    private String message;

    public Notification() {
    }

    public Notification(Person recipient, NotificationChannel channel, String subject, String message) {
        this.recipient = recipient;
        this.channel = channel;
        this.subject = subject;
        this.message = message;
    }

    public Person getRecipient() {
        return recipient;
    }

    public void setRecipient(Person recipient) {
        this.recipient = recipient;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
