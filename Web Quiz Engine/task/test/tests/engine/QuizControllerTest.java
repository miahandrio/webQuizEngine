package tests.engine;


import engine.JSONEntities.Answer;
import engine.JSONEntities.User;
import engine.QuizController;
import engine.database.quizcompletiontable.QuizCompletionService;
import engine.database.quiztable.QuizJPAEntity;
import engine.database.quiztable.QuizService;
import engine.database.usertable.UserJPAEntity;
import engine.database.usertable.UserService;
import engine.exceptions.BadRegistrationRequestException;
import engine.exceptions.QuizNotFoundException;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


public class QuizControllerTest {

    private final QuizService quizService = Mockito.mock(QuizService.class);
    private final UserService userService = Mockito.mock(UserService.class);
    private final QuizCompletionService quizCompletionService = Mockito.mock(QuizCompletionService.class);
    private final Authentication auth = Mockito.mock(Authentication.class);

    private final QuizController quizController = new QuizController(quizService, userService, quizCompletionService);

    public QuizControllerTest() {
        quizController.setEncoder(new BCryptPasswordEncoder());
        Mockito.when(userService.isUserExistsByEmail(any(String.class))).thenReturn(false);


    }
    /*
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


    /*
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


    /*
     *
     *
     * getQuizzes tests
     *
     *
     */

    @Test
    public void testGetQuizzes_Expect200() {
        int pageIdParam = 1;
        int defaultPageSize = 10;

        Pageable paging = PageRequest.of(pageIdParam, defaultPageSize);
        Mockito.when(quizService.findAll(paging)).thenReturn(null);

        assertEquals(HttpStatus.OK, quizController.getQuizzes(pageIdParam).getStatusCode());
    }


    /*
     *
     *
     * getQuizzesCompletion tests
     *
     *
     */

    @Test
    public void testGetQuizzesCompletion_Expect200() {
        int pageIdParam = 1;
        int defaultPageSize = 10;

        Pageable paging = PageRequest.of(pageIdParam, defaultPageSize);
        Mockito.when(quizService.findAll(paging)).thenReturn(null);
        Mockito.when(auth.getPrincipal()).thenReturn(new UserJPAEntity());
        Mockito.when(quizCompletionService.findQuizCompletionEntitiesByUserId(
                Mockito.any(Pageable.class),
                Mockito.any(UserJPAEntity.class)))
                .thenReturn(null);

        assertEquals(HttpStatus.OK,
                quizController.getQuizzesCompletion(pageIdParam, auth).getStatusCode());
    }

    /*
     *
     *
     * createQuiz tests
     *
     *
     */

    @Test
    public void testCreateQuiz_Expect201() {
        Mockito.when(auth.getPrincipal()).thenReturn(new UserJPAEntity());
        Mockito.when(quizService.saveQuiz(Mockito.any(QuizJPAEntity.class))).thenReturn(new QuizJPAEntity());

        QuizJPAEntity quiz = new QuizJPAEntity();
        quiz.setTitle("The Java Logo");
        quiz.setText("What is depicted on the Java logo?");
        quiz.setOptions(new String[]{"Robot", "Tea leaf", "Cup of coffee", "Bug"});
        quiz.setAnswer(new int[]{2,3});

        assertEquals(HttpStatus.CREATED,
                quizController.createQuiz(new QuizJPAEntity(), auth).getStatusCode());
    }

    /*
     *
     *
     * solveQuiz tests
     *
     *
     */

    @Test
    public void testSolveQuiz_GivenValidAnswer_Expect200() {
        int testQuizId = 1;

        QuizJPAEntity testQuiz = new QuizJPAEntity();
        testQuiz.setAnswer(new int[]{1,2});

        Answer testAnswer = new Answer();
        testAnswer.setAnswer(new int[]{1, 2});

        Mockito.when(auth.getPrincipal()).thenReturn(new UserJPAEntity());
        Mockito.when(quizService.isQuizExist(testQuizId)).thenReturn(true);
        Mockito.when(quizService.getQuizById(testQuizId)).thenReturn(testQuiz);


        assertEquals(HttpStatus.OK,
                quizController.solveQuiz(testQuizId, testAnswer, auth).getStatusCode());
    }

    @Test
    public void testSolveQuiz_GivenValidNullAnswer_Expect200() {
        int testQuizId = 1;

        QuizJPAEntity testQuiz = new QuizJPAEntity();
        testQuiz.setAnswer(null);

        Answer testAnswer = new Answer();

        Mockito.when(auth.getPrincipal()).thenReturn(new UserJPAEntity());
        Mockito.when(quizService.isQuizExist(testQuizId)).thenReturn(true);
        Mockito.when(quizService.getQuizById(testQuizId)).thenReturn(testQuiz);


        assertEquals(HttpStatus.OK,
                quizController.solveQuiz(testQuizId, testAnswer, auth).getStatusCode());
    }

    @Test
    public void testSolveQuiz_GivenValidBlankAnswer_Expect200() {
        int testQuizId = 1;

        QuizJPAEntity testQuiz = new QuizJPAEntity();
        testQuiz.setAnswer(new int[]{});

        Answer testAnswer = new Answer();
        testAnswer.setAnswer(new int[]{});

        Mockito.when(auth.getPrincipal()).thenReturn(new UserJPAEntity());
        Mockito.when(quizService.isQuizExist(testQuizId)).thenReturn(true);
        Mockito.when(quizService.getQuizById(testQuizId)).thenReturn(testQuiz);


        assertEquals(HttpStatus.OK,
                quizController.solveQuiz(testQuizId, testAnswer, auth).getStatusCode());
    }

