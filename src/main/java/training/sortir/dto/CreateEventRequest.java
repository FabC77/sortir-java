package training.sortir.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class CreateEventRequest {

    @NotNull
    @NotBlank
    private String name;
    private String infos;
    @NotNull
    @NotBlank
    private boolean isDraft;
    private byte[] picture;
    private String locationId;
    private String locationName;
    private float longitude;
    private float latitude;
    private String cityName;
    private String address;
    @NotNull
    @NotBlank
    private Date startDate;
    private Duration duration;
    @NotNull
    @NotBlank
    private Date deadline;
    @NotNull
    @NotBlank
    private int maxMembers;
    private List<User> members;

}
