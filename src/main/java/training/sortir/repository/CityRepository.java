package training.sortir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import training.sortir.entities.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
}
