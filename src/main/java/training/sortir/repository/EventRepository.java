package training.sortir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import training.sortir.entities.Event;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByStartDateBetweenAndCampusIdAndNameContainingIgnoreCase(Date startDate, Date endDate, int campusId, String keyword);


    List<Event> findByCampusIdAndNameContainingIgnoreCase(int campusId, String keyword);
    List<Event> findByCampusId(int campusId);

    List<Event> findByStartDateBeforeAndCampusIdAndNameContainingIgnoreCase(Date endDate, int campusId,String keyword);

    List<Event> findByStartDateAfterAndCampusIdAndNameContainingIgnoreCase(Date startDate, int campusId,String keyword);
}
