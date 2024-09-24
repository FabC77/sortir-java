package training.sortir.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private EventStatus status = EventStatus.DRAFT;
    @NonNull
    private UUID organizerId;
    private String name;
    private String infos;
    private String reason;
    private String picture;
    @JsonBackReference("location-events")
    @ManyToOne
    private Location location;
    @JsonBackReference("campus-events")
    @ManyToOne
    private Campus campus;
    private Date startDate;
    private Duration duration;
    private Date archiveDate;
    private Date deadline;
    private Date lastUpdated;
    private int maxMembers;
    @NonNull
    private int currentMembers;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "event_members",
            joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "member_id"))
    private List<User> members = new ArrayList<>();

    public void removeMember(User user) {
        this.members.remove(user);
        user.getEvents().remove(this);
    }
}
