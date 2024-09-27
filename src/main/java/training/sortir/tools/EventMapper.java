package training.sortir.tools;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import training.sortir.dto.*;
import training.sortir.entities.Event;
import training.sortir.entities.Location;
import training.sortir.entities.User;
import training.sortir.service.FileStoreService;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface EventMapper {



    //@Mapping(target = "profilePicture", expression = "java(fileStoreService.getFullUrl(user.getProfilePicture()))")
    MemberDto userToMemberDto(User user, @Context FileStoreService fileStoreService);

    List<MemberDto> membersToDto(List<User> users, @Context FileStoreService fileStoreService);


    List<UserEventResponse> userEventsToDto(List<Event> events);

    SearchedEventDto searchedEventToDto(Event event);


    @Mapping(target = "duration", ignore = true)
    EventResponse eventToDto(Event event);

    UserEventResponse eventToUserEventResponse(Event event);

    @Mapping(target = "location", ignore = true)
    @Mapping(target = "organizerId", ignore = true)
    @Mapping(target = "picture", ignore = true)
    Event dtoToEvent(UpdateEventRequest dto);



    default Event updateEventWithLocation(Event event, UpdateEventRequest updatedEvent, Location location) {
        if (event.getLocation() != null && !event.getLocation().getId().equals(updatedEvent.getLocationId())) {
            event.setLocation(location);
        }
        return event;
    }


}
