package training.sortir.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import training.sortir.dto.MessageDTO;
import training.sortir.config.JwtService;
import training.sortir.entities.Message;
import training.sortir.repository.MessageRepository;
import training.sortir.repository.UserRepository;
import training.sortir.service.MessageService;
import training.sortir.tools.MessageMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public boolean addMessage(MessageDTO message, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);
        Message newMessage = messageMapper.dtoToMessage(message);
//TODO prévoir exceptions (jwt, setOwner)
        newMessage.setOwner(userRepository.findByUsername(username).get());
        if (messageRepository.save(newMessage) != null) {
            return true;
        }
        return false;
    }

    @Override
    public List<MessageDTO> getMessages() {
        List<Message> messages = messageRepository.findAll();
        List<MessageDTO> dto = messageMapper.messagesToDTO(messages);
        for (MessageDTO message : dto) {
            System.out.println(message.getCreatedAt().toString());
        }
        return dto;
    }

    @Override
    public List<MessageDTO> deleteMessage(UUID userId, int messageId) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (!messageOptional.isPresent()) {
            throw new RuntimeException("Le message avec l'ID " + messageId + " n'appartient pas à l'utilisateur avec l'ID " + userId);
        }
        messageRepository.deleteById(messageId);

        List<Message> remainingMessages = messageRepository.findAll();

        return messageMapper.messagesToDTO(remainingMessages);
    }

    @Override
    public List<MessageDTO> getMessagesFromUser(UUID userId) {
        List<Message> messages = messageRepository.findByOwnerId(userId);
        List<MessageDTO> dtos = messageMapper.messagesToDTO(messages);

        return dtos;
    }
}


