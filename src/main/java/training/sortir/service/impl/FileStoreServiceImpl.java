package training.sortir.service.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


import java.io.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStoreServiceImpl implements FileStoreService {

    @Value("${aws.s3.baseurl}")
    private String S3_URL;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private static final Logger logger = LoggerFactory.getLogger(FileStoreServiceImpl.class);

    private String uploadFileToS3(MultipartFile data) {
        try {
            String fileName = data.getOriginalFilename();
            fileName = generateUniqueFileName(fileName);
            logger.info("Renamed file: {}", fileName);
            AWSCloudUtil util = new AWSCloudUtil();
            util.uploadFileToS3(fileName, data.getBytes());
            logger.info("File '{}' successfully uploaded to S3.", fileName);
            return fileName;
        } catch (IOException e) {
            logger.error("File upload failed for '{}'. Error: {}", data.getOriginalFilename(), e.getMessage());
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
        logger.info("Attempting to delete file '{}' from S3.", filename);
        AWSCloudUtil util = new AWSCloudUtil();
        util.deleteFileFromS3(filename);
        logger.info("File '{}' successfully deleted from S3.", filename);
    }


    @Override
    @Transactional
    public void confirmProfilePicture(String filename, User user) {
        if (user.getProfilePicture() != null) {
            logger.info("Deleting existing profile picture for user '{}'.", user.getUsername());
            deleteFileFromS3(user.getProfilePicture());
        }
        AWSCloudUtil util = new AWSCloudUtil();
        util.confirmFile(filename, "profile-picture/");
        util.confirmSmallFile(filename, "profile-picture/small/");
        user.setProfilePicture(filename);
        logger.info("Profile picture '{}' confirmed for user '{}'.", filename, user.getUsername());
    }

    @Override
    public void confirmEventPicture(String filename, Event event) {
        if (event.getPicture() != null) {
            logger.info("Deleting existing event picture for event '{}'.", event.getId());
            deleteFileFromS3(event.getPicture());
        }
        AWSCloudUtil util = new AWSCloudUtil();
        util.confirmFile(filename, "event-picture/");
        util.confirmSmallFile(filename, "event-picture/small/");
        event.setPicture(filename);
        logger.info("Event picture '{}' confirmed for event '{}'.", filename, event.getId());
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
    public String uploadFile(MultipartFile file, String username) throws FileUploadException {
        logger.info("User '{}' initiated file upload.", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username + ". Unauthorized."));
        logger.info("User '{}' found. Proceeding with file upload.", username);
        String response = uploadFileToS3(file);
        logger.info("File upload process completed for user '{}'.", username);
        return response;
    }

    @Override
    public void cancelUpload(String filename, String username) throws FileNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username + ". Unauthorized."));

        try {
            logger.info("User '{}' attempting to cancel file upload '{}'.", username, filename);
            AWSCloudUtil util = new AWSCloudUtil();
            util.deleteFileFromS3("temp-files/" + filename);
            logger.info("File '{}' successfully canceled and deleted from S3 for user '{}'.", filename, username);
        } catch (AwsServiceException e) {
            logger.error("Failed to delete file '{}' from S3 for user '{}'. Error: {}", filename, username, e.getMessage());
            throw new FileUploadException("Failed to delete file from S3: " + e.getMessage());
        }

    }


}
