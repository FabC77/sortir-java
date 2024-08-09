package training.sortir.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import training.sortir.dto.*;
import training.sortir.entities.*;
import training.sortir.repository.CampusRepository;
import training.sortir.repository.EventRepository;
import training.sortir.repository.LocationRepository;
import training.sortir.repository.UserRepository;
import training.sortir.service.EventService;
import training.sortir.tools.EventMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CampusRepository campusRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;


    public EventResponse register(CreateEventRequest dto, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found with ID: " + dto.getLocationId()));

        Campus campus = campusRepository.findById(user.getCampusId())
                .orElseThrow(() -> new EntityNotFoundException("Campus not found with ID: " + user.getCampusId()));

        Calendar cal = Calendar.getInstance();
        cal.setTime(dto.getStartDate());
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date archiveDate = cal.getTime();

        Event newEvent = Event.builder()
                .name(dto.getName())
                .infos(dto.getInfos())
                .organizerId(user.getId())
                .status(dto.getStatus())
                .picture(dto.getPicture())
                .location(location)
                .campus(campus)
                .startDate(dto.getStartDate())
                .duration(dto.getDuration())
                .archiveDate(archiveDate)
                .maxMembers(dto.getMaxMembers())
                .currentMembers(1)
                .members(new ArrayList<>())
                .deadline(dto.getDeadline())
                .lastUpdated(new Date())
                .build();

        newEvent.getMembers().add(user);
        user.getEvents().add(newEvent);
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

        if (!event.getOrganizerId().equals(user.getId()))
            throw new IllegalStateException("User is not authorized to update");

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

        event.setLastUpdated(new Date());

        eventRepository.save(event);
        eventResponse = eventMapper.eventToDto(event);
        eventResponse.setOrganizerName(user.getFirstname() + " " + user.getLastname());
        eventResponse.setLocationId(event.getLocation().getId());
        eventResponse.setLocationName(event.getLocation().getName());
        eventResponse.setCampusId(campus.getId());
        eventResponse.setCampusName(campus.getName());

        return eventResponse;
    }

    @Override
    public boolean cancel(CancelEventRequest dto, long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        Event event = eventRepository.findById(id).orElseThrow();
        if (!event.getOrganizerId().equals(user.getId()))
            throw new IllegalStateException("User is not authorized to update");
        if (new Date().after(event.getStartDate())) throw new IllegalStateException("Active event can't be canceled");
        event.setStatus(EventStatus.CANCELLED);

        event.setReason(dto.getReason());

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date archiveDate = cal.getTime();
        event.setArchiveDate(archiveDate);
        event.setLastUpdated(new Date());
        eventRepository.save(event);
        return true;
    }

    @Override
    public List<MemberDto> joinEvent(long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        Event event = eventRepository.findById(id).orElseThrow();

        if (user.getId().equals(event.getOrganizerId()))
            throw new IllegalStateException("The organizer can't join their own event");
        if (event.getMembers().stream().count() == event.getMaxMembers())
            throw new IllegalStateException("The event capacity is full");
        if (event.getStatus() != EventStatus.OPEN) throw new IllegalStateException("The event is closed");

        if (event.getMembers().contains(user))
            throw new IllegalStateException("The user is already registered in the event");
        List<User> members = event.getMembers();
        members.add(user);
        event.setMembers(members);
        event.setCurrentMembers(event.getCurrentMembers() + 1);
        user.getEvents().add(event);
        eventRepository.save(event);
        List<MemberDto> list = eventMapper.membersToDto(members);
        return list;
    }

    @Override
    public List<MemberDto> leaveEvent(long id, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        Event event = eventRepository.findById(id).orElseThrow();
        if (user.getId().equals(event.getOrganizerId()))
            throw new IllegalStateException("The organizer can't leave his own event");

        event.removeMember(user);
        event.setCurrentMembers(event.getCurrentMembers() - 1);

        user.removeEvent(event);
        eventRepository.save(event);

        List<MemberDto> list = eventMapper.membersToDto(event.getMembers());
        return list;
    }

    @Override
    public List<UserEventResponse> getUserEvents(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        List<Event> events = user.getEvents();
        for (Event event : events) {
            if (checkStatusChange(event)) {
                eventRepository.save(event);
            }


        }
        events.removeIf(event -> event.getStatus() == EventStatus.ARCHIVED);
        List<Long> organizedEvents = events.stream()
                .filter(e -> e.getOrganizerId().equals(user.getId()))
                .map(Event::getId)
                .collect(Collectors.toList());
        List<UserEventResponse> userEvents = eventMapper.userEventsToDto(events);
        for (UserEventResponse e : userEvents) {
            if (organizedEvents.contains(e.getId())) {
                e.setOrganizer(true);
            }
        }
        return userEvents;
    }

    @Override
    public EventResponse getEvent(long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        Event event = eventRepository.findById(id).orElseThrow();
        if (checkStatusChange(event)) {
            eventRepository.save(event);
        }

        if (event.getStatus() == EventStatus.DRAFT && !event.getOrganizerId().equals(user.getId()))
            throw new IllegalStateException("User is not authorized to see the event");
        EventResponse dto = eventMapper.eventToDto(event);
        return dto;
    }

    @Override
    public List<SearchedEventDto> getCampusEvents(int campusId, String username) {
        List<Event> campusEvents = eventRepository.findByCampusId(campusId);

        List<SearchedEventDto> events = new ArrayList<SearchedEventDto>();
        for (Event campEvent : campusEvents) {
            SearchedEventDto e = eventMapper.searchedEventToDto(campEvent);
            User org = userRepository.findById(campEvent.getOrganizerId()).orElseThrow();
            e.setOrganizerName(org.getFirstname() + " " + org.getLastname());
            e.setLocationName(campEvent.getLocation().getName());
            events.add(e);
        }
        return events;
    }

    private boolean checkStatusChange(Event event) {
        Date now = new Date();
        boolean hasChanged = false;
        switch (event.getStatus()) {
            case IN_PROGRESS:
                Instant eventStartInstant = event.getStartDate().toInstant();
                Instant endInstant = eventStartInstant.plus(event.getDuration());
                Date endDate = Date.from(endInstant);
                if (now.after(endDate)) {
                    event.setStatus(EventStatus.FINISHED);
                    hasChanged = true;
                }
                break;
            case DRAFT:
                break;
            case CLOSED:
                if (event.getMembers().stream().count() < event.getMaxMembers() && !(new Date().after(event.getDeadline()))) {
                    event.setStatus(EventStatus.OPEN);
                    event.setLastUpdated(now);
                    hasChanged = true;
                }
                if (now.after(event.getStartDate())) {
                    event.setStatus(EventStatus.IN_PROGRESS);
                    hasChanged = true;
                }
                break;
            case CANCELLED:
                if (new Date().after(event.getArchiveDate())) {
                    event.setStatus(EventStatus.ARCHIVED);
                    hasChanged = true;
                }
                break;
            case FINISHED:
                if (now.after(event.getArchiveDate())) {
                    event.setStatus(EventStatus.ARCHIVED);
                }
                break;
            case OPEN:
                if (event.getMembers().stream().count() == event.getMaxMembers()) {
                    event.setStatus(EventStatus.CLOSED);
                    event.setLastUpdated(now);
                    hasChanged = true;
                }
                break;
            default:
                break;


        }

        return hasChanged;
    }
}
