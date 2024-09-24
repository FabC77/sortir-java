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
    private String organizerName;
    private boolean isCreator;
    private boolean isEventMember;
    private String name;
    private String infos;
    private String reason;
    private String picture;
    private String locationId;
    private String locationName;
    private String address;
    private int campusId;
    private String campusName;
    private Date startDate;
    private String duration;
    private Date deadline;
    private int maxMembers;
    private int currentMembers;
    private Date lastUpdated;
    private List<MemberDto> members;
}
