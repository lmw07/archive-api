package com.SignicatTask.SignicatTask;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.SignicatTask.SignicatTask.Archiving.ArchiveService;
import com.SignicatTask.SignicatTask.Repository.LogRepository;
import com.SignicatTask.SignicatTask.Repository.RequestData;


/**
 * Unit tests of controller class.
 */
@WebMvcTest(Controller.class)
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArchiveService service;

    @MockBean
    private LogRepository logRepository;

    @Test
    public void testUploadOneFileReturns200andZip() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "Hello file".getBytes());

        when(service.archiveFiles(any())).thenReturn(new byte[] { 1, 2, 3, 4 });

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file)
                .param("method", "ZIP"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // "Content-Disposition : attachment" ensures that file is downloaded by browser
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition",
                        "attachment; filename=\"archive.zip\""));
        // verify save
        verify(logRepository, times(1)).save(any(RequestData.class));
    }

    @Test
    public void testUploadMultipleFileReturns200andZip() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files", "Hello file, this is file 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "Hello file, this is file 2".getBytes());

        when(service.archiveFiles(any())).thenReturn(new byte[] { 1, 2, 3, 4 });

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file1)
                .file(file2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // "Content-Disposition : attachment" ensures that file is downloaded by browser
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition",
                        "attachment; filename=\"archive.zip\""));

        // verify save
        verify(logRepository, times(1)).save(any(RequestData.class));
    }

    @Test
    public void testNoFileReturns400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testInvalidMethodParamReturns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "Hello file".getBytes());

        when(service.archiveFiles(any())).thenReturn(new byte[] { 1, 2, 3, 4 });

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file)
                .param("method", "RAR"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        // verify save
        verify(logRepository, times(1)).save(any(RequestData.class));
    }

    @Test
    public void testExceptionCaught500Returned() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "Hello file".getBytes());

        when(service.archiveFiles(any())).thenThrow(new IOException());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
        // verify save
        verify(logRepository, times(1)).save(any(RequestData.class));
    }

}
