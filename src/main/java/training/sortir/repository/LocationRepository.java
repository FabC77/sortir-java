package training.sortir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import training.sortir.entities.Location;

import java.util.UUID;
@Repository
public interface LocationRepository extends JpaRepository<Location, String> {
}
