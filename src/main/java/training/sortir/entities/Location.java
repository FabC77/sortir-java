package training.sortir.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private String id;

    @NonNull
    private String name;
    private String address;
    private float latitude;
    private float longitude;
    @JsonBackReference("city-locations")
    @ManyToOne
    private City city;

    @JsonManagedReference("location-events")
    @OneToMany(mappedBy = "location")
    private List<Event> events = new ArrayList<>();
//    @NonNull
//    private long cityId;
//

public Location (String name,String address, City city) {
    this.name = name;
    this.address = address;
    this.city = city ;
}
}
