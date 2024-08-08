package training.sortir.tools;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import training.sortir.dto.EventResponse;
import training.sortir.entities.Event;

@Mapper(componentModel = "spring")
@Component
public interface EventMapper {


    EventResponse eventToDto(Event event);
}
