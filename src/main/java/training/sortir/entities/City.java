package training.sortir.entities;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "cities")
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private String name;
    @NonNull
    private String zipCode;

    @OneToMany(mappedBy = "city")
    private List<Location> locations = new ArrayList<>();

    public City(String name,String zipCode) { this.name = name; this.zipCode = zipCode; }
}