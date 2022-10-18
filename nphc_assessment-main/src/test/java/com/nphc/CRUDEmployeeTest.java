package com.nphc;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nphc.helper.EmployeeDomain;
import com.nphc.repository.EmployeeRepository;
import com.nphc.service.CSVService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.AssertionErrors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

@SpringBootTest(classes = Application.class)
public class CRUDEmployeeTest {

	@Autowired
	CSVService service;

	@Autowired
	EmployeeRepository dao;

    @Autowired
    private ResourceLoader loader;

    @BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void whenSaveEmployee() throws IOException {

		Resource resource = loader.getResource(CLASSPATH_URL_PREFIX + "data/add.json");

		File file = resource.getFile();
		String payload = Files.readString(file.toPath());
		Gson GSON = new GsonBuilder().create();
		var domain = GSON.fromJson(payload, EmployeeDomain.class);

		AssertionErrors.assertTrue("sucessfully saved",service.save(domain));

	}

	@Test
	public void whenGetEmployee()  {

    	var domain = service.findById("e0001");

		AssertionErrors.assertNotNull("Employee Not found",domain);



	}

	@Test
	public void whenPutEmployee() throws IOException {

		var employee = service.findById("emp0023");

		AssertionErrors.assertNotNull("Employee Not found",employee);

		Resource resource = loader.getResource(CLASSPATH_URL_PREFIX + "data/update.json");

		File file = resource.getFile();
		String payload = Files.readString(file.toPath());
		Gson GSON = new GsonBuilder().create();
		var employeeDetails = GSON.fromJson(payload, EmployeeDomain.class);

		service.updateValidation(employeeDetails);
		employee.setName(employeeDetails.getName());
		employee.setSalary(employeeDetails.getSalary());
		employee.setLogin(employeeDetails.getLogin());

		AssertionErrors.assertTrue("Updated successfully",service.save(employeeDetails));

	}

	@Test
	public void whenDeleteEmployee()  {

		var domain = service.findById("emp0023");

		AssertionErrors.assertNotNull("Employee Not found",domain);

		service.deleteById("emp0023");

    	var test = service.findById("emp0023");

		assertThat(test).isNull();



	}



}
