package com.SignicatTask.SignicatTask;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.SignicatTask.SignicatTask.Archiving.ArchiveService;
import com.SignicatTask.SignicatTask.Repository.LogRepository;
import com.SignicatTask.SignicatTask.Repository.RequestData;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class Controller {

    private final LogRepository logRepo;
    private ArchiveService archiveService;

    public Controller(LogRepository repository, ArchiveService archiveService){
        this.logRepo = repository;
        this.archiveService = archiveService;
    }


    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files, HttpServletRequest request) {
        if (files.length == 0 || files == null) {
            return ResponseEntity.badRequest().build();
        }

        // Allow no file larger than 1MB
        final long MAX_FILE_SIZE = 1024*1024;
        final long MAX_TOTAL_SIZE = MAX_FILE_SIZE * 10;

        //check size of files
        long totalSize = 0;
        for (MultipartFile file : files) {
            long fileSizeInBytes = file.getSize();
            totalSize += fileSizeInBytes;
            if (fileSizeInBytes > MAX_FILE_SIZE || totalSize > MAX_TOTAL_SIZE) {
                RequestData rqd = new RequestData(LocalDate.now(), request.getRemoteAddr(), RequestData.Status.FAIL);
                logRepo.save(rqd);
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("Files larger than 1MB not allowed.");
                
            }
        }

        try {
            byte[] zipFile = archiveService.archiveFiles(files);
            
            // save to DB
            RequestData rqd = new RequestData(LocalDate.now(), request.getRemoteAddr(), RequestData.Status.SUCCESS);
            logRepo.save(rqd);

            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"archive.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipFile);
        } catch (Exception e) {
            // Log the error and return an appropriate error response
            return ResponseEntity.internalServerError()
                .body(null);
        }
    }
}





