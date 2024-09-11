package training.sortir.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import training.sortir.dto.CampusDTO;
import training.sortir.entities.Campus;
import training.sortir.repository.CampusRepository;
import training.sortir.service.MainService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final CampusRepository campusRepository;

    @Override
    public List<CampusDTO> getCampuses() {
        List<Campus> campus = campusRepository.findAll();
        List<CampusDTO> dtos= new ArrayList<CampusDTO>();
        for (Campus cam : campus) {
            CampusDTO dto= new CampusDTO();
            dto.setId(cam.getId());
            dto.setName(cam.getName());
        dtos.add(dto);
        }

        return dtos;
    }
}
