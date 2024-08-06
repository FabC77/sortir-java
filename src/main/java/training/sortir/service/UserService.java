package training.sortir.service;


import org.springframework.stereotype.Service;
import training.sortir.DTOs.UserDTO;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {

    boolean createUser(UserDTO userDTO);
 boolean login(UserDTO userDTO);
    boolean logout(UUID userId);
    boolean deleteUser(UUID userId);

    List<UserDTO> getUsers();

    UserDTO getUser(UUID userId);
}
