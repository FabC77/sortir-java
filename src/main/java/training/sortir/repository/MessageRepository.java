package training.sortir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import training.sortir.entities.Message;

import java.util.List;
import java.util.UUID;

@Service
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByOwnerId(UUID ownerId);
}
