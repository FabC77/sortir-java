package training.sortir.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import training.sortir.DTOs.MessageDTO;
import training.sortir.DTOs.UserDTO;
import training.sortir.service.MessageService;
import training.sortir.service.UserService;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    /* TEST */

    @GetMapping("/hello")
    public String gethello() {
        System.out.println("HELLO IN");
        return "hello";
    }

    /* USERS CRUD */

    @PostMapping("/users/create")
    public ResponseEntity<?> createUser(@RequestBody UserDTO user) {
        System.out.println("REST Create User START");
        boolean isCreated = userService.createUser(user);
        System.out.println("User created: " + isCreated);
        if (isCreated) {
            return ResponseEntity.ok().body("yes");
        } else {
            return ResponseEntity.badRequest().body("no");
        }
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {

        if (userService.login(user)) {
            return ResponseEntity.ok().body(messageService.getMessages());
        } else {
            return ResponseEntity.badRequest().body("échec");
        }
    }

    @DeleteMapping("/users/{userId}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        if (userService.deleteUser(userId)) {
            userService.logout(userId);
            return ResponseEntity.ok().body("Compte supprimé avec succès");
        } else {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression du compte");
        }
    }

    @PostMapping("/users/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return ResponseEntity.ok().body("Vous êtes déconnecté");
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
    public UserDTO getUser(@PathVariable UUID userId){
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
    public List<MessageDTO> getMessagesFromUser(@PathVariable UUID userId)
    {
        return messageService.getMessagesFromUser(userId);
    }

    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        return userService.getUsers();
    }

}
