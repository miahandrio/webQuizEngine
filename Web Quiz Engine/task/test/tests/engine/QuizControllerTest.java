package tests.engine;


import engine.JSONEntities.User;
import engine.QuizController;
import engine.database.quizcompletiontable.QuizCompletionService;
import engine.database.quiztable.QuizService;
import engine.database.usertable.UserService;
import engine.exceptions.BadRegistrationRequestException;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
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
     * VALID email and VALID password tests
     *
     */
    @Test
    @DisplayName("given VALID email and VALID password, when POST register, then return 200")
    public void testRegisterUserValid1() {
        User validUser = new User("test@google.com", "qwerty");
        HttpStatus expectedStatus = HttpStatus.OK;

        assertEquals(expectedStatus, quizController.registerUser(validUser).getStatusCode());
    }

    @Test
    @DisplayName("given VALID email and VALID password, when POST register, then return 200")
    public void testRegisterUserValid2() {
        User validUser = new User("@.", "qwerty");
        HttpStatus expectedStatus = HttpStatus.OK;

        assertEquals(
                expectedStatus,
                quizController.registerUser(validUser).getStatusCode());
    }


    /**
     *
     * VALID email and INVALID password tests
     *
     */

    @Test
    @DisplayName("given VALID email and INVALID (short) password, when POST register, then expect exception")
    public void testRegisterUserShortPassword() {
        //The password is shorter than 5 characters
        User invalidUser = new User("test@google.com", "pas");


        assertThrows(
                BadRegistrationRequestException.class,
                () -> quizController.registerUser(invalidUser));
    }



    @Test
    @DisplayName("given INVALID email and VALID password, when POST register, then expect exception")
    public void testRegisterUser3() {
        //The password is shorter than 5 characters
        User invalidUser1 = new User("test", "qwerty");
        User invalidUser2 = new User("", "qwerty");
        User invalidUser3 = new User("test", "qwerty");
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;

        assertThrows(
                BadRegistrationRequestException.class,
                () -> quizController.registerUser(invalidUser1));
    }

}
