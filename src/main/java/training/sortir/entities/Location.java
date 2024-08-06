package training.sortir.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    private String name;
    private String address;
    private float latitude;
    private float longitude;
    @ManyToOne
    private City city;

    @OneToMany(mappedBy = "location")
    private List<Event> events = new ArrayList<>();
//    @NonNull
//    private long cityId;
//


}
