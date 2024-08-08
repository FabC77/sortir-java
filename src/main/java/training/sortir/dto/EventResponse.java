package training.sortir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import training.sortir.entities.Campus;
import training.sortir.entities.EventStatus;
import training.sortir.entities.Location;
import training.sortir.entities.User;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private long id;
    private EventStatus status;
    //private UUID organizerId;
    private String organizerName;
    private String name;
    private String infos;
    private String reason;
    private byte[] picture;
    private UUID locationId;
    private String locationName;
    private int campusId;
    private String campusName;
    private Date startDate;
    private Duration duration;
    private Date deadline;
    private List<User> members;
}
