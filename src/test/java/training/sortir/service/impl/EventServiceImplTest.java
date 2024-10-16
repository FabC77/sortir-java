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

    private User user;
    private User user2;
    private CreateEventRequest createEventRequest;
    private Event event;
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
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        Date eventDate = calendar.getTime();
        event.setStartDate(eventDate);
    }

    @Test
    void EventShouldBeCreated() {

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        //doReturn(Optional.of(user)).when(userRepository).findByUsername("testUser");
        when(cityRepository.findByNameAndZipCode(anyString(), anyString())).thenReturn(Optional.of(city));
        when(locationRepository.findById(anyString())).thenReturn(Optional.of(location));
        when(campusRepository.findById(anyInt())).thenReturn(Optional.of(campus));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

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
        newDuration.put("minutes",0);
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
        assertEquals("Reason for cancellation",event.getReason());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void EventCanBeJoined() {
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
    void EventCanBeLeaved() {
        when(userRepository.findByUsername("testUser2")).thenReturn(Optional.of(user2));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        event.setMembers(new ArrayList<>(List.of(user2)));

        List<MemberDto> members = eventService.leaveEvent(1, "testUser2");

        assertNotNull(members);
        assertFalse(event.getMembers().contains(user2));
        verify(eventRepository, times(1)).save(event);
    }
}
