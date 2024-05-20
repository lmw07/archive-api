package com.SignicatTask.SignicatTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import com.SignicatTask.SignicatTask.Archiving.ZipArchiver;


public class ZipArchiverTests {

   
    private ZipArchiver zipper = new ZipArchiver();

    @Test
    void testBasicZipOf2Files() throws IOException {

        // Create mock MultipartFiles
        MockMultipartFile mockFile1 = new MockMultipartFile(
            "file1.txt",
            "file1.txt",
            "application/octet-stream",
            "Hello World".getBytes()
        );

        MockMultipartFile mockFile2 = new MockMultipartFile(
            "file2.txt",
            "file2.txt",
            "text/plain",
            "Goodbye World".getBytes()
        );

        MockMultipartFile[] files = { mockFile1, mockFile2 };

        // compress files
        byte[] compressedData = zipper.compress(files);

        
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(compressedData));

        // position ZipInputStream at beginning of first file
        ZipEntry zipEntry = zis.getNextEntry();
        assertEquals("file1.txt", zipEntry.getName());

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        
        while ((len = zis.read(buffer)) > 0) {
            content.write(buffer, 0, len);
        }
        assertEquals("Hello World", content.toString(), "Content of 'file1.txt' should match");

        // position ZipInputStream at beginning of second file
        zipEntry = zis.getNextEntry();
        assertEquals("file2.txt", zipEntry.getName());


        content.reset(); // reset ByteArrayOutputStream to empty
        while ((len = zis.read(buffer)) > 0) {
            content.write(buffer, 0, len);
        }
        assertEquals("Goodbye World", content.toString(), "Content of 'file2.txt' should match");

        assertNull(zis.getNextEntry(), "There should be no more files in the zip");

        zis.close();
        content.close();

    }
}
