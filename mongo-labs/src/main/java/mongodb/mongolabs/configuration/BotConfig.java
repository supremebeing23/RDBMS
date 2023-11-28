package mongodb.mongolabs.configuration;



import com.pengrad.telegrambot.TelegramBot;
import mongodb.mongolabs.service.TelegramUpdateListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL;
@PropertySource("application.properties")

@Configuration
public class BotConfig {
    private final TelegramUpdateListener telegramUpdateListener;

    public BotConfig(TelegramUpdateListener telegramUpdateListener) {
        this.telegramUpdateListener = telegramUpdateListener;
    }

    @Value("${bot.name}")
    String botName;
    @Value("${bot.token}")
    String token;


    @Bean
    public TelegramBot telegramBot(@Value("${bot.token}") final String botToken) {
        final TelegramBot telegramBot = new TelegramBot(botToken);
        telegramBot.setUpdatesListener(updates -> {
            telegramUpdateListener.onUpdates(updates);
            return CONFIRMED_UPDATES_ALL;
        });
        return telegramBot;
    }
}