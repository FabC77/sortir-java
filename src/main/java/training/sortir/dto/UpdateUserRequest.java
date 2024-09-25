package training.sortir.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {


    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
    private int campusId;
    private String profilePicture;
}
