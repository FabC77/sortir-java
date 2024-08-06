package training.sortir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import training.sortir.entities.Location;

import java.util.UUID;
@Service
public interface LocationRepository extends JpaRepository<Location, UUID> {
}
