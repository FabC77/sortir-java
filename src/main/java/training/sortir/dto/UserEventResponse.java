package training.sortir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import training.sortir.entities.EventStatus;
import training.sortir.entities.User;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEventResponse {

    private long id;
    private EventStatus status;
    private String organizerName;
    private boolean isOrganizer;
    private String name;
    private String reason;
    private int campusId;
    private String campusName;
    private Date startDate;
    private Date deadline;
    private int maxMembers;
    private int currentMembers;
}
