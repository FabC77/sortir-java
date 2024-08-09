package training.sortir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import training.sortir.entities.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {


    List<Event> findByCampusId(int campusId);
}
