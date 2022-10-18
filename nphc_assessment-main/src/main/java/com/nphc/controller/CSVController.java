package com.nphc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nphc.helper.EmployeeDomain;
import com.nphc.helper.EmployeeResponseList;
import com.nphc.service.CSVService;

@RestController
@RequestMapping("/users")
public class CSVController {

	@Autowired
	CSVService fileService;

	@PostMapping("/upload")
	public ResponseEntity<EmployeeResponseList<String>> uploadFile(@RequestParam("file") MultipartFile file) {
		String message = "";

		if (fileService.hasCSVFormat(file)) {
			try {
				fileService.save(file);

				message = "Data Created or uploaded";
				return ResponseEntity.status(HttpStatus.CREATED).body(new EmployeeResponseList<String>(message));
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new EmployeeResponseList<String>(e.getMessage()));
			}
		}

		message = "Please upload a csv file!";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new EmployeeResponseList<String>(message));
	}

	@GetMapping(path = "/allEmployees")
	public ResponseEntity<EmployeeResponseList<?>> getAllEmployees() {
		try {
			List<EmployeeDomain> empList = fileService.getAllEmployees();

			if (empList.isEmpty()) {
				return ResponseEntity.status(HttpStatus.OK).body(new EmployeeResponseList<String>("No such employee"));
			}

			return ResponseEntity.status(HttpStatus.OK).body(new EmployeeResponseList<List<EmployeeDomain>>(empList));
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<EmployeeResponseList<String>> updateEmployee(@PathVariable(value = "id") String employeeId,
			@RequestBody EmployeeDomain employeeDetails) {
		try {
			EmployeeDomain employee = fileService.findById(employeeId);

			if (employee == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new EmployeeResponseList<String>("No Such Employee"));
			}

			fileService.updateValidation(employeeDetails);

			employee.setName(employeeDetails.getName());
			employee.setSalary(employeeDetails.getSalary());
			employee.setLogin(employeeDetails.getLogin());
			fileService.save(employee);
			return ResponseEntity.status(HttpStatus.OK).body(new EmployeeResponseList<String>("Successfully Updated"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new EmployeeResponseList<String>(e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<EmployeeResponseList<String>> deleteEmployee(@PathVariable(value = "id") String employeeId) {
		EmployeeDomain employee = fileService.findById(employeeId);
		if (employee == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new EmployeeResponseList<String>("No such employee"));
		}
		fileService.deleteById(employeeId);
		return ResponseEntity.status(HttpStatus.OK).body(new EmployeeResponseList<String>("Successfully deleted"));
	}

	@GetMapping
	public ResponseEntity<EmployeeResponseList<?>> search(@RequestParam("search") String search,
														  @RequestParam(value = "pagesize", required = false, defaultValue = "50")  Integer pageSize,
														  @RequestParam(value = "pageindex", required = false, defaultValue = "0") Integer pageIndex) {
		try {
			List<EmployeeDomain> mainObjList = fileService.search(search, pageSize, pageIndex);
			if (mainObjList.isEmpty()) {
				return ResponseEntity.status(HttpStatus.OK).body(new EmployeeResponseList<String>("No result found"));
			}
			return ResponseEntity.status(HttpStatus.OK)
					.body(new EmployeeResponseList<List<EmployeeDomain>>(mainObjList));
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeResponseList<String>> create(@RequestBody EmployeeDomain obj) {
		String message = "";
		try {
			fileService.validation(obj);
			fileService.save(obj);
			message = "New employee record created";
			return ResponseEntity.status(HttpStatus.CREATED).body(new EmployeeResponseList<String>(message));
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new EmployeeResponseList<String>(ex.getMessage()));
		} catch (Exception ex) {
			message = "Bad Input - ";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new EmployeeResponseList<String>(message + ex.getMessage()));
		}
	}


	@GetMapping("/{id}")
	public ResponseEntity<EmployeeResponseList<?>> getEmployee(@PathVariable(value = "id") String employeeId) {
		try {
			EmployeeDomain employee = fileService.findById(employeeId);

			if (employee == null) {
				return ResponseEntity.status(HttpStatus.OK).body(new EmployeeResponseList<String>("No such employee"));
			}

			return ResponseEntity.status(HttpStatus.OK).body(new EmployeeResponseList<>(employee));
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
