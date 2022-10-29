package engine;

import engine.JSONEntities.Answer;
import engine.JSONEntities.ServerFeedback;
import engine.JSONEntities.User;
import engine.database.quizcompletiontable.QuizCompletionEntity;
import engine.database.quizcompletiontable.QuizCompletionService;
import engine.database.quiztable.QuizJPAEntity;
import engine.database.usertable.UserJPAEntity;
import engine.database.usertable.UserService;
import engine.exceptions.BadRegistrationRequestException;
import engine.exceptions.CustomErrorMessage;
import engine.exceptions.QuizNotFoundException;
import engine.database.quiztable.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.*;


/**
 * This class is the main controller of the application. It handles all the requests and responses.
 * It also handles the database operations.
 * It uses the QuizService, QuizCompletionService and UserService classes to access the database.
 * It has 7 endpoints, 1(registration) is available for everyone, 6 are available for authenticated users.
 *
 * @author Mykhailo Bubnov
 * @version 0.1
 */
@RestController
public class QuizController {

    QuizService quizService;

    QuizCompletionService quizCompletionService;

    UserService userService;

    PasswordEncoder encoder;


    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public QuizController(@Autowired QuizService quizService, @Autowired UserService userService, @Autowired QuizCompletionService quizCompletionService) {
        this.quizService = quizService;
        this.userService = userService;
        this.quizCompletionService = quizCompletionService;
    }


    /**
     * This method handles the registration request.
     * It checks if the user already exists in the database and if email and password are valid.
     * If the user does not exist and password and email are valid, it creates a new user and saves it to the database.
     * If something doesn't fit, it throws a BadRegistrationRequestException.
     * @param user email and password
     * @return ResponseEntity with the status code 200 and the message "You have successfully registered".
     */
    @PostMapping(path="/api/register", consumes = "application/json")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        if (user.getEmail().matches(".*@.*\\..*")
            && !userService.isUserExistsByEmail(user.getEmail()))
        {
            userService.saveUser(
                    new UserJPAEntity(user.getEmail(),
                            encoder.encode(user.getPassword()),
                            new SimpleGrantedAuthority("ROLE_USER"))
            );
            return new ResponseEntity<>("Registration is successful", HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>("Registration is unsuccessful", HttpStatus.BAD_REQUEST);
        }
    }


    /*
      Quiz Operations
     */


    /**
     * This mapping allows getting a certain quiz with its id.
     * Throws a QuizNotFoundException if the quiz with the given id does not exist.
     * @param id, entered as a path variable
     * @return ResponseEntity with the status code 200 and the quiz in the body.
     */
    @GetMapping(value = "/api/quizzes/{id}", produces = "application/json")
    public ResponseEntity<QuizJPAEntity> getQuiz(@PathVariable int id) {
        if (quizService.isQuizExist(id)) {
            return new ResponseEntity<>(quizService.getQuizById(id),
                    HttpStatus.OK);
        }
        throw new QuizNotFoundException("Quiz with this id is not found");
    }


    /**
     * This mapping allows getting all the quizzes from the database.
     * Quizzes are organised in pages, each page has 10 quizzes.
     * @param page page number
     * @return ResponseEntity with the status code 200 and the list of quizzes in the body.
     */
    @GetMapping(value = "/api/quizzes", produces = "application/json")
    public ResponseEntity<Page<QuizJPAEntity>> getQuizzes(
            @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;

        Pageable paging = PageRequest.of(page, pageSize);

        Page<QuizJPAEntity> pagedResult = quizService.findAll(paging);

        return new ResponseEntity<>(pagedResult, HttpStatus.OK);
    }

    /**
     * This mapping allows getting all quiz completion entities from the database for authenticated user.
     * They contain the date of completion and the id of the quiz.
     * Quiz completion entities are organised in pages, each page has 10 quiz completion entities.
     * @param page page number
     * @param auth authentication object, autowired.
     * @return ResponseEntity with the status code 200 and the list of quiz completion entities in the body.
     */
    @GetMapping(value = "/api/quizzes/completed", produces = "application/json")
    public ResponseEntity<Page<QuizCompletionEntity>> getQuizzesCompletion(
            @RequestParam(defaultValue = "0") int page,
            Authentication auth) {
        UserJPAEntity user = (UserJPAEntity) auth.getPrincipal();

        int pageSize = 10;

        Pageable paging = PageRequest.of(page, pageSize, Sort.by("completedAt").descending());

        Page<QuizCompletionEntity> pagedResult = quizCompletionService.findQuizCompletionEntitiesByUserId(paging, user);

        return new ResponseEntity<>(pagedResult, HttpStatus.OK);
    }


