package training.sortir.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import training.sortir.entities.Event;
import training.sortir.entities.User;

@Service
public interface FileStoreService {


    void confirmProfilePicture(String filename, User user);

    void confirmEventPicture(String filename, Event event);

    boolean deleteProfilePicture( User user);

    boolean deleteEventPicture(Event event);

    String getFullUrl(String pictureName);

    String uploadFile(MultipartFile data, String username);

}
