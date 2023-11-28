package mongodb.mongolabs.service;

import com.pengrad.telegrambot.model.Update;
import org.springframework.context.ApplicationEvent;

public class TelegramUpdateReceived extends ApplicationEvent {

    private final Update telegramUpdate;

    public TelegramUpdateReceived(Object source, Update telegramUpdate) {
        super(source);
        this.telegramUpdate = telegramUpdate;
    }

    public Update getTelegramUpdate() {
        return telegramUpdate;
    }
}