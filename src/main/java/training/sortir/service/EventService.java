package training.sortir.service;

import training.sortir.dto.*;

import java.util.List;

public interface EventService {
    EventResponse register(CreateEventRequest event, String username);

    EventResponse update(UpdateEventRequest event, long id, String username);

    boolean cancel(CancelEventRequest event, long id, String username);

    List<MemberDto> joinEvent(long id, String username);

    List<MemberDto> leaveEvent(long id, String username);
}
