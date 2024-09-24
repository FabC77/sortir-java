package training.sortir.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import training.sortir.token.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NonNull
    private String username;
    @NonNull
    private String firstname;
    @NonNull
    private String lastname;
    @Column(unique = true, nullable = false)
    private String email;
    private String phoneNumber;
    private String password;
    private boolean IsAdmin;
    private boolean IsActive = true;
    private int campusId;
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @ManyToMany(mappedBy = "members")
    private List<Event> events = new ArrayList<>();

    @JsonManagedReference("owner-messages")
    @OneToMany(mappedBy = "owner")
    private List<Message> messages = new ArrayList<>();

    @JsonManagedReference("owner-token")
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public void removeEvent(Event event) {
        this.events.remove(event);
        event.getMembers().remove(this);
    }

    public User(@NonNull String username, @NonNull String firstname, @NonNull String lastname, String email, String password, int campusId) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.campusId = campusId;
    }
}
