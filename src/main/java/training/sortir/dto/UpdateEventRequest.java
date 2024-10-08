package training.sortir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import training.sortir.entities.EventStatus;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventRequest {


    private EventStatus status;
    private String name;
    private String infos;
    private String reason;
    private String picture;
    private String locationId;
    private String locationName;
    private Date startDate;
    private Duration duration;
    private Date deadline;
    private int maxMembers;

}
