package com.SignicatTask.SignicatTask.Archiving;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/*
 * Interface for different archiving methods.
 */
public interface ArchiverInterface {
    byte[] compress(MultipartFile[] files) throws IOException;
}
