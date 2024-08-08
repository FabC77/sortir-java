package training.sortir.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import training.sortir.dto.UserDTO;
import training.sortir.entities.User;
import training.sortir.repository.UserRepository;
import training.sortir.service.UserService;
import training.sortir.tools.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public boolean login(UserDTO userDTO) {
        Optional<User> user = userRepository.findById(userDTO.getId());
        if (!user.isPresent()) {
            return false;
        }
        User userReal = user.get();
        if (userReal.getPassword().equals(userDTO.getPassword())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteUser(UUID userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        } else {
            throw new RuntimeException("L'utilisateur avec l'ID " + userId + " n'existe pas.");
        }
    }

    @Override
    public List<UserDTO> getUsers() {

        List<User> users= userRepository.findAll();
        List<UserDTO> dtoList = userMapper.usersToDtos(users);

        return dtoList;
    }

    @Override
    public UserDTO getUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setUsername(user.getUsername());

        return dto;
    }
}
