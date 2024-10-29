package training.sortir.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import training.sortir.dto.*;
import training.sortir.entities.*;
import training.sortir.repository.*;
import training.sortir.service.FileStoreService;
import training.sortir.tools.EventMapper;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class EventServiceImplTest {

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private LocationRepository locationRepository;

    @MockBean
    private CampusRepository campusRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CityRepository cityRepository;

    @MockBean
    private FileStoreService fileStoreService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private EventMapper eventMapper;

    private User user;
    private User user2;
    private CreateEventRequest createEventRequest;
    private Event event;
    private Event event2;
    private Location location;
    private City city;
    private Campus campus;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UUID id = UUID.randomUUID();
        user = new User();
        user.setId(id);
        user.setUsername("testUser");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("jd@example.com");
        user.setCampusId(1);
        UUID id2 = UUID.randomUUID();
        user2 = new User();
        user2.setId(id2);
        user2.setUsername("testUser2");
        user2.setFirstname("Bob");
        user2.setLastname("Morane");
        user2.setEmail("bm@example.com");
        user2.setCampusId(2);

        createEventRequest = new CreateEventRequest();
        createEventRequest.setName("Event Name");
        createEventRequest.setCityName("Test City");
        createEventRequest.setZipCode("12345");
        createEventRequest.setLocationId("id");
        Map<String, Integer> duration = new HashMap<>();
        duration.put("hours", 1);
        duration.put("minutes", 1);
        createEventRequest.setDuration(duration);
        createEventRequest.setLocationName("Test Location");
        createEventRequest.setStartDate(new Date());
        createEventRequest.setAddress("123 Main St");
        createEventRequest.setLongitude(1.23f);
        createEventRequest.setLatitude(2.34f);

        location = new Location();
        location.setId("id");
        location.setName("Test Location");


        city = new City();
        city.setName("Test City");
        city.setZipCode("12345");

        campus = new Campus();
        campus.setId(1);
        campus.setName("Test Campus");

        event = new Event();
        event.setId(1);
        event.setStatus(EventStatus.OPEN);
        event.setOrganizerId(id);
        event.setCampus(campus);
        event.setLocation(location);
        event.setDuration(Duration.ofHours(2));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 2);
        Date eventDate = calendar.getTime();
        event.setStartDate(eventDate);
        Calendar deadlineCal = Calendar.getInstance();
        deadlineCal.add(Calendar.MONTH, 1);
        Date deadline = deadlineCal.getTime();
        event.setDeadline(deadline);
        event.setMaxMembers(10);

        event2 = new Event();
        event2.setId(2);
        event2.setStatus(EventStatus.CLOSED);
        UUID id3 = UUID.randomUUID();
        event2.setOrganizerId(id3);
        event2.setCampus(campus);
        event2.setLocation(location);
        event2.setDuration(Duration.ofHours(1));
        event2.setMaxMembers(10);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.MONTH, 3);
        Date eventDate2 = calendar2.getTime();
        event2.setStartDate(eventDate2);
        Calendar deadlineCal2 = Calendar.getInstance();
        deadlineCal2.add(Calendar.MONTH, 2);
        Date deadline2 = deadlineCal2.getTime();
        event2.setDeadline(deadline2);

        user.setEvents(new ArrayList<>(List.of(event, event2)));
    }

    @Test
    void EventShouldBeCreated() {

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        //doReturn(Optional.of(user)).when(userRepository).findByUsername("testUser");
        when(cityRepository.findByNameAndZipCode(anyString(), anyString())).thenReturn(Optional.of(city));
        when(locationRepository.findById(anyString())).thenReturn(Optional.of(location));
        when(campusRepository.findById(anyInt())).thenReturn(Optional.of(campus));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
user.getEvents().clear();
        EventResponse response = eventService.createEvent(createEventRequest, "testUser");

        assertNotNull(response);
        assertEquals(1, response.getId());

        verify(userRepository, times(1)).findByUsername("testUser");
        verify(cityRepository, times(1)).findByNameAndZipCode(anyString(), anyString());
        verify(locationRepository, times(1)).findById(anyString());
        verify(campusRepository, times(1)).findById(anyInt());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void ShouldTriggerUserNotFoundException() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());


        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                eventService.createEvent(createEventRequest, "testUser")
        );

        assertEquals("User not found with username: testUser", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void EventShouldBeUpdated() {
        Date now = new Date();
        event.setLocation(location);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(campusRepository.findById(anyInt())).thenReturn(Optional.of(campus));
        when(cityRepository.findById(anyLong())).thenReturn(Optional.of(city));
        when(locationRepository.findById(anyString())).thenReturn(Optional.of(location));
        doNothing().when(fileStoreService).confirmEventPicture(anyString(), any(Event.class));
        event.setPicture("updated_picture_url");

        EventStatus previousStatus = event.getStatus();
        UpdateEventRequest updateEventRequest = new UpdateEventRequest();
        updateEventRequest.setStatus(EventStatus.DRAFT);
        updateEventRequest.setName("Updated Event Name");
        updateEventRequest.setInfos("Updated event info");
        updateEventRequest.setReason("Updated event reason");
        updateEventRequest.setPicture("updated_picture_url");
        updateEventRequest.setLocationId("newLocationId");
        updateEventRequest.setLongitude(1.5f);
        updateEventRequest.setLatitude(2.5f);
        updateEventRequest.setCityName("Test City");
        updateEventRequest.setZipCode("12345");
        updateEventRequest.setAddress("updated_address");
        updateEventRequest.setLocationName("Test Location");
        updateEventRequest.setStartDate(now);
        Map<String, Integer> newDuration = new HashMap<>();
        newDuration.put("hours", 2);
        newDuration.put("minutes", 0);
        updateEventRequest.setDuration(newDuration);
        updateEventRequest.setDeadline(now);
        updateEventRequest.setMaxMembers(50);


        EventResponse response = eventService.update(updateEventRequest, 1, "testUser");

        assertNotNull(response);
        assertEquals(EventStatus.DRAFT, response.getStatus());
        assertEquals("Test Location", response.getLocationName());
        assertEquals("Updated Event Name", response.getName());
        assertEquals("Updated event info", response.getInfos());
        assertEquals("Updated event reason", response.getReason());
        assertEquals("updated_picture_url", response.getPicture());
        assertEquals(now, response.getStartDate(), "Start date should match");
        assertEquals("2h00", response.getDuration());
        assertEquals(now, response.getDeadline(), "Deadline should match");
        assertEquals(50, response.getMaxMembers());
        assertEquals(user.getFirstname() + " " + user.getLastname(), response.getOrganizerName());
        assertEquals(campus.getName(), response.getCampusName());

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void EventShouldBeCanceledByOrganizer() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        CancelEventRequest cancelEventRequest = new CancelEventRequest();
        cancelEventRequest.setReason("Reason for cancellation");

        boolean result = eventService.cancel(cancelEventRequest, 1, "testUser");

        assertTrue(result);
        assertEquals(EventStatus.CANCELLED, event.getStatus());
        assertEquals("Reason for cancellation", event.getReason());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void EventShouldBeJoined() {
        when(userRepository.findByUsername("testUser2")).thenReturn(Optional.of(user2));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        event.setStatus(EventStatus.OPEN);
        event.setMaxMembers(10);
        event.setCurrentMembers(5);
        event.setMembers(new ArrayList<>());

        List<MemberDto> members = eventService.joinEvent(1, "testUser2");

        assertNotNull(members);
        assertTrue(event.getMembers().contains(user2));
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void EventShouldBeLeaved() {
        when(userRepository.findByUsername("testUser2")).thenReturn(Optional.of(user2));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        event.setMembers(new ArrayList<>(List.of(user2)));

        List<MemberDto> members = eventService.leaveEvent(1, "testUser2");

        assertNotNull(members);
        assertFalse(event.getMembers().contains(user2));
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void EventShouldBeRetrieved() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(userRepository.findByUsername("testUser2")).thenReturn(Optional.of(user2));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

        EventResponse response = eventService.getEvent(1, "testUser2");

        assertNotNull(response);
        assertEquals(event.getId(), response.getId());
        assertEquals(event.getName(), response.getName());
        assertEquals(event.getInfos(), response.getInfos());
        assertEquals(event.getReason(), response.getReason());
        assertEquals(event.getPicture(), response.getPicture());
        assertEquals(event.getStatus(), response.getStatus());
        assertEquals(event.getStartDate(), response.getStartDate(), "Start date should match");
        assertNotNull(response.getDuration());
        assertEquals(event.getDeadline(), response.getDeadline(), "Deadline should match");
        assertEquals(event.getMaxMembers(), response.getMaxMembers());
        assertEquals(event.getCampus().getId(), response.getCampusId());

        verify(eventRepository,times(1)).findById(1L);
        verify(userRepository,times(1)).findByUsername("testUser2");
        verify(userRepository,times(1)).findById(any(UUID.class));
    }

    @Test
    void UserEventResponseTransformationShouldBeCorrect() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user2));
        user.setEvents(List.of(event));

        List<UserEventResponse> rawResponse = eventService.getUserEvents("testUser");
        UserEventResponse singleEventTransformed = rawResponse.get(0);

        assertNotNull(singleEventTransformed);
        assertEquals(event.getId(), singleEventTransformed.getId());
        assertEquals(EventStatus.OPEN, singleEventTransformed.getStatus());
        assertTrue(singleEventTransformed.isOrganizer());
        assertEquals(user.getFirstname() + " " + user.getLastname(), singleEventTransformed.getOrganizerName());
        assertEquals(location.getName(), singleEventTransformed.getLocationName());
        assertEquals(event.getName(), singleEventTransformed.getName());
        assertEquals(event.getReason(), singleEventTransformed.getReason());
        assertEquals(event.getCampus().getId(), singleEventTransformed.getCampusId());
        assertEquals(event.getCampus().getName(), singleEventTransformed.getCampusName());
        assertEquals(event.getStartDate(), singleEventTransformed.getStartDate());
        assertEquals(event.getDeadline(), singleEventTransformed.getDeadline());
        assertEquals(event.getMaxMembers(), singleEventTransformed.getMaxMembers());
        assertEquals(event.getCurrentMembers(), singleEventTransformed.getCurrentMembers());
        assertEquals(event.getLastUpdated(), singleEventTransformed.getLastUpdated());
        assertEquals(event.getPicture(), singleEventTransformed.getPicture());
     }

    @Test
    void UserEventsCountShouldBeRetrieved() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user2));

        List<UserEventResponse> events = eventService.getUserEvents("testUser");

        assertNotNull(events);
        assertEquals(2, events.size());

        verify(userRepository,times(1)).findByUsername("testUser");
        verify(userRepository,times(1)).findById(any(UUID.class));

    }

    @Test
    void ShouldReturnEmptyListWhenNoEvents() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        user.setEvents(Collections.emptyList());

        List<UserEventResponse> events = eventService.getUserEvents("testUser");

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }


}
