package training.sortir.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import training.sortir.entities.Event;
import training.sortir.entities.User;

@Service
public interface FileStoreService {


    String uploadProfilePicture(MultipartFile data, User user);

    String uploadEventPicture(MultipartFile data, Event event);

    boolean deleteProfilePicture( User user);

    boolean deleteEventPicture(Event event);

    String getFullUrl(String pictureName);

}
