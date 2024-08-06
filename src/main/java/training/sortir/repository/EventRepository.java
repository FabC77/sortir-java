package training.sortir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import training.sortir.entities.Event;

@Service
public interface EventRepository extends JpaRepository<Event,Long> {
}
