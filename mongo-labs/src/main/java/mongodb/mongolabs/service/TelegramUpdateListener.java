package mongodb.mongolabs.service;

import com.pengrad.telegrambot.model.Update;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramUpdateListener {

    private final ApplicationEventPublisher eventPublisher;

    public TelegramUpdateListener(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void onUpdates(final List<Update> updates) {
        updates.forEach(this::emitTelegramUpdateReceivedEvent);
    }

    private void emitTelegramUpdateReceivedEvent(final Update update) {
        final TelegramUpdateReceived updateReceivedEvent = new TelegramUpdateReceived(this, update);
        eventPublisher.publishEvent(updateReceivedEvent);
    }
}