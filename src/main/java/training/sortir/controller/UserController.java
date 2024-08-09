package training.sortir.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import training.sortir.dto.UpdateUserRequest;
import training.sortir.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/user/update-profile")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request, Principal principal){
        String username = principal.getName();
        if(userService.updateUser(request, username)){
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("success");
        } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur de mise à jour de profil");
    }
}
