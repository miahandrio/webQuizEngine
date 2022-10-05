# webQuizEngine

### In this project I've used quiet a lot of Spring parts(Spring Boot, Spring Data, Spring Security)

It is a backend engine for a quiz website in wich you can create, solve, share quizzes and much more.

It supports creating accounts, authorisation(quizzes can be deleted only by their creators), logging quiz solving.
The local PostgreSQL DBMS is used, but it can be configured to work with H2 database(find configuration at the bottom of this readme)

For accessing api, web UI should be comin in the following weeks, but now, using it with postman(or similar software is recommended).

## NOTE
The WebQuizTestEngine test is currently working only if you have no table entries.

## Authorisation
For any request through /quizzes endpoints, the authentification is required. This project uses http basic authentification.

## Endpoints:

There are 7 endpoints, but without authorisation, only the registration endpoint is accessible. 

### POST /api/register
This mapping consumes a JSON body(User entity)
Request body should look like this:
```
{
"email": "sample@email.com"
"password": "samplePassword"
}
```

minimum password length and email regex constrains are applied.


### GET /api/quizzes

This Get mapping is used to display existing quizzes, it has a pagation implemented so that quizzes are displayed in pages consisting of 10 entries
Sample requests:
```
localhost:8080/api/quizzes?page=0
localhost:8080/api/quizzes
```

### GET /api/quizzes/{id}
This mapping allowes to extract a quiz with certain id from a database
Sample request:
```
localhost:8080/api/quizzes/3
```


### GET /api/quizzes/completed
This mapping shows what quizzes the authentificated user has successfully solved and time of completion.

### POST /api/quizzes
This mapping consumes a JSON object of a QuizJPAEntity
