package com.SignicatTask.SignicatTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class SignicatTaskApplicationTests {
	@Autowired
    private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	public void testNormalFileUploadAndZipCreationWithMultipleFiles() throws Exception {
        MockMultipartFile firstFile = new MockMultipartFile("files", "file1.xml", "text/plain", "some xml".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("files", "file2.txt", "text/plain", "some text".getBytes());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(firstFile)
                .file(secondFile)
                .param("method", "ZIP"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        byte[] responseBytes = result.getResponse().getContentAsByteArray();

		// Validate zip
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(responseBytes));
		ZipEntry entry = zipStream.getNextEntry();
		assertNotNull(entry, "Expected a zip entry");
		assertEquals("file1.xml", entry.getName());

		entry = zipStream.getNextEntry();
		assertNotNull(entry, "Expected second zip entry");
		assertEquals("file2.txt", entry.getName());

		assertNull(zipStream.getNextEntry(), "No more entries expected");
        
    }
	@Test
	public void testNormalFileUploadAndZipCreationWithOneFile() throws Exception {
        MockMultipartFile firstFile = new MockMultipartFile("files", "file1.xml", "text/plain", "some xml".getBytes());
        

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(firstFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        byte[] responseBytes = result.getResponse().getContentAsByteArray();

		// Validate zip
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(responseBytes));
		ZipEntry entry = zipStream.getNextEntry();
		assertNotNull(entry, "Expected a zip entry");
		assertEquals("file1.xml", entry.getName());
		assertNull(zipStream.getNextEntry(), "No more entries expected");
        
    }

	@Test
	public void testNoFileReturns400() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.multipart("/upload"))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
	}
}

