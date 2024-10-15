package training.sortir.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import training.sortir.dto.*;
import training.sortir.entities.*;
import training.sortir.repository.*;
import training.sortir.service.EventService;
import training.sortir.service.FileStoreService;
import training.sortir.tools.EventMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    @Value("${aws.s3.baseurl}")
    private String S3_URL;

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CampusRepository campusRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final CityRepository cityRepository;
    private final FileStoreService fileStoreService;
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Transactional
    public EventResponse createEvent(CreateEventRequest dto, String username) {
        logger.info("Starting event registration for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        logger.info("User found: {}", user.getUsername());

        City city = cityRepository.findByNameAndZipCode(dto.getCityName(), dto.getZipCode())
                .orElseGet(() -> {
                    logger.info("City not found, creating a new city: {} {}", dto.getCityName(), dto.getZipCode());
                    City newCity = City.builder()
                            .name(dto.getCityName())
                            .zipCode(dto.getZipCode())
                            .build();
                    return cityRepository.save(newCity);
                });
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseGet(() -> {
                    logger.info("Location not found, creating a new location with ID: {}", dto.getLocationId());

                    Location newLocation = Location.builder()
                            .id(dto.getLocationId())
                            .name(dto.getLocationName() != null ? dto.getLocationName() : dto.getLocationNotNamed())
                            .latitude(dto.getLatitude())
                            .longitude(dto.getLongitude())
                            .address(dto.getAddress())
                            .city(city)
                            .build();
                    return locationRepository.save(newLocation);
                });
        logger.info("Location resolved with ID: {}", location.getId());

        if (city.getLocations() != null) {
            city.getLocations().add(location);
        } else {
            city.setLocations(new ArrayList<>(Collections.singletonList(location)));
        }
        cityRepository.save(city);

        Campus campus = campusRepository.findById(user.getCampusId())
                .orElseThrow(() -> new EntityNotFoundException("Campus not found with ID: " + user.getCampusId()));
        logger.info("Campus resolved: {}", campus.getName());

        Calendar cal = Calendar.getInstance();
        cal.setTime(dto.getStartDate());
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date archiveDate = cal.getTime();

        Duration newDuration = Duration.ofHours
                        (dto.getDuration().get("hours") * 60 * 60)
                .plusMinutes(dto.getDuration().get("minutes") * 60);

        Event newEvent = Event.builder()
                .name(dto.getName())
                .infos(dto.getInfos())
                .organizerId(user.getId())
                .status(dto.isDraft() ? EventStatus.DRAFT : EventStatus.OPEN)
                .location(location)
                .campus(campus)
                .startDate(dto.getStartDate())
                .duration(newDuration)
                .archiveDate(archiveDate)
                .maxMembers(dto.getMaxMembers())
                .currentMembers(1)
                .members(new ArrayList<>())
                .deadline(dto.getDeadline())
                .lastUpdated(new Date())
                .build();

        if (dto.getPicture() != null) {
            logger.info("Event picture provided, confirming the picture");
            fileStoreService.confirmEventPicture(dto.getPicture(), newEvent);
        }

        newEvent.getMembers().add(user);
        user.getEvents().add(newEvent);

        Event savedEvent = eventRepository.save(newEvent);
        logger.info("Event created successfully with ID: {}", savedEvent.getId());
        EventResponse response = new EventResponse();
        response.setId(savedEvent.getId());
        return response;
    }

    @Override
    @Transactional
    public EventResponse update(UpdateEventRequest dto, long id, String username) {
        logger.info("Updating event ID: {} for user: {}", id, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        Event event = eventRepository.findById(id).orElseThrow();
        logger.info("Event found for update with ID: {}", id);

        Campus campus = campusRepository.findById(user.getCampusId())
                .orElseThrow(() -> new EntityNotFoundException("Campus not found with ID: " + user.getCampusId()));

        if (!event.getOrganizerId().equals(user.getId()))
            throw new IllegalStateException("User is not authorized to update");

        EventResponse eventResponse;

        if (dto.getName() != null) event.setName(dto.getName());
        if (dto.getInfos() != null) event.setInfos(dto.getInfos());
        if (dto.getStatus() != null) event.setStatus(dto.getStatus());
        if (dto.getReason() != null) event.setReason(dto.getReason());
        if (dto.getPicture() != null) {
            fileStoreService.confirmEventPicture(dto.getPicture(), event);
        }
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
        logger.info("Event with ID: {} updated successfully", id);
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
        logger.info("Cancelling event with ID: {} by user: {}", id, username);

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
        logger.info("Event with ID: {} has been cancelled", id);
        return true;
    }

    @Override
    @Transactional
    public List<MemberDto> joinEvent(long id, String username) {
        logger.info("User {} attempting to join event with ID: {}", username, id);
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
        userRepository.save(user);
        logger.info("User {} successfully joined event with ID: {}", username, id);
        return eventMapper.membersToDto(members, fileStoreService);
    }

    @Override
    @Transactional
    public List<MemberDto> leaveEvent(long id, String username) {
        logger.info("User {} attempting to leave event with ID: {}", username, id);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        Event event = eventRepository.findById(id).orElseThrow();
        if (user.getId().equals(event.getOrganizerId()))
            throw new IllegalStateException("The organizer can't leave his own event");
        if (!event.getMembers().contains(user)) {
            throw new IllegalStateException("User is not a member of this event");
        }

        event.removeMember(user);
        if (event.getCurrentMembers() > 0) {
            event.setCurrentMembers(event.getCurrentMembers() - 1);
        }
        user.removeEvent(event);
        eventRepository.save(event);
        userRepository.save(user);
        logger.info("User {} successfully left event with ID: {}", username, id);
        return eventMapper.membersToDto(event.getMembers(), fileStoreService);
    }

    @Override
    public List<UserEventResponse> getUserEvents(String username) {
        logger.info("Fetching events for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        List<Event> events = user.getEvents();
        List<UserEventResponse> userEvents = new ArrayList<>();
        for (Event event : events) {
            System.out.println("DATE DE L'EVENT n°" + event.getId() + " - heure : " + event.getDeadline().toString());
            UserEventResponse response = new UserEventResponse();
            if (checkStatusChange(event)) {
                eventRepository.save(event);
            }
            response = eventMapper.eventToUserEventResponse(event);
            if (event.getPicture() != null) {
                response.setPicture(event.getPicture());
            }
            if (event.getOrganizerId().equals(user.getId())) {
                response.setOrganizerName("Vous");
                response.setOrganizer(true);
            } else {
                User owner = userRepository.findById(event.getOrganizerId()).orElseThrow();
                response.setOrganizerName(owner.getFirstname() + " " + owner.getLastname());
            }
            response.setLocationName(event.getLocation().getName());
            userEvents.add(response);
        }
        userEvents.removeIf(event -> event.getStatus() == EventStatus.ARCHIVED);
        logger.info("Returning {} events for user: {}", userEvents.size(), username);
        return userEvents;
    }

    @Override
    public EventResponse getEvent(long id, String username) {
        logger.info("Fetching event with ID: {} for user: {}", id, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        Event event = eventRepository.findById(id).orElseThrow();
        if (checkStatusChange(event)) {
            eventRepository.save(event);
        }

        if (event.getStatus() == EventStatus.DRAFT && !event.getOrganizerId().equals(user.getId()))
            throw new IllegalStateException("User is not authorized to see the event");
        EventResponse dto = eventMapper.eventToDto(event);
        dto.setLocationId(event.getLocation().getId());
        dto.setLocationName(event.getLocation().getName());
        dto.setCampusName(event.getCampus().getName());
        dto.setCampusId(event.getCampus().getId());
        dto.setAddress(event.getLocation().getAddress());
        if (event.getPicture() != null) {
            dto.setPicture(event.getPicture());
        }
        dto.setMembers(eventMapper.membersToDto(event.getMembers(), fileStoreService));

        if (event.getOrganizerId().equals(user.getId())) {
            dto.setCreator(true);
        }
        if (event.getMembers().contains(user)) {
            dto.setEventMember(true);
        }
        dto.setDuration(event.getDuration().toHours() + "h" + (event.getDuration().toMinutes() % 60));
        User organizer = userRepository.findById(event.getOrganizerId()).orElseThrow();
        dto.setOrganizerName(organizer.getFirstname() + " " + organizer.getLastname());
        logger.info("Returning event details for ID: {}", id);
        return dto;
    }

    @Override
    public List<SearchedEventDto> getCampusEvents(int campusId, String username) {
        logger.info("Fetching campus events for campus ID: {} and user: {}", campusId, username);
        List<Event> campusEvents = eventRepository.findByCampusId(campusId);

        List<SearchedEventDto> events = new ArrayList<SearchedEventDto>();
        for (Event campEvent : campusEvents) {
            SearchedEventDto e = eventMapper.searchedEventToDto(campEvent);
            User org = userRepository.findById(campEvent.getOrganizerId()).orElseThrow();
            e.setOrganizerName(org.getFirstname() + " " + org.getLastname());
            e.setLocationName(campEvent.getLocation().getName());
            if (campEvent.getPicture() != null) {
                e.setPicture(campEvent.getPicture());
            }
            events.add(e);
        }
        logger.info("Returning {} events for campus ID: {}", events.size(), campusId);
        return events;
    }

    @Override
    public List<SearchedEventDto> searchEvents(String username, SearchEventRequest req) {
        logger.info("Searching events for user: {} with keyword: {}", username, req.getKeyword());
        String keyword = req.getKeyword().trim();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        List<SearchedEventDto> eventsDto = new ArrayList<>();
        List<Event> events = new ArrayList<>();

        if (req.getStartDate() != null && req.getEndDate() != null) {
            events = eventRepository.findByStartDateBetweenAndCampusIdAndNameContainingIgnoreCase(req.getStartDate(), req.getEndDate(), req.getCampusId(), keyword);
        } else if (req.getEndDate() != null) {
            events = eventRepository.findByStartDateBeforeAndCampusIdAndNameContainingIgnoreCase(req.getEndDate(), req.getCampusId(), keyword);

        } else if (req.getStartDate() != null) {
            events = eventRepository.findByStartDateAfterAndCampusIdAndNameContainingIgnoreCase(req.getStartDate(), req.getCampusId(), keyword);

        } else {
            events = eventRepository.findByCampusIdAndNameContainingIgnoreCase(req.getCampusId(), keyword);
        }

        List<Event> filteredEvents = events.stream().filter(event -> event.getStatus() != EventStatus.ARCHIVED && event.getStatus() != EventStatus.CANCELLED)
                .toList();
        for (Event event : filteredEvents) {
            SearchedEventDto s = new SearchedEventDto();
            s = eventMapper.searchedEventToDto(event);
            User org = userRepository.findById(event.getOrganizerId()).orElseThrow();
            if (event.getPicture() != null) {
                s.setPicture(event.getPicture());
            }
            s.setOrganizerName(org.getFirstname() + " " + org.getLastname());
            s.setLocationName(event.getLocation().getName());
            s.setCampusId(event.getCampus().getId());
            eventsDto.add(s);
        }
        logger.info("Returning {} searched events for user: {}", eventsDto.size(), username);
        return eventsDto;
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
                    fileStoreService.deleteEventPicture(event);
                    hasChanged = true;
                }
                break;
            case FINISHED:
                if (now.after(event.getArchiveDate())) {
                    event.setStatus(EventStatus.ARCHIVED);
                    fileStoreService.deleteEventPicture(event);
                }
                break;
            case OPEN:
                if (event.getMembers().stream().count() == event.getMaxMembers()) {
                    event.setStatus(EventStatus.CLOSED);
                    event.setLastUpdated(now);
                    hasChanged = true;
                }
                if (event.getDeadline().before(now)) {
                    event.setStatus(EventStatus.CLOSED);
                    hasChanged = true;
                }
                break;
            default:
                break;


        }

        return hasChanged;
    }
}
