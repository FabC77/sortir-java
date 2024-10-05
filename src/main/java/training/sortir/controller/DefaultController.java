package training.sortir.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import training.sortir.dto.CampusDTO;
import training.sortir.dto.MessageDTO;
import training.sortir.dto.UserDTO;
import training.sortir.service.MainService;
import training.sortir.service.MessageService;
import training.sortir.service.UserService;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DefaultController {


    private final MessageService messageService;
    private final UserService userService;
    private final MainService mainService;
    private static final Logger logger = LoggerFactory.getLogger(DefaultController.class);


    /* DIVERS */

    @GetMapping("/campuses")
    public ResponseEntity<List<CampusDTO>> getCampuses() {
        List<CampusDTO> response = mainService.getCampuses();
        return ResponseEntity.ok().body(response);
    }

    /* MESSAGE CRUD - Désactivé */

    @PostMapping("/messages/new")
    public ResponseEntity<?> addMessage(@RequestBody MessageDTO message, HttpServletRequest request) {

        if (messageService.addMessage(message, request)) {
            return ResponseEntity.ok().body("good");
        }
        return ResponseEntity.badRequest().body("error");
    }


    @DeleteMapping("/messages/{messageId}/delete")
    public ResponseEntity<?> deleteMessage(@PathVariable int messageId) {
        return ResponseEntity.ok().body(deleteMessage(messageId));
    }

    @GetMapping("/messages")
    public List<MessageDTO> getMessages() {
        return messageService.getMessages();
    }

    @GetMapping("/messages/{userId}")
    public List<MessageDTO> getMessagesFromUser(@PathVariable UUID userId) {
        return messageService.getMessagesFromUser(userId);
    }


}
