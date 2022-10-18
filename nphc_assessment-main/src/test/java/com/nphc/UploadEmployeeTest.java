package com.nphc;


import static org.mockito.Mockito.when;
import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

import java.io.FileInputStream;
import java.io.IOException;


import com.nphc.helper.EmployeeDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.nphc.repository.EmployeeRepository;
import com.nphc.service.CSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(classes = Application.class)
public class UploadEmployeeTest {

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
	public void updateEmployeesTest() throws IOException {

		when(service.findById(Mockito.any())).thenReturn(new EmployeeDomain());

        Resource resource = loader.getResource(CLASSPATH_URL_PREFIX + "data/test.csv");

        MultipartFile multipartFile = new MockMultipartFile("test.csv", new FileInputStream(resource.getFile()));

        service.save(multipartFile);
	}

}
