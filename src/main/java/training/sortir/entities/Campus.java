package training.sortir.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="campuses")
public class Campus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NonNull
    private String name;
@JsonManagedReference("campus-events")
    @OneToMany(mappedBy="campus")
private List<Event> events = new ArrayList<>();

    public Campus(String name) {this.name = name;  }
}
