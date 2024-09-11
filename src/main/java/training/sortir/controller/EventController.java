package training.sortir.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import training.sortir.dto.*;
import training.sortir.service.EventService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;


    @PostMapping("/event/create")
    public ResponseEntity<EventResponse> createEvent(@RequestBody CreateEventRequest request, Principal principal) {
        String username = principal.getName();
        EventResponse response = eventService.register(request, username);
        System.out.println("in event create controller");
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
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else {
            //TODO: traiter les cas
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/event/{id}/join")
    public ResponseEntity<?> joinEvent(@PathVariable long id, Principal principal) {
        String username = principal.getName();

        List<MemberDto> members = eventService.joinEvent(id, username);
        return ResponseEntity.status(HttpStatus.OK).body(members);
    }

    @DeleteMapping("/event/{id}/leave")
    public ResponseEntity<?> leaveEvent(@PathVariable long id, Principal principal) {
        String username = principal.getName();

        List<MemberDto> members = eventService.leaveEvent(id, username);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(members);
    }

    @GetMapping("/event/user-events")
    public ResponseEntity<List<UserEventResponse>> getUserEvents(Principal principal) {
        String username = principal.getName();
        List<UserEventResponse> events = eventService.getUserEvents(username);
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable long id, Principal principal) {
        String username = principal.getName();
        EventResponse response = eventService.getEvent(id, username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/event/{campusId}/search")
    public ResponseEntity<List<SearchedEventDto>> getSearchedCampusEvents(@PathVariable int campusId, Principal principal){
        String username = principal.getName();
        List<SearchedEventDto> events = eventService.getCampusEvents(campusId,username);
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    @PostMapping("/event/search")
    public ResponseEntity<List<SearchedEventDto>> searchEvent(Principal principal, @RequestBody SearchEventRequest req){
        String username = principal.getName();
        List<SearchedEventDto> events = eventService.searchEvents(username,req);

        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

}
