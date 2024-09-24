package training.sortir.service;

import org.springframework.stereotype.Service;
import training.sortir.dto.*;

import java.util.List;

@Service
public interface EventService {
    EventResponse register(CreateEventRequest event, String username);

    EventResponse update(UpdateEventRequest event, long id, String username);

    boolean cancel(CancelEventRequest event, long id, String username);

    List<MemberDto> joinEvent(long id, String username);

    List<MemberDto> leaveEvent(long id, String username);

    List<UserEventResponse> getUserEvents(String username);

    EventResponse getEvent(long id, String username);

    List<SearchedEventDto> getCampusEvents(int campusId, String username);

    List<SearchedEventDto> searchEvents(String username, SearchEventRequest req);
}
