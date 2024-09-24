package training.sortir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import training.sortir.entities.EventStatus;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchedEventDto {
    private long id;
    private EventStatus status;
    private String organizerName;
    private String name;
    private String locationName;
    private int campusId;
    private Date startDate;
    private Duration duration;
    private Date deadline;
    private int maxMembers;
    private int currentMembers;
    private Date lastUpdated;
    private String picture;

}
