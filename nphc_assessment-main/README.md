## Overview
The following repo contains examples for OpenFin's Java adapter.

## Guidelines
Clone the repo:

Git:

$ git clone https://github.com/sssudharsan/nphc_test.git

Or download a ZIP of main manually and expand the contents someplace on your system

Prerequisites
Have a JDK 11 installed
Have Maven installed and available on your PATH or IDE
Have postman installed for testing
Verify installation
You can verify the project builds correctly from your IDE or from the command line.

CLI
Open a command prompt and verify that all needed bins are on your path and up to date:

$ java -version
# should be at least 11
$ mvn -version
# should be at least 3.0.0
Finally, verify that mvn install succeeds.

## Run Spring Boot application
```
mvn spring-boot:run
```


Payload:   
  
1) Upload API      
  
Request URL         : localhost:8080/users/upload    
Request Method      : POST    
Request Content Type: multipart/form-data    


2) Fetch List of Employees     
 
Note: I've coded a custom search. Payloads have been a little different, so follow the instructions below to search for employees.      

Request URL : localhost:8080/users     
Method      : GET      




switch (input) {   
case ":":    
return EQUALITY;     
case "!":     
return NEGATION;     
case ">":     
return GREATER_THAN;     
case "<":      
return LESS_THAN;    
case ">=":      
return GREATHER_THAN_EQUAL;     
case "<=":    
return LESSER_THAN_EQUAL;     
case "%":    
return LIKE;    
case "+":    
case " ": // + is encoded in query strings as a space    
return SORT_ASC;    
case "-":    
return SORT_DESC;    
default:    
return null;    
}   

1) localhost:8080/users/?search=salary>=0,salary>4000&pageindex=10&pagesize=0  
Employees displays salary between 0 to 4000   
2) localhost:8080/users/?search=id:e0001&pageindex=10&pagesize=0   
   Employees displays id = e0001   
3) localhost:8080/users/?search=id!e0001&pageindex=10&pagesize=0    
   Employees displays id not equal to e0001      
4) localhost:8080/users/?search=id!e0001,id-&pageindex=10&pagesize=0    
    Employees displays id not equal to e0001 and desc order    
   

3) CRUD   

Can perform CRUD operations with respective payload   

Create:
Request URL         : localhost:8080/users   
Request Method      : POST    
Request Content Type: application/json    

Sample Payload   
{     
"id": "emp0001",    
"name": "Harry Potter",    
"login": "hpotter",    
"salary": 1234.00,    
"startDate": "2001-11-16"    
}    


Update:   
Request URL         : localhost:8080/users   
Request Method      : PUT   
Request Content Type: application/json    

Sample payload    
{    
"name": "Harry Potter",    
"login": "hpotter",    
"salary": 1234.00,    
}    



Get:    
Request URL         : localhost:8080/users   
Request Method      : GET    

Payload: localhost:7000/users/e0001    

Delete:   
Request URL         : localhost:8080/users    
Request Method      : delete    

Payload: localhost:7000/users/e0001    
