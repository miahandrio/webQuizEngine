# webQuizEngine

### In this project, I've used quite a lot of Spring parts(Spring Boot, Spring Data, Spring Security)

It is a backend engine for a quiz website in wich you can create, solve, share quizzes and much more.

It supports creating accounts, authorization (quizzes can be deleted only by their creators), and logging quiz solving.
The local PostgreSQL DBMS is used, but it can be configured to work with the H2 database(UPD now primary DB for this repo) Find the configuration at the bottom of this readme.
This project uses a Gradle build/dependency management system and java 11+ SDK, Spring framework version is specified as the newest.

For accessing API, web UI should be coming in the following weeks, but now, using it with the postman(or similar software is recommended).

## NOTE
The WebQuizTestEngine test is currently working only if you have no table entries, new test file coming soon!

## Authorization
For any request through /quizzes endpoints, authentification is required. This project uses HTTP basic authentification.

## Endpoints:

There are 7 endpoints, but only the registration endpoint is accessible without authorization.

### POST /api/register
This mapping consumes a JSON body(User entity)
Request body should look like this:
```
{
"email": "sample@email.com"
"password": "samplePassword"
}
```

minimum password length and email regex constraints are applied.



### GET /api/quizzes
This Get mapping is used to display existing quizzes, it has pagination implemented so that quizzes are displayed in pages consisting of 10 entries
Sample requests:
```
localhost:8080/api/quizzes?page=0
localhost:8080/api/quizzes
```



### GET /api/quizzes/{id}
This mapping allows to extract a quiz with a certain id from a database 
Sample request:
```
localhost:8080/api/quizzes/3
```



### GET /api/quizzes/completed
This mapping shows what quizzes the authenticated user has successfully solved and the time of completion. 



### POST /api/quizzes
This mapping consumes a JSON object of a QuizJPAEntity, creates a quiz, and adds it to the database
Sample input:
```
{
"title": "sample title",
"text": "sample quiz"
"options": ["option 1", "option 2", "option 3", "option 4"]
"answer": [1,3]
}
```
the not blank title, text, and at least 2 elements in options are required


### POST /api/quizzes/{id}/solve
This mapping allows us to solve a quiz by sending an answer entity in the body
Sample entry:
```
localhost:8080/api/quizzes/3/solve
```

Sample input:
```
{
"answer": [1,2]
}
```



### DELETE /api/quizzes/{id}
This mapping allows deleting specified quizzes that the current authenticated user created, you can not delete quizzes of other users.
Sample entry:
```
localhost:8080/api/quizzes/2
```


# Database configuration
## Overall configuration
For configuring the postgreSQL database you need to update these parameters to these values in 'application.properties' file in 'Web Quiz Engine\src\resouces folder':
```
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/yourDatabase
spring.datasource.username=yourUsername
spring.datasource.password=YourPassword

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL10Dialect
```
### Online database connectivity coming soon



## H2 configuration
For H2 database configuration, change these parameters to these values in 'application.properties' file in 'Web Quiz Engine\src\resouces folder':
```
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.url=jdbc:h2:file:./testdb
spring.datasource.username=username
spring.datasource.password=password

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```
