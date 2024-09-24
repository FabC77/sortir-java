package training.sortir.service;

import org.springframework.stereotype.Service;
import training.sortir.dto.CampusDTO;

import java.util.List;

@Service
public interface MainService {
    List<CampusDTO> getCampuses();
}
