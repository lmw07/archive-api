package com.SignicatTask.SignicatTask.Archiving;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.web.multipart.MultipartFile;

/**
 * Class for compressing files via zip
 */
public class ZipArchiver implements ArchiverInterface {

    /**
     * @param files
     * @return byte array of compressed file data
     */
    @Override
    public byte[] compress(MultipartFile[] files) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        try {
            for (MultipartFile file : files) {
                // Create a zip entry for each file and add it to ZipOutputStream
                ZipEntry zipEntry = new ZipEntry(file.getOriginalFilename());
                zos.putNextEntry(zipEntry);

                // Read file content and write it to ZipOutputStream
                byte[] bytes = file.getBytes();
                zos.write(bytes);

                // Close the current entry
                zos.closeEntry();
            }
        } finally {
            zos.close();
        }
        return baos.toByteArray();
    }
}
