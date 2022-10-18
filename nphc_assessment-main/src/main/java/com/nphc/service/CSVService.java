package com.nphc.service;

import static org.springframework.util.StringUtils.hasText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nphc.helper.EmployeeDomain;
import com.nphc.helper.SearchCriteria;
import com.nphc.helper.SearchSpecification;
import com.nphc.model.Employee;
import com.nphc.repository.EmployeeRepository;

@Service
public class CSVService {

	@Autowired
	EmployeeRepository repository;

	@Autowired
	SearchCriteriaParser searchCriteriaParser;

	
	public static String TYPE = "text/csv";
	
	
	public  boolean hasCSVFormat(MultipartFile file) {

		return TYPE.equals(file.getContentType());
	}

	public List<EmployeeDomain> csvToEmployee(InputStream is) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			 CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
						.withIgnoreHeaderCase().withCommentMarker('#').withTrim())) {

			return getEmployeeList(csvParser);
		} catch (IOException e) {
			throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
		}
	}

	private List<EmployeeDomain> getEmployeeList(CSVParser csvParser) throws IOException {
		try {
			Map<String, EmployeeDomain> idCheck = new HashMap<>();
			Iterable<CSVRecord> csvRecords = csvParser.getRecords();
			List<EmployeeDomain> empList = new ArrayList<>();
			for (CSVRecord csvRecord : csvRecords) {

				EmployeeDomain employee = new EmployeeDomain();

				employee.setId(csvRecord.get("Id"));
				employee.setLogin(csvRecord.get("Login"));
				employee.setName(csvRecord.get("Name"));
				employee.setSalary(
						hasText(csvRecord.get("Salary")) ? 
								new BigDecimal(csvRecord.get("Salary")).setScale(2, RoundingMode.FLOOR) :
									BigDecimal.ZERO);
				employee.setStartDate(csvRecord.get("startDate"));

				uploadValidation(employee);

				if (idCheck.containsKey(employee.getId())) {
					throw new RuntimeException("Bad Input - Duplicate Row");
				}
				if (idCheck.containsKey(employee.getLogin())) {
					throw new RuntimeException("Bad Input - Duplicate login name");
				}
				idCheck.put(employee.getId(), employee);
				idCheck.put(employee.getLogin(), employee);
				empList.add(employee);
			}

			return empList;
		} catch (RuntimeException e) {
			throw new RuntimeException("Bad Input - " + e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void save(MultipartFile file) {
		try {
			List<EmployeeDomain> empDomainList = csvToEmployee(file.getInputStream());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			List<Employee> empList = new ArrayList<>();
			for (EmployeeDomain d : empDomainList) {
				Employee emp = new Employee();
				emp.setId(d.getId());
				emp.setLogin(d.getLogin());
				emp.setName(d.getName());
				emp.setSalary(d.getSalary());
				emp.setStartDate(LocalDate.parse(d.getStartDate(), formatter));
				empList.add(emp);
			}
			repository.saveAllAndFlush(empList);
		} catch (IOException e) {
			throw new RuntimeException("fail to store csv data: " + e.getMessage());
		}
	}

	public List<EmployeeDomain> getAllEmployees() {
		List<Employee> empList = repository.findAll();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		return empList.stream().map(e -> {
			EmployeeDomain emp = new EmployeeDomain();
			emp.setId(e.getId());
			emp.setLogin(e.getLogin());
			emp.setName(e.getName());
			emp.setSalary(e.getSalary().setScale(2, RoundingMode.FLOOR));
			emp.setStartDate(e.getStartDate().format(formatter));
			return emp;

		}).collect(Collectors.toList());

	}

	public EmployeeDomain findById(String id) {

		Optional<Employee> emp = repository.findById(id);

		if (emp.isPresent()) {
			Employee empObj = emp.get();
			return empMapper(empObj);
		}

		return null;
	}

	public void deleteById(String id) {
		repository.deleteById(id);
	}

	public boolean save(EmployeeDomain domain) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("[yyyy-MM-dd][dd-MMM-yy]");
			Employee emp = new Employee();
			emp.setId(domain.getId());
			emp.setLogin(domain.getLogin());
			emp.setName(domain.getName());
			emp.setSalary(domain.getSalary());
			emp.setStartDate(LocalDate.parse(domain.getStartDate(), formatter));
			repository.save(emp);
			return true;
		} catch (Exception e) {
			throw new RuntimeException("fail to save  " + e.getMessage());
		}
	}

	private EmployeeDomain empMapper(Employee e) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		EmployeeDomain emp = new EmployeeDomain();
		emp.setId(e.getId());
		emp.setLogin(e.getLogin());
		emp.setName(e.getName());
		emp.setSalary(e.getSalary().setScale(2, RoundingMode.FLOOR));
		emp.setStartDate(e.getStartDate().format(formatter));
		return emp;
	}

	private boolean hasLogin(String login) {

		long count;
		count = repository.countByLogin(login);
		return count < 1;
	}
	
	public void uploadValidation(EmployeeDomain e) {

		if (!hasText(e.getLogin())) {
			throw new RuntimeException("Invalid login");
		}

		
		if (!hasText(e.getName())) {
			throw new RuntimeException("Invalid name");
		}
		
		if (e.getSalary().compareTo(BigDecimal.ZERO) == 0) {
			throw new RuntimeException("Invalid salary");
		}


		if (e.getStartDate() == null) {
			throw new RuntimeException("Invalid date");
		}
		
		DateTimeFormatter formatter = DateTimeFormatter
				  .ofPattern("[yyyy-MM-dd][dd-MMM-yy]");
		try {
		e.setStartDate(LocalDate.parse(e.getStartDate(), formatter).toString());
		}
		catch(Exception e1) {
			throw new RuntimeException("Invalid date format");
		}
		
	}

	public void validation(EmployeeDomain e) {

		if (!hasText(e.getId())) {
			throw new RuntimeException("Invalid Employee id");
		}

		boolean employeeId = repository.existsById(e.getId());

		if (employeeId) {
			throw new RuntimeException("Employee ID already exists");
		}

		if (!hasText(e.getLogin())) {
			throw new RuntimeException("Invalid login");
		}
		boolean loginExist = hasLogin(e.getLogin());

		if (!loginExist) {
			throw new RuntimeException("Employee Login not Unique");
		}

		uploadValidation(e);
		
		

	}
	
	public void updateValidation(EmployeeDomain e) {

		if (!hasText(e.getLogin())) {
			throw new RuntimeException("Invalid login");
		}
		boolean loginExist = hasLogin(e.getLogin());

		if (!loginExist) {
			throw new RuntimeException("Employee Login not Unique");
		}

		if (!hasText(e.getName())) {
			throw new RuntimeException("Invalid name");
		}


		if (e.getSalary().compareTo(BigDecimal.ZERO) == 0) {
			throw new RuntimeException("Invalid salary");
		}

	}

	public List<EmployeeDomain> search(String searchString, Integer pageSize, Integer pageIndex) {
		List<SearchCriteria> searchCriteria = searchCriteriaParser.parse(searchString);
		List<SearchSpecification> specList = searchCriteria.stream()
				.map(SearchSpecification::new).collect(Collectors.toList());
		Specification<Employee> specs = andSpecification(specList)
				.orElseThrow(() -> new IllegalArgumentException("No criteria provided"));
		List<Sort> sortList = generateSortList(searchCriteria);
		Sort sort = andSort(sortList).orElse(Sort.unsorted());
		Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
		Page<Employee> page = repository.findAll(specs, pageable);
		return page.getContent().stream().map(e -> {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			EmployeeDomain emp = new EmployeeDomain();
			emp.setId(e.getId());
			emp.setLogin(e.getLogin());
			emp.setName(e.getName());
			emp.setSalary(e.getSalary().setScale(2, RoundingMode.FLOOR));
			emp.setStartDate(e.getStartDate().format(formatter));
			return emp;

		}).collect(Collectors.toList());
	}

	private <T, V extends Specification<T>> Optional<Specification<T>> andSpecification(List<V> criteria) {
		Iterator<V> itr = criteria.iterator();
		if (itr.hasNext()) {
			Specification<T> spec = Specification.where(itr.next());
			while (itr.hasNext()) {
				spec = spec.and(itr.next());
			}
			return Optional.of(spec);
		}
		return Optional.empty();
	}

	private List<Sort> generateSortList(List<SearchCriteria> criteria) {
		return criteria.stream().map((criterion) -> {
			switch (criterion.getOperation()) {
			case SORT_ASC:
				return Sort.by(Order.asc(criterion.getKey()));
			case SORT_DESC:
				return Sort.by(Order.desc(criterion.getKey()));
			default:
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private <V extends Sort> Optional<Sort> andSort(List<V> criteria) {

		Iterator<V> itr = criteria.iterator();
		if (itr.hasNext()) {
			Sort sort = (itr.next());
			while (itr.hasNext()) {
				sort = sort.and(itr.next());
			}
			return Optional.of(sort);
		}
		return Optional.empty();
	}
}
