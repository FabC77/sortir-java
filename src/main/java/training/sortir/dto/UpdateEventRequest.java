package training.sortir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import training.sortir.entities.EventStatus;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
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
    private String locationNotNamed;
    private float longitude;
    private float latitude;
    private String cityName;
    private String address;
    private String zipCode;
    private Date startDate;
    private Map<String,Integer> duration;
    private Date deadline;
    private int maxMembers;

}
