package application.adapters.notification;

import application.domain.models.Notification;
import application.domain.ports.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Notification adapter that delivers nothing (extension point for e-mail / SMS / push). It only logs
 * the channel and subject; message bodies and recipient data are never written to the log.
 */
@Component
public class NoOpNotificationAdapter implements NotificationPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpNotificationAdapter.class);

    @Override
    public void send(Notification notification) {
        if (notification != null) {
            LOGGER.debug("Notification skipped (no-op adapter): channel={}, subject={}",
                    notification.getChannel(), notification.getSubject());
        }
    }
}