    /**
     * This mapping allows creating a quiz.
     * It consumes a quiz object in the body of the request.
     * Sample request:
     * {
     *     "title": "The Java Logo",
     *     "text": "What is depicted on the Java logo?",
     *     "options": ["Robot", "Tea leaf", "Cup of coffee", "Bug"],
     *     "answer": [2,3]
     * }
     * @param quiz quiz object in the body of the request
     * @param auth authentication object, autowired.
     * @return ResponseEntity with the status code 200 and the created quiz in the body.
     */
    @PostMapping(value = "/api/quizzes", consumes = "application/json", produces = "application/json")
    public ResponseEntity<QuizJPAEntity> createQuiz (@Valid @RequestBody QuizJPAEntity quiz, Authentication auth) {
        if (quiz.getAnswer() == null) {
            quiz.setAnswer(new int[0]);
        }
        UserJPAEntity user = (UserJPAEntity) auth.getPrincipal();
        quiz.setUser(user);
        return new ResponseEntity<>(quizService.saveQuiz(quiz), HttpStatus.CREATED);
    }


    /**
     * This mapping allows solving a quiz.
     * @param id id of the quiz as the path variable.
     * @param answerObject object with the answer in the body of the request, sample : {"answer": [2,3]}
     * @param auth authentication object, autowired.
     * @return ResponseEntity with either the congratulation message or the try again message.
     */
    @PostMapping(value = "/api/quizzes/{id}/solve", produces = "application/json")
    public ResponseEntity<ServerFeedback> solveQuiz(@PathVariable int id, @RequestBody Answer answerObject, Authentication auth) {
        int[] answer = answerObject.getAnswer();

        if (quizService.isQuizExist(id)) {
            QuizJPAEntity quiz = quizService.getQuizById(id);
            if (Arrays.equals(quiz.getAnswer(), answer)) {
                UserJPAEntity user = (UserJPAEntity) auth.getPrincipal();
                quizCompletionService.saveQuizCompletion(
                        new QuizCompletionEntity(
                                quiz,
                                user,
                                new Timestamp(System.currentTimeMillis())
                        )
                );
                return new ResponseEntity<>(new ServerFeedback(true, "Congratulations, you're right!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ServerFeedback(false, "Wrong answer! Please, try again."), HttpStatus.BAD_REQUEST);
        }
        throw new QuizNotFoundException("Quiz with this id is not found");
    }

    /**
     * This mapping allows deleting a quiz with a certain id.
     * Only the user that created the quiz can delete it.
     * @param id id of the quiz as the path variable.
     * @param auth authentication object, autowired.
     * @return ResponseEntity with the status code 204.
     */
    @DeleteMapping(value = "/api/quizzes/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable int id, Authentication auth) {
        UserJPAEntity user = (UserJPAEntity) auth.getPrincipal();
        if (quizService.isQuizExist(id)) {
            if (quizService.getQuizById(id).getUser().getId().equals(user.getId())) {
                quizService.deleteQuizById(id);
                return new ResponseEntity<>("Quiz deleted successfully", HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>("Only the owners of quizzes can delete them", HttpStatus.FORBIDDEN);
        }
        throw new QuizNotFoundException("Quiz with this id is not found");
    }


    /**
     * Bean that generates a password encoder.
     * It is used in the registration process.
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @ExceptionHandler(QuizNotFoundException.class)
    public ResponseEntity<CustomErrorMessage> handleQuizNotFoundException(QuizNotFoundException e) {
        return new ResponseEntity<>(
                new CustomErrorMessage(HttpStatus.NOT_FOUND.value(),
                        e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRegistrationRequestException.class)
    public ResponseEntity<CustomErrorMessage> handleBadRegistrationRequestException(BadRegistrationRequestException e) {
        return new ResponseEntity<>(
                new CustomErrorMessage(HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(
                new CustomErrorMessage(HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
