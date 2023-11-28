package mongodb.mongolabs.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import mongodb.mongolabs.persistence.entity.Student;
import mongodb.mongolabs.persistence.repository.StudentRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
public class TelegramUpdateReceivedHandler implements ApplicationListener<TelegramUpdateReceived> {

    private final StudentRepository studentRepository;
    private final TelegramService telegramService;

    public TelegramUpdateReceivedHandler(StudentRepository studentRepository,
                                         TelegramService telegramService) {
        this.studentRepository = studentRepository;
        this.telegramService = telegramService;
    }

    @Override
    public void onApplicationEvent(TelegramUpdateReceived event) {
        final Update update = event.getTelegramUpdate();
        final long chatId = getChatId(update);

        final String message = getMessage();
        telegramService.sendMessage(chatId, message);
    }

    private String getMessage() {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .collect(Collectors.joining("\n"));
    }

    private Long getChatId(final Update update) {
        return ofNullable(update)
                .map(Update::message)
                .map(Message::chat)
                .map(Chat::id)
                .orElse(null);
    }
}