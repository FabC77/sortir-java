package training.sortir.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MainController {


    private final MessageService messageService;
    private final UserService userService;
    private final MainService mainService;

    /* USERS CRUD */


    @DeleteMapping("/users/{userId}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        if (userService.deleteUser(userId)) {
            return ResponseEntity.ok().body("Compte supprimé avec succès");
        } else {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression du compte");
        }
    }


    /* MESSAGE CRUD */

    @PostMapping("/messages/new")
    public ResponseEntity<?> addMessage(@RequestBody MessageDTO message, HttpServletRequest request) {

        if (messageService.addMessage(message, request)) {
            return ResponseEntity.ok().body("good");
        }
        return ResponseEntity.badRequest().body("error");
    }

    @GetMapping("/users/{userId}")
    public UserDTO getUser(@PathVariable UUID userId) {
        return userService.getUser(userId);
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

    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/campuses")
    public ResponseEntity<List<CampusDTO>> getCampuses() {
        List<CampusDTO> response = mainService.getCampuses();
        return ResponseEntity.ok().body(response);
    }
}
