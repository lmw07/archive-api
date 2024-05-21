package com.SignicatTask.SignicatTask.Archiving;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*
 * Service class for handling archiving.
 */
@Service
public class ArchiveService {

    private ArchiverInterface archiver;

    // default constructor sets archiving method to zip
    public ArchiveService() {
        this.setArchivingMethod(ArchivingMethodEnum.ZIP);
    }

    public ArchiveService(ArchivingMethodEnum method) {
        this.setArchivingMethod(method);
    }

    // Add support for new archiving methods here
    public void setArchivingMethod(ArchivingMethodEnum method) {
        switch (method) {
            case ArchivingMethodEnum.ZIP:
                this.archiver = new ZipArchiver();
                break;
            default:
                break;
        }
    }

    public byte[] archiveFiles(MultipartFile[] files) throws IOException {
        return archiver.compress(files);
    }
}
