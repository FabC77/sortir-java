package training.sortir.controller;

import lombok.RequiredArgsConstructor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import training.sortir.dto.CreateEventRequest;
import training.sortir.dto.EventResponse;
import training.sortir.dto.UploadResponse;
import training.sortir.exception.FileUploadException;
import training.sortir.service.FileStoreService;

import java.io.FileNotFoundException;
import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    private final FileStoreService fileService;
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @PostMapping("/file/upload")
    public ResponseEntity<UploadResponse> upload(@RequestParam("file") MultipartFile file, Principal principal) {
        logger.info("File upload initiated by user '{}'", principal.getName());
        logger.debug("File name: {}", file.getOriginalFilename());
        String username = principal.getName();
        try {
            String fileName = fileService.uploadFile(file, username);
            logger.info("File '{}' uploaded successfully by user '{}'", fileName, username);
            UploadResponse response = new UploadResponse(fileName, "File uploaded successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (MaxUploadSizeExceededException e) {
            logger.warn("File upload failed due to size limit. User: '{}', File: '{}'", username, file.getOriginalFilename());
            UploadResponse response = new UploadResponse(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        } catch (FileUploadException e) {
            logger.error("File upload failed for user '{}'. Error: {}", username, e.getMessage());
            UploadResponse response = new UploadResponse(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error during file upload for user '{}'. Error: {}", username, e.getMessage());
            UploadResponse response = new UploadResponse(null, "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/file/{fileName}/cancel")
    public ResponseEntity<UploadResponse> cancelUpload(@PathVariable String fileName, Principal principal) {
        String username = principal.getName();
        logger.info("User '{}' is attempting to cancel the upload of file '{}'", username, fileName);

        try {
            fileService.cancelUpload(fileName, username);
            logger.info("Upload of file '{}' successfully cancelled by user '{}'", fileName, username);
            UploadResponse response = new UploadResponse(fileName, "File upload cancelled successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (FileNotFoundException e) {
            logger.warn("File '{}' not found for cancellation by user '{}'", fileName, username);
            UploadResponse response = new UploadResponse(null, "File not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (FileUploadException e) {
            logger.error("Error cancelling file upload for user '{}'. File: '{}'. Error: {}", username, fileName, e.getMessage());
            UploadResponse response = new UploadResponse(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error while cancelling upload for user '{}'. File: '{}'. Error: {}", username, fileName, e.getMessage());
            UploadResponse response = new UploadResponse(null, "An unexpected error occurred while cancelling the upload.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
