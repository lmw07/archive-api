package com.SignicatTask.SignicatTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
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

import com.SignicatTask.SignicatTask.Repository.LogRepository;
import com.SignicatTask.SignicatTask.Repository.RequestData;

@SpringBootTest
@AutoConfigureMockMvc
class SignicatTaskApplicationTests {
	@Autowired
    private MockMvc mockMvc;

	@Autowired
	LogRepository repo;

	@Test
	void contextLoads() {
	}

	@Test
	public void testNormalFileUploadAndZipCreationWithMultipleFiles() throws Exception {
		repo.deleteAll();
        MockMultipartFile file1 = new MockMultipartFile("files", "file1.xml", "text/plain", "some xml".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "file2.txt", "text/plain", "some text".getBytes());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file1)
                .file(file2)
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
		repo.deleteAll();
        
    }
	@Test
	public void testNormalFileUploadAndZipCreationWithOneFile() throws Exception {
		repo.deleteAll();
        MockMultipartFile file1 = new MockMultipartFile("files", "file1.xml", "text/plain", "some xml".getBytes());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                .file(file1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        byte[] responseBytes = result.getResponse().getContentAsByteArray();

		// Validate zip
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(responseBytes));
		ZipEntry entry = zipStream.getNextEntry();
		assertNotNull(entry, "Expected a zip entry");
		assertEquals("file1.xml", entry.getName());
		assertNull(zipStream.getNextEntry(), "No more entries expected");
        repo.deleteAll();
    }

	@Test
	public void testNoFileReturns400() throws Exception{
		repo.deleteAll();
		mockMvc.perform(MockMvcRequestBuilders.multipart("/upload"))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		repo.deleteAll();
	}


	@Test
	public void testRepositoryLogsSuccessfulRequests() throws Exception {
		repo.deleteAll();
		MockMultipartFile file1 = new MockMultipartFile("files", "file1.xml", 
		"text/plain", new byte[1024*1024/2]);
		mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
		.file(file1)).andExpect(MockMvcResultMatchers.status().isOk());
		assertEquals(1, repo.count());
		var out = repo.findAll();
		assertTrue(out.get(0).date.equals(LocalDate.now()));
		assertEquals(RequestData.Status.SUCCESS, out.get(0).status);
		repo.deleteAll();
	}
	
}

