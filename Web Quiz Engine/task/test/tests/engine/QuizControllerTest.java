package tests.engine;


import engine.JSONEntities.User;
import engine.QuizController;
import engine.database.quizcompletiontable.QuizCompletionService;
import engine.database.quiztable.QuizJPAEntity;
import engine.database.quiztable.QuizService;
import engine.database.usertable.UserService;
import engine.exceptions.BadRegistrationRequestException;
import engine.exceptions.QuizNotFoundException;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


public class QuizControllerTest {

    private QuizService quizService = Mockito.mock(QuizService.class);
    private UserService userService = Mockito.mock(UserService.class);
    private QuizCompletionService quizCompletionService = Mockito.mock(QuizCompletionService.class);

    private final QuizController quizController = new QuizController(quizService, userService, quizCompletionService);

    public QuizControllerTest() {
        quizController.setEncoder(new BCryptPasswordEncoder());
        Mockito.when(userService.isUserExistsByEmail(any(String.class))).thenReturn(false);


    }
    /**
     *
     *
     *
     * User registration tests
     *
     *
     *
     */



    /*
     *
     * VALID email and VALID password tests
     *
     */
    @Test
    public void testRegisterUser_Valid_Expect200_1() {
        User validUser = new User("test@google.com", "qwerty");
        HttpStatus expectedStatus = HttpStatus.OK;

        assertEquals(expectedStatus, quizController.registerUser(validUser).getStatusCode());
    }

    @Test
    public void testRegisterUser_Valid_Expect200_2() {
        User validUser = new User("@.", "qwerty");
        HttpStatus expectedStatus = HttpStatus.OK;

        assertEquals(
                expectedStatus,
                quizController.registerUser(validUser).getStatusCode());
    }


    /*
     *
     * INVALID email tests
     *
     */
    @Test
    public void testRegisterUser_WithNotMatchingEmail_ExpectException1() {
        User invalidUser = new User("test", "qwerty");

        assertThrows(
                BadRegistrationRequestException.class,
                () -> quizController.registerUser(invalidUser));
    }

    @Test
    public void testRegisterUser_WithNotMatchingEmail_ExpectException2() {
        User invalidUser = new User("test@", "qwerty");

        assertThrows(
                BadRegistrationRequestException.class,
                () -> quizController.registerUser(invalidUser));
    }

    @Test
    public void testRegisterUser_WithNotMatchingEmail_ExpectException3() {
        User invalidUser = new User("test.", "qwerty");

        assertThrows(
                BadRegistrationRequestException.class,
                () -> quizController.registerUser(invalidUser));
    }

    @Test
    public void testRegisterUser_WithNotMatchingEmail_ExpectException4() {
        User invalidUser = new User(".@", "qwerty");

        assertThrows(
                BadRegistrationRequestException.class,
                () -> quizController.registerUser(invalidUser));
    }


    /**
     *
     *
     * getQuiz test
     *
     *
     */

    @Test
    public void testGetQuiz_ExpectReturns200() {
        Mockito.when(quizService.isQuizExist(1)).thenReturn(true);
        Mockito.when(quizService.getQuizById(1)).thenReturn(new QuizJPAEntity());

        assertEquals(HttpStatus.OK, quizController.getQuiz(1).getStatusCode());
    }

    @Test
    public void testGetQuiz_ExpectException() {
        Mockito.when(quizService.isQuizExist(1)).thenReturn(false);

        assertThrows(QuizNotFoundException.class,
                () -> quizController.getQuiz(1));
    }


    /**
     *
     *
     * getQuizzes test
     *
     *
     */

    @Test
    public void testGetQuizzesExpect200() {
        int pageIdParam = 1;
        int defaultPageSize = 10;

        Pageable paging = PageRequest.of(pageIdParam, defaultPageSize);
        Mockito.when(quizService.findAll(paging)).thenReturn(null);

        assertEquals(HttpStatus.OK, quizController.getQuizzes(pageIdParam));
    }
}
