package com.SignicatTask.SignicatTask;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.SignicatTask.SignicatTask.Archiving.ArchiveService;
import com.SignicatTask.SignicatTask.Repository.LogRepository;

//TODO comment

@WebMvcTest(Controller.class)
public class ControllerTests {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArchiveService service;

    @MockBean
    private LogRepository logRepository;

    @Test
    public void testUploadOneFileReturns200andZip() throws Exception{
        MockMultipartFile file = new MockMultipartFile("files", "Hello file".getBytes());

        //when(service.archiveFiles(any())).thenReturn(CompletableFuture.completedFuture(new byte[]{1, 2, 3, 4}));
        when(service.archiveFiles(any())).thenReturn(new byte[]{1, 2, 3, 4});
        
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                //"Content-Disposition : attachment" ensures that file is downloaded by browser
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"archive.zip\""));
    }

    @Test
    public void testUploadMultipleFileReturns200andZip() throws Exception{
        MockMultipartFile file1 = new MockMultipartFile("files", "Hello file, this is file 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "Hello file, this is file 2".getBytes());

        //when(service.archiveFiles(any())).thenReturn(CompletableFuture.completedFuture(new byte[]{1, 2, 3, 4}));
        when(service.archiveFiles(any())).thenReturn(new byte[]{1, 2, 3, 4});
        
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file1)
                .file(file2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                //"Content-Disposition : attachment" ensures that file is downloaded by browser
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"archive.zip\""));
    }

    @Test
    public void testUploadOneFileTooLargeReturns413() throws Exception{

        //file of size 3MB
        int fileSize = 1024 * 1024 *3;
        MockMultipartFile file = new MockMultipartFile("bigfile", new byte[fileSize]);

        //when(service.archiveFiles(any())).thenReturn(CompletableFuture.completedFuture(new byte[]{1, 2, 3, 4}));
        when(service.archiveFiles(any())).thenReturn(new byte[]{1, 2, 3, 4});
        
        // Expect status code 413 (Payload too large)
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file))
                .andExpect(MockMvcResultMatchers.status().isPayloadTooLarge());

    }

   @Test
    public void testUploadMultipleFilesTooLargeReturns413() throws Exception {
        MockMultipartFile[] files = new MockMultipartFile[21];
        
        // Populate the array with 21 files each of 0.5 MB
        int fileSize = 1024 * 1024 / 2;
        for (int i = 0; i < files.length; i++) {
            files[i] = new MockMultipartFile("files", "file" + i + ".txt", "text/plain", new byte[fileSize]);
        }

        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/upload");
        
        // Add all files to the request
        for (MockMultipartFile file : files) {
            requestBuilder.file(file);
        }
        // Expect status code 413 (Payload too large)
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isPayloadTooLarge());
}
    @Test
    public void testNoFileReturns400() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    




    


}
