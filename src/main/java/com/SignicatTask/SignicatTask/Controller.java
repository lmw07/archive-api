package com.SignicatTask.SignicatTask;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import com.SignicatTask.SignicatTask.Archiving.ArchiveService;
import com.SignicatTask.SignicatTask.Archiving.ArchivingMethod;
import com.SignicatTask.SignicatTask.Repository.LogRepository;
import com.SignicatTask.SignicatTask.Repository.RequestData;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class Controller {

    private final LogRepository logRepo;
    private ArchiveService archiveService;

    public Controller(LogRepository repository, ArchiveService archiveService) {
        this.logRepo = repository;
        this.archiveService = archiveService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "method", defaultValue = "ZIP") ArchivingMethod method, HttpServletRequest request) {
        if (files.length == 0 || files == null) {
            RequestData rqd = new RequestData(LocalDate.now(), request.getRemoteAddr(), RequestData.Status.FAIL);
            logRepo.save(rqd);
            return ResponseEntity.badRequest().build();
        }

        try {
            archiveService.setArchivingMethod(method);
            byte[] zipFile = archiveService.archiveFiles(files);

            // save to DB
            RequestData rqd = new RequestData(LocalDate.now(), request.getRemoteAddr(), RequestData.Status.SUCCESS);
            logRepo.save(rqd);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"archive.zip\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipFile);
        }
         catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Handle File too large and return 413
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> maxUploadSizeExceeded(MaxUploadSizeExceededException e,HttpServletRequest request) {
        RequestData rqd = new RequestData(LocalDate.now(), request.getRemoteAddr(), RequestData.Status.FAIL);
                logRepo.save(rqd);
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("Files larger than 1MB not allowed. Cumulative file size shouldn't exceed 10MB.");
    }

    // Catch all other Tomcat exceptions and return code 400
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> badRequest(Exception e,HttpServletRequest request) {
        RequestData rqd = new RequestData(LocalDate.now(), request.getRemoteAddr(), RequestData.Status.FAIL);
                logRepo.save(rqd);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Malformed Request");
    }
    





}
