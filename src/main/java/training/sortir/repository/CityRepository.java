package training.sortir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface CityRepository extends JpaRepository<CityRepository, Long> {
}
