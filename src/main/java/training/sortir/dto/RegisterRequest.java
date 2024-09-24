package training.sortir.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotNull(message = "firstname is null")
    @NotBlank(message = "firstname is empty")
    private String firstname;
    @NotNull(message = "lastname is null")
    @NotBlank(message = "lastname is empty")
    private String lastname;
    @NotNull(message = "username is null")
    @NotBlank(message = "lastname is empty")
    private String username;
    @NotNull(message = "password is null")
    @NotBlank(message = "lastname is empty")
    @Size(min = 3, max = 15)
    private String password;
    @NotNull(message = "email is null")
    @NotBlank(message = "email is empty")
    private String email;


    private String phoneNumber;
    @NotNull(message = "CampusId is null")
    private int campusId;
    private MultipartFile profilePicture;
}
