package training.sortir.tools;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import training.sortir.DTOs.UserDTO;
import training.sortir.entities.User;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {
UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

@Mapping(source="username",target="username")
@Mapping(source="password",target="password")
@Mapping(source="firstname",target="firstname")
@Mapping(source="lastname",target="lastname")
@Mapping(source="id",target="id")
User dtoToUser(UserDTO dto);

UserDTO userToDto(User user);

List<UserDTO> usersToDtos(List<User> users);
}
