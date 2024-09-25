package training.sortir.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import training.sortir.dto.UpdateUserRequest;
import training.sortir.dto.UserDTO;
import training.sortir.entities.Campus;
import training.sortir.entities.User;
import training.sortir.repository.CampusRepository;
import training.sortir.repository.UserRepository;
import training.sortir.service.FileStoreService;
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
    private final CampusRepository campusRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStoreService fileStoreService;

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
            User user = userRepository.findById(userId).get();
            fileStoreService.deleteProfilePicture(user);
            userRepository.deleteById(userId);

            return true;
        } else {
            throw new RuntimeException("L'utilisateur avec l'ID " + userId + " n'existe pas.");
        }
    }

    @Override
    public List<UserDTO> getUsers() {

        List<User> users = userRepository.findAll();
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

    @Transactional
    @Override
    public boolean updateUser(UpdateUserRequest dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (dto.getCurrentPassword() != null) {
            System.out.println(dto.getCurrentPassword() + " CURRENT PASSWORD");
            if (!passwordEncoder.matches(dto.getCurrentPassword().trim(), user.getPassword()))
                throw new IllegalArgumentException("Current password is incorrect");
            if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
                throw new IllegalStateException("the passwords don't match");
            user.setPassword(passwordEncoder.encode(dto.getConfirmPassword()));

        }
        if (dto.getFirstname() != null && !dto.getFirstname().isEmpty()) {
            user.setFirstname(dto.getFirstname());
        }
        if (dto.getLastname() != null && !dto.getLastname().isEmpty()) {
            user.setLastname(dto.getLastname());
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(dto.getEmail()))
                throw new IllegalStateException("The email already exists");
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getCampusId() > 0) {
            Campus campus = campusRepository.findById(dto.getCampusId())
                    .orElseThrow(() -> new EntityNotFoundException("Campus not found"));
            user.setCampusId(campus.getId());
        }
        if (dto.getProfilePicture() != null) {
          fileStoreService.confirmProfilePicture(dto.getProfilePicture(), user);
        }
        userRepository.save(user);

        return true;
    }

}
