package training.sortir.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import training.sortir.config.JwtService;
import training.sortir.dto.CreateEventRequest;
import training.sortir.dto.EventResponse;
import training.sortir.entities.*;
import training.sortir.repository.CampusRepository;
import training.sortir.repository.EventRepository;
import training.sortir.repository.LocationRepository;
import training.sortir.repository.UserRepository;
import training.sortir.service.EventService;
import training.sortir.tools.EventMapper;
import training.sortir.tools.UserMapper;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CampusRepository campusRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;


    public EventResponse register(CreateEventRequest event, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Location location = locationRepository.findById(event.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found with ID: " + event.getLocationId()));

        Campus campus = campusRepository.findById(event.getCampusId())
                .orElseThrow(() -> new EntityNotFoundException("Campus not found with ID: " + event.getCampusId()));

        Event newEvent = Event.builder()
                .name(event.getName())
                .infos(event.getInfos())
                .organizerId(user.getId())
                .status(event.getStatus())
                .picture(event.getPicture())
                .location(location)
                .campus(campus)
                .startDate(event.getStartDate())
                .duration(event.getDuration())
                .deadline(event.getDeadline())
                .members(event.getMembers())
                .build();

        eventRepository.save(newEvent);

        EventResponse response = eventMapper.eventToDto(newEvent);
        response.setOrganizerName(user.getFirstname() + " " + user.getLastname());
        response.setLocationId(location.getId());
        response.setLocationName(location.getName());
        response.setCampusId(campus.getId());
        response.setCampusName(campus.getName());
        return response;
    }
}
