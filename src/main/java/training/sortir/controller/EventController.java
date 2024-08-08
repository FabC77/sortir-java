package training.sortir.controller;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import training.sortir.dto.CancelEventRequest;
import training.sortir.dto.CreateEventRequest;
import training.sortir.dto.EventResponse;
import training.sortir.dto.UpdateEventRequest;
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

    @PutMapping("/event/{id}/update")
    public ResponseEntity<EventResponse> updateEvent(@RequestBody UpdateEventRequest request, @PathVariable long id, Principal principal) {
        String username = principal.getName();
        EventResponse response = eventService.update(request, id, username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/event/{id}/cancel")
    public ResponseEntity<?> cancelEvent(@RequestBody CancelEventRequest request, @PathVariable long id, Principal principal) {
        String username = principal.getName();
        if (eventService.cancel(request, id, username)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Event cancelled successfully.");
        } else {
            //TODO: traiter les cas
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to cancel event.");
        }
    }
}
