package training.sortir.service;


import org.springframework.stereotype.Service;
import training.sortir.dto.UserDTO;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {

 boolean login(UserDTO userDTO);
    boolean deleteUser(UUID userId);

    List<UserDTO> getUsers();

    UserDTO getUser(UUID userId);
}
