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
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.*;


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

    @PostMapping(path="/api/register", consumes = "application/json")
    public HttpStatus registerUser(@RequestBody User user) {
        if (user.getEmail().matches(".*@.*\\..*")
            && user.getPassword().length() >= 5
            && !userService.isUserExistsByEmail(user.getEmail()))
        {
            userService.saveUser(
                    new UserJPAEntity(user.getEmail(),
                            encoder.encode(user.getPassword()),
                            new SimpleGrantedAuthority("ROLE_USER"))
            );
            return HttpStatus.OK;
        } else {
            throw new BadRegistrationRequestException("Bad registration request");
        }
    }






    /*

    Quiz operations

     */

    /*
    Create a new quiz
     */
    @GetMapping(value = "/api/quizzes/{id}", produces = "application/json")
    public QuizJPAEntity getQuiz(@PathVariable int id) {
        if (quizService.isQuizExist(id)) {
            return quizService.getQuizById(id);
        }
        throw new QuizNotFoundException("Quiz with this id is not found");
    }



//    @GetMapping(value = "/api/quizzes", produces = "application/json")
//    public ResponseEntity<Iterable<QuizJPAEntity>> getQuizzes() {
//        return new ResponseEntity<>(quizService.getAllQuizzes(), HttpStatus.OK);
//    }

    @GetMapping(value = "/api/quizzes", produces = "application/json")
    public ResponseEntity<Page<QuizJPAEntity>> getQuizzes(
            @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;

        Pageable paging = PageRequest.of(page, pageSize);

        Page<QuizJPAEntity> pagedResult = quizService.findAll(paging);

        return new ResponseEntity<>(pagedResult, HttpStatus.OK);
    }

    @GetMapping(value = "/api/quizzes/completed", produces = "application/json")
    public ResponseEntity<Page<QuizCompletionEntity>> getQuizzescompletion(
            @RequestParam(defaultValue = "0") int page,
            Authentication auth) {
        UserJPAEntity user = (UserJPAEntity) auth.getPrincipal();

        int pageSize = 10;

        Pageable paging = PageRequest.of(page, pageSize, Sort.by("completedAt").descending());

        Page<QuizCompletionEntity> pagedResult = quizCompletionService.findQuizCompletionEntitiesByUserId(paging, user);

        return new ResponseEntity<>(pagedResult, HttpStatus.OK);
    }




    @PostMapping(value = "/api/quizzes", consumes = "application/json", produces = "application/json")
    public QuizJPAEntity createQuiz (@Valid @RequestBody QuizJPAEntity quiz, Authentication auth) {
        if (quiz.getAnswer() == null) {
            quiz.setAnswer(new int[0]);
        }
        UserJPAEntity user = (UserJPAEntity) auth.getPrincipal();
        quiz.setUser(user);
        return quizService.saveQuiz(quiz);
    }



    @PostMapping(value = "/api/quizzes/{id}/solve", produces = "application/json")
    public ServerFeedback solveQuiz(@PathVariable int id, @RequestBody(required = false) Answer answerObject, Authentication auth) {
        int[] answer = answerObject.getAnswer();

        if (quizService.isQuizExist(id)) {
            QuizJPAEntity quiz = quizService.getQuizById(id);
            if (Arrays.equals(quiz.getAnswer(), answer)) {
                UserJPAEntity user = (UserJPAEntity) auth.getPrincipal();
                System.out.println("\n\n\n\n\n\n\n\n\nuser id: " + user.getId() + " solved quiz: " + id + "\n\n\n\n\n\n\n\n\n");
                quizCompletionService.saveQuizCompletion(
                        new QuizCompletionEntity(
                                quiz,
                                user,
                                new Timestamp(System.currentTimeMillis())
                        )
                );
                return new ServerFeedback(true, "Congratulations, you're right!");
            }
            return new ServerFeedback(false, "Wrong answer! Please, try again.");
        }
        throw new QuizNotFoundException("Quiz with this id is not found");
    }

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
}
