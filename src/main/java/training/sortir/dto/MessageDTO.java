package training.sortir.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import training.sortir.entities.User;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private int id;
    private String content;
    private User owner;
    private Date createdAt;


}
