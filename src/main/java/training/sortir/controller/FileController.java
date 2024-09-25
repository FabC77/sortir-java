package training.sortir.controller;

import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import training.sortir.dto.CreateEventRequest;
import training.sortir.dto.EventResponse;
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
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file, Principal principal) {
        String username = principal.getName();
        try {
            String fileName = fileService.uploadFile(file, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(fileName);
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @DeleteMapping("/file/{fileName}/cancel")
    public ResponseEntity<String> cancelUpload(@PathVariable String fileName, Principal principal) {
        String username = principal.getName();
        try {
            fileService.cancelUpload(fileName, username);
            return ResponseEntity.status(HttpStatus.OK).body("File upload cancelled successfully: " + fileName);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + e.getMessage());
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while cancelling the upload.");
        }
    }
}
