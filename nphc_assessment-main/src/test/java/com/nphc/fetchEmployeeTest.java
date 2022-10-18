package com.nphc;


import com.nphc.repository.EmployeeRepository;
import com.nphc.service.CSVService;
import com.nphc.service.SearchCriteriaParser;
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

import java.io.FileInputStream;
import java.io.IOException;

import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

@SpringBootTest(classes = Application.class)
public class fetchEmployeeTest {

	@Autowired
	CSVService service;

	@Autowired
	SearchCriteriaParser searchCriteriaParser;

    @BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void fetchEmployeesTest() throws IOException {

		String search = "id:emp0023";
		Integer pageSize = 5;
		Integer pageIndex = 0;
		service.search(search, pageSize, pageIndex);
	}
	
}
