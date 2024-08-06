package training.sortir.controller.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
