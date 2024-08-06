package training.sortir.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import training.sortir.DTOs.UserDTO;
import training.sortir.entities.User;
import training.sortir.repository.UserRepository;
import training.sortir.service.UserService;
import training.sortir.tools.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @Override
    public boolean createUser(UserDTO userDTO) {
        User newUser = userMapper.dtoToUser(userDTO);

        if (!userRepository.existsById(newUser.getId())) {
            userRepository.save(newUser);
            return true;
        } else throw new RuntimeException("bug repository save user");

    }

    @Override
    public boolean login(UserDTO userDTO) {
        Optional<User> user = userRepository.findById(userDTO.getId());
        if (!user.isPresent()) {
            return false;
        }
        User userReal = user.get();
        if (userReal.getPassword().equals(userDTO.getPassword())) {
            return true; // Le mot de passe correspond
        } else {
            return false; // Le mot de passe ne correspond pas
        }
    }

    @Override
    public boolean logout(UUID userId) {
        // à faire
        return true;
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
