package training.sortir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import training.sortir.entities.Campus;

@Repository
public interface CampusRepository extends JpaRepository<Campus,Integer> {
}
