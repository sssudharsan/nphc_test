package com.nphc;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nphc.helper.EmployeeDomain;
import com.nphc.repository.EmployeeRepository;
import com.nphc.service.CSVService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

@SpringBootTest(classes = Application.class)
public class CreateEmployeeTest {

	@InjectMocks
	CSVService service;

	@Mock
	EmployeeRepository dao;

    @Autowired
    private ResourceLoader loader;

    @BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void saveEmployeesTest() throws IOException {

        Resource resource = loader.getResource(CLASSPATH_URL_PREFIX + "data/add.json");

        File file = resource.getFile();
		String payload = Files.readString(file.toPath());
		Gson GSON = new GsonBuilder().create();
		var domain = GSON.fromJson(payload, EmployeeDomain.class);
        service.save(domain);
	}
	
}
