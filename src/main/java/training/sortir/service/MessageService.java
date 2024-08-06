package training.sortir.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import training.sortir.DTOs.MessageDTO;

import java.util.List;
import java.util.UUID;

@Service
public interface MessageService {

    boolean addMessage(MessageDTO message, HttpServletRequest request);
    List<MessageDTO> getMessages();
    List<MessageDTO> deleteMessage(UUID userId, int messageId);

    List<MessageDTO> getMessagesFromUser(UUID userId);
}
