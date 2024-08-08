package training.sortir.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import training.sortir.config.JwtService;
import training.sortir.dto.CreateEventRequest;
import training.sortir.dto.EventResponse;
import training.sortir.dto.UpdateEventRequest;
import training.sortir.entities.*;
import training.sortir.repository.CampusRepository;
import training.sortir.repository.EventRepository;
import training.sortir.repository.LocationRepository;
import training.sortir.repository.UserRepository;
import training.sortir.service.EventService;
import training.sortir.tools.EventMapper;
import training.sortir.tools.UserMapper;

import java.nio.file.AccessDeniedException;
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

        Campus campus = campusRepository.findById(user.getCampusId())
                .orElseThrow(() -> new EntityNotFoundException("Campus not found with ID: " + user.getCampusId()));

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

    @Override
    public EventResponse update(UpdateEventRequest dto, long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        Event event = eventRepository.findById(id).orElseThrow();

        Campus campus = campusRepository.findById(user.getCampusId())
                .orElseThrow(() -> new EntityNotFoundException("Campus not found with ID: " + user.getCampusId()));

        System.out.println("TEST orga id avant bloc" + event.getOrganizerId() + "   " + user.getId());
        EventResponse eventResponse;

        if (dto.getName() != null) event.setName(dto.getName());
        if (dto.getInfos() != null) event.setInfos(dto.getInfos());
        if (dto.getStatus() != null) event.setStatus(dto.getStatus());
        if (dto.getReason() != null) event.setReason(dto.getReason());
        if (dto.getPicture() != null) event.setPicture(dto.getPicture());
        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new EntityNotFoundException("Location not found with ID: " + dto.getLocationId()));
            event.setLocation(location);
        }
        if (dto.getLocationName() != null) {
            Location location = event.getLocation();
            if (location != null) {
                location.setName(dto.getLocationName());
                locationRepository.save(location);
            }
        }
        if (dto.getStartDate() != null) event.setStartDate(dto.getStartDate());
        if (dto.getDuration() != null) event.setDuration(dto.getDuration());
        if (dto.getDeadline() != null) event.setDeadline(dto.getDeadline());
        if (dto.getMaxMembers() != 0) event.setMaxMembers(dto.getMaxMembers());


        eventRepository.save(event);
        eventResponse = eventMapper.eventToDto(event);
        eventResponse.setOrganizerName(user.getFirstname() + " " + user.getLastname());
        eventResponse.setLocationId(event.getLocation().getId());
        eventResponse.setLocationName(event.getLocation().getName());
        eventResponse.setCampusId(campus.getId());
        eventResponse.setCampusName(campus.getName());

        return eventResponse;
    }
}
