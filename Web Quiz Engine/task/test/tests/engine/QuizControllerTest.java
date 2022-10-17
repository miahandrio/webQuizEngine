package tests.engine;


import engine.JSONEntities.User;
import engine.QuizController;
import engine.database.quizcompletiontable.QuizCompletionService;
import engine.database.quiztable.QuizService;
import engine.database.usertable.UserService;
import engine.exceptions.BadRegistrationRequestException;
import org.junit.Test;
import org.mockito.Mockito;
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
    public void testRegisterUserValid_Expect200_1() {
        User validUser = new User("test@google.com", "qwerty");
        HttpStatus expectedStatus = HttpStatus.OK;

        assertEquals(expectedStatus, quizController.registerUser(validUser).getStatusCode());
    }

    @Test
    public void testRegisterUserValid_Expect200_2() {
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
    public void testRegisterUserWithNotMatchingEmail_ExpectException1() {
        User invalidUser = new User("test", "qwerty");

        assertThrows(
                BadRegistrationRequestException.class,
                () -> quizController.registerUser(invalidUser));
    }

    @Test
    public void testRegisterUserWithNotMatchingEmail_ExpectException2() {
        User invalidUser = new User("test@", "qwerty");

        assertThrows(
                BadRegistrationRequestException.class,
                () -> quizController.registerUser(invalidUser));
    }

    @Test
    public void testRegisterUserWithNotMatchingEmail_ExpectException3() {
        User invalidUser = new User("test.", "qwerty");

        assertThrows(
                BadRegistrationRequestException.class,
                () -> quizController.registerUser(invalidUser));
    }

    @Test
    public void testRegisterUserWithNotMatchingEmail_ExpectException4() {
        User invalidUser = new User(".@", "qwerty");

        assertThrows(
                BadRegistrationRequestException.class,
                () -> quizController.registerUser(invalidUser));
    }

}
