package training.sortir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import training.sortir.entities.City;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
   Optional<City> findByName(String name);

   Optional<City> findByNameAndZipCode(String cityName, String zipCode);
}
