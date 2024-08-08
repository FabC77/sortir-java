package training.sortir.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import training.sortir.entities.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private UUID id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String email;
    private List<Message> messages = new ArrayList<>();

}
