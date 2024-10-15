package training.sortir.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import training.sortir.dto.*;
import training.sortir.entities.*;
import training.sortir.repository.*;
import training.sortir.service.FileStoreService;
import training.sortir.tools.EventMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private CampusRepository campusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private FileStoreService fileStoreService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private EventServiceImpl eventService;

    private User user;
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

        createEventRequest = new CreateEventRequest();
        createEventRequest.setName("Event Name");
        createEventRequest.setCityName("Test City");
        createEventRequest.setZipCode("12345");
        createEventRequest.setLocationName("Test Location");
        createEventRequest.setStartDate(new Date());

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
        event.setOrganizerId(id);
    }

    @Test
    void testCreateEventSuccess() {

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
    void testCreateEventUserNotFound() {
        //when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        //when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
doReturn(Optional.empty()).when(userRepository).findByUsername("testUser");

        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                eventService.createEvent(createEventRequest, "testUser")
        );

        assertEquals("User not found with username: testUser", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testUpdateSuccess() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(campusRepository.findById(anyInt())).thenReturn(Optional.of(campus));

        UpdateEventRequest updateEventRequest = new UpdateEventRequest();
        updateEventRequest.setName("Updated Event");
        EventResponse response = eventService.update(updateEventRequest, 1, "testUser");

        assertNotNull(response);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCancelSuccess() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        CancelEventRequest cancelEventRequest = new CancelEventRequest();
        cancelEventRequest.setReason("Reason for cancellation");

        boolean result = eventService.cancel(cancelEventRequest, 1, "testUser");

        assertTrue(result);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testJoinEventSuccess() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        event.setStatus(EventStatus.OPEN);
        event.setMaxMembers(10);
        event.setCurrentMembers(5);
        event.setMembers(new ArrayList<>());

        List<MemberDto> members = eventService.joinEvent(1, "testUser");

        assertNotNull(members);
        assertTrue(event.getMembers().contains(user));
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testLeaveEventSuccess() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        event.setMembers(new ArrayList<>(List.of(user)));

        List<MemberDto> members = eventService.leaveEvent(1, "testUser");

        assertNotNull(members);
        assertFalse(event.getMembers().contains(user));
        verify(eventRepository, times(1)).save(event);
    }
}
