package training.sortir.service.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import training.sortir.tools.AWSCloudUtil;
import training.sortir.entities.Event;
import training.sortir.entities.User;
import training.sortir.exception.FileUploadException;
import training.sortir.repository.EventRepository;
import training.sortir.repository.UserRepository;
import training.sortir.service.FileStoreService;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStoreServiceImpl implements FileStoreService {

    @Value("${aws.s3.baseurl}")
    private String S3_URL;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    private String uploadFileToS3(MultipartFile data) {
        try {
            String fileName = data.getOriginalFilename();
            fileName = generateUniqueFileName(fileName);
            System.out.println("AFTER RENAMED FILE : "+fileName);
            AWSCloudUtil util = new AWSCloudUtil();
            util.uploadFileToS3(fileName, data.getBytes());
            System.out.println("AFTER util.uploadFileToS3");
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return String.format("File %s upload failed.", data.getOriginalFilename());
        }

    }

    private String generateUniqueFileName(String originalFilename) {
        String nameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueName = nameWithoutExtension + "-" + UUID.randomUUID().toString().substring(0, 8) + extension;
        return uniqueName;
    }

    public void deleteFileFromS3(String filename) {

        AWSCloudUtil util = new AWSCloudUtil();
        util.deleteFileFromS3(filename);

    }


    @Override
    @Transactional
    public void confirmProfilePicture(String filename, User user) {
        if (user.getProfilePicture() != null) {
            deleteFileFromS3(user.getProfilePicture());
        }
        AWSCloudUtil util = new AWSCloudUtil();
        util.confirmFile(filename, "profile-picture/");
user.setProfilePicture("profile-picture/"+filename);
    }

    @Override
    public void confirmEventPicture(String filename, Event event) {
        if (event.getPicture() != null) {
            deleteFileFromS3(event.getPicture());
        }
        AWSCloudUtil util = new AWSCloudUtil();
        util.confirmFile(filename, "event-picture/");
event.setPicture("event-picture/"+filename);

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
        return S3_URL + pictureName;
    }

    @Override
    public String uploadFile(MultipartFile file, String username) throws FileUploadException {
        System.out.println("IN UPLOAD FILE \n");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username + ". Unauthorized."));
        System.out.println("AFTER USER CHECK \n");
        String response = uploadFileToS3(file);
        System.out.println("AFTER uploadFileToS3 method \n");
        return response;
    }

    @Override
    public void cancelUpload(String filename, String username) throws FileNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username + ". Unauthorized."));

        try {
            AWSCloudUtil util = new AWSCloudUtil();
            util.deleteFileFromS3("temp-files/" + filename);
        } catch (AwsServiceException e) {
            throw new FileUploadException("Failed to delete file from S3: " + e.getMessage());
        }

    }


}
