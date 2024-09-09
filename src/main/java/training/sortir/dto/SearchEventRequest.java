package training.sortir.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchEventRequest {
    private String keyword= "";
    private int campusId;
    private Date startDate;
    private Date endDate;
    private boolean full;

}
