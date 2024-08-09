package training.sortir.tools;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import training.sortir.dto.*;
import training.sortir.entities.Event;
import training.sortir.entities.Location;
import training.sortir.entities.User;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface EventMapper {


    List<MemberDto> membersToDto(List<User> users);

    List<UserEventResponse> userEventsToDto(List<Event> events);


    SearchedEventDto searchedEventToDto(Event event);



    EventResponse eventToDto(Event event);

    @Mapping(target = "location", ignore = true) // Ignorer la mappage de location pour pouvoir le gérer séparément
    @Mapping(target = "organizerId", ignore = true)
        // Ignorer la mappage de location pour pouvoir le gérer séparément
    Event dtoToEvent(UpdateEventRequest dto);

    default Event updateEventWithLocation(Event event, UpdateEventRequest updatedEvent, Location location) {
        if (event.getLocation() != null && !event.getLocation().getId().equals(updatedEvent.getLocationId())) {
            event.setLocation(location);
        }
        return event;
    }


}
