package training.sortir.tools;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import training.sortir.dto.MessageDTO;
import training.sortir.entities.Message;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MessageMapper {
MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);
    @Mapping(source="content",target="content")
    @Mapping(source="id",target="id")
    @Mapping(source="owner",target="owner")
    Message dtoToMessage(MessageDTO dto);

    @Mapping(source = "content", target = "content")
    @Mapping(source = "id", target = "id")
    //@Mapping(source = "owner", target = "owner")
    //@Mapping(source = "createdAt", target = "createdAt")// @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    MessageDTO messageToDTO(Message message);

    List<MessageDTO> messagesToDTO(List<Message> messages);
}
