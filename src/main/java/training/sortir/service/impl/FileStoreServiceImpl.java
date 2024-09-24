package training.sortir.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import training.sortir.config.AWSCloudUtil;
import training.sortir.entities.Event;
import training.sortir.entities.User;
import training.sortir.repository.EventRepository;
import training.sortir.repository.UserRepository;
import training.sortir.service.FileStoreService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileStoreServiceImpl implements FileStoreService {

    @Value("${aws.s3.baseurl}")
    private String S3_URL;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    private boolean uploadFileToS3(MultipartFile data) {
        try {

            AWSCloudUtil util = new AWSCloudUtil();
            //vérifier si un fichier est lié
            //vérifier s'il est déjà uploader
            //supprimer le fichier 1 avant d'ernegistrer le fichier 2.
            util.uploadFileToS3(data.getOriginalFilename(), data.getBytes() );

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteFileFromS3(String filename) {

            AWSCloudUtil util = new AWSCloudUtil();
            util.deleteFileFromS3(filename );

    }


    @Override
    @Transactional
    public String uploadProfilePicture(MultipartFile data, User user) {
        if (user.getProfilePicture() != null) {
            deleteFileFromS3(user.getProfilePicture());
        }

        if (uploadFileToS3(data)) {
            user.setProfilePicture(data.getOriginalFilename());

            return String.format("File %s uploaded successfully.", data.getOriginalFilename());
        }
        return String.format("File %s upload failed.", data.getOriginalFilename());


    }

    @Override
    public String uploadEventPicture(MultipartFile data, Event event) {

        if (uploadFileToS3(data)) {
            event.setPicture(data.getOriginalFilename());

            return String.format("File %s uploaded successfully.", data.getOriginalFilename());
        }
        return String.format("File %s upload failed.", data.getOriginalFilename());

    }

    @Override
    public boolean deleteProfilePicture(User user) {
        //TODO
        return false;
    }

    @Override
    public boolean deleteEventPicture(Event event) {
        //TODO
        return false;
    }

    @Override
    public String getFullUrl(String pictureName) {
        return S3_URL+pictureName;
    }

}
