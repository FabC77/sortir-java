package training.sortir.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import training.sortir.dto.CreateEventRequest;
import training.sortir.dto.EventResponse;
import training.sortir.service.EventService;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;



    @PostMapping("/event/create")
    public ResponseEntity<EventResponse> createEvent(@RequestBody CreateEventRequest request, Principal principal) {
        String username = principal.getName();
        EventResponse response = eventService.register(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
