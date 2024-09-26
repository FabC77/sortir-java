package training.sortir.controller;

import lombok.RequiredArgsConstructor;


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

    @PostMapping("/file/upload")
    public ResponseEntity<UploadResponse> upload(@RequestParam("file") MultipartFile file, Principal principal) {
        System.out.printf("INSIDE UPLOAD CONTROLLER - START" + "\n");
        System.out.println(file.getOriginalFilename());
        String username = principal.getName();
        try {
            String fileName = fileService.uploadFile(file, username);
            System.out.printf("INSIDE UPLOAD CONTROLLER - AFTER SUCCESS // filname= " + fileName + "\n");
            UploadResponse response = new UploadResponse(fileName, "File uploaded successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (MaxUploadSizeExceededException e) {
            UploadResponse response = new UploadResponse(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        } catch (FileUploadException e) {
            UploadResponse response = new UploadResponse(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            UploadResponse response = new UploadResponse(null, "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/file/{fileName}/cancel")
    public ResponseEntity<UploadResponse> cancelUpload(@PathVariable String fileName, Principal principal) {
        String username = principal.getName();
        try {
            fileService.cancelUpload(fileName, username);
            UploadResponse response = new UploadResponse(fileName, "File upload cancelled successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (FileNotFoundException e) {
            UploadResponse response = new UploadResponse(null, "File not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (FileUploadException e) {
            UploadResponse response = new UploadResponse(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            UploadResponse response = new UploadResponse(null, "An unexpected error occurred while cancelling the upload.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