    @Test
    public void testSolveQuiz_GivenInvalidAnswer_Expect400() {
        int testQuizId = 1;

        QuizJPAEntity testQuiz = new QuizJPAEntity();
        testQuiz.setAnswer(new int[]{1,2});

        Answer testAnswer = new Answer();
        testAnswer.setAnswer(new int[]{3,4});

        Mockito.when(auth.getPrincipal()).thenReturn(new UserJPAEntity());
        Mockito.when(quizService.isQuizExist(testQuizId)).thenReturn(true);
        Mockito.when(quizService.getQuizById(testQuizId)).thenReturn(testQuiz);


        assertEquals(HttpStatus.BAD_REQUEST,
                quizController.solveQuiz(testQuizId, testAnswer, auth).getStatusCode());
    }

    @Test
    public void testSolveQuiz_GivenHalfValidAnswer_Expect400() {
        int testQuizId = 1;

        QuizJPAEntity testQuiz = new QuizJPAEntity();
        testQuiz.setAnswer(new int[]{1,2});

        Answer testAnswer = new Answer();
        testAnswer.setAnswer(new int[]{1});

        Mockito.when(auth.getPrincipal()).thenReturn(new UserJPAEntity());
        Mockito.when(quizService.isQuizExist(testQuizId)).thenReturn(true);
        Mockito.when(quizService.getQuizById(testQuizId)).thenReturn(testQuiz);


        assertEquals(HttpStatus.BAD_REQUEST,
                quizController.solveQuiz(testQuizId, testAnswer, auth).getStatusCode());
    }

    @Test
    public void testSolveQuiz_GivenInvalidQuizId_ExpectThrows() {
        int testQuizId = 1;

        QuizJPAEntity testQuiz = new QuizJPAEntity();
        testQuiz.setAnswer(new int[]{1, 2});

        Answer testAnswer = new Answer();
        testAnswer.setAnswer(new int[]{1, 2});

        Mockito.when(auth.getPrincipal()).thenReturn(new UserJPAEntity());
        Mockito.when(quizService.isQuizExist(testQuizId)).thenReturn(false);
        Mockito.when(quizService.getQuizById(testQuizId)).thenReturn(null);

        assertThrows(QuizNotFoundException.class,
                () -> quizController.solveQuiz(testQuizId, testAnswer, auth));
    }

    /*
     *
     *
     * deleteQuiz tests
     *
     *
     */

    @Test
    public void testDeleteQuiz_GivenValidQuizId_Expect204() {
        int testQuizId = 1;
        int testUserQuizDeleterId = 1;

        UserJPAEntity testUserQuizDeleter = new UserJPAEntity();
        testUserQuizDeleter.setId(testUserQuizDeleterId);

        UserJPAEntity testUserQuizCreator = new UserJPAEntity();
        testUserQuizCreator.setId(testQuizId);

        QuizJPAEntity testQuiz = new QuizJPAEntity();
        testQuiz.setId(testQuizId);
        testQuiz.setUser(testUserQuizCreator);

        Mockito.when(auth.getPrincipal()).thenReturn(testUserQuizDeleter);
        Mockito.when(quizService.isQuizExist(testQuizId)).thenReturn(true);
        Mockito.when(quizService.getQuizById(testQuizId)).thenReturn(testQuiz);

        assertEquals(HttpStatus.NO_CONTENT,
                quizController.deleteQuiz(testQuizId, auth).getStatusCode());
    }

    @Test
    public void testDeleteQuiz_GivenInvalidQuizId_Expect403() {
        int testQuizId = 2;
        int testUserQuizDeleterId = 1;

        UserJPAEntity testUserQuizDeleter = new UserJPAEntity();
        testUserQuizDeleter.setId(testUserQuizDeleterId);

        UserJPAEntity testUserQuizCreator = new UserJPAEntity();
        testUserQuizCreator.setId(testQuizId);

        QuizJPAEntity testQuiz = new QuizJPAEntity();
        testQuiz.setId(testQuizId);
        testQuiz.setUser(testUserQuizCreator);

        Mockito.when(auth.getPrincipal()).thenReturn(testUserQuizDeleter);
        Mockito.when(quizService.isQuizExist(testQuizId)).thenReturn(true);
        Mockito.when(quizService.getQuizById(testQuizId)).thenReturn(testQuiz);

        assertEquals(HttpStatus.FORBIDDEN,
                quizController.deleteQuiz(testQuizId, auth).getStatusCode());
    }

    @Test
    public void testDeleteQuiz_GivenNonexistentQuiz_ExpectThrows() {
        int testQuizId = 1;
        Mockito.when(auth.getPrincipal()).thenReturn(new UserJPAEntity());
        Mockito.when(quizService.isQuizExist(testQuizId)).thenReturn(false);
        Mockito.when(quizService.getQuizById(testQuizId)).thenReturn(new QuizJPAEntity());

        assertThrows(QuizNotFoundException.class,
                () -> quizController.deleteQuiz(testQuizId, auth));
    }
}
