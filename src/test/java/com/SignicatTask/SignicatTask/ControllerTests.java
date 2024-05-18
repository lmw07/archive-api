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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;



@WebMvcTest(Controller.class)
public class ControllerTests {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArchiveService service;

    @Test
    public void testUploadOneFileReturns200andZip() throws Exception{
        MockMultipartFile file = new MockMultipartFile("file", "Hello file".getBytes());

        when(service.archiveFiles(any())).thenReturn(CompletableFuture.completedFuture(new byte[]{1, 2, 3, 4}));

        
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                //"Content-Disposition : attachment" ensures that file is downloaded by browser
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"archive.zip\""));
    }

    @Test
    public void testUploadMultipleFileReturns200andZip() throws Exception{
        MockMultipartFile file1 = new MockMultipartFile("file", "Hello file, this is file 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "Hello file, this is file 2".getBytes());

        when(service.archiveFiles(any())).thenReturn(CompletableFuture.completedFuture(new byte[]{1, 2, 3, 4}));

        
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file1)
                .file(file2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                //"Content-Disposition : attachment" ensures that file is downloaded by browser
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"archive.zip\""));
    }


}
