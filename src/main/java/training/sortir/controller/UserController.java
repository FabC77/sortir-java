package training.sortir.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import training.sortir.dto.UpdateUserRequest;
import training.sortir.dto.UserDTO;
import training.sortir.service.MainService;
import training.sortir.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MainService mainService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/{userId}")
    public UserDTO getUser(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }


    @PutMapping("/users/update-profile")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request, Principal principal){
        String username = principal.getName();
        if(userService.updateUser(request, username)){
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("success");
        } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur de mise à jour de profil");
    }

    @DeleteMapping("/users/{userId}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        if (userService.deleteUser(userId)) {
            return ResponseEntity.ok().body("Compte supprimé avec succès");
        } else {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression du compte");
        }
    }
}
