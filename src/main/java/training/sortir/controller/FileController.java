package training.sortir.controller;

import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import training.sortir.dto.CreateEventRequest;
import training.sortir.dto.EventResponse;
import training.sortir.exception.FileUploadException;
import training.sortir.service.FileStoreService;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    private final FileStoreService fileService;

    @PostMapping("/file/upload")
    public ResponseEntity<String> upload(@RequestBody MultipartFile file, Principal principal) {
        String username = principal.getName();
        try{
            String fileName = fileService.uploadFile(file, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(fileName);
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) { // Gestion des erreurs générales
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
