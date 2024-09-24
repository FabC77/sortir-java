package training.sortir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

    private String firstname;
    private String lastname;
    private String profilePicture;
    private String username;
    private boolean isOrganizer;
}
