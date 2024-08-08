package training.sortir.service;

import training.sortir.dto.CancelEventRequest;
import training.sortir.dto.CreateEventRequest;
import training.sortir.dto.EventResponse;
import training.sortir.dto.UpdateEventRequest;

import java.security.Principal;

public interface EventService {
    EventResponse register(CreateEventRequest event, String username);

    EventResponse update(UpdateEventRequest event, long id, String username);

    boolean cancel(CancelEventRequest event, long id, String username);
}
