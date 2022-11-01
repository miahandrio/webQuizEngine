package engine;

import engine.JSONEntities.User;
import engine.database.quiztable.QuizJPAEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Objects;


@Controller
public class WebPagesController {
    QuizController quizController;

    public WebPagesController(@Autowired QuizController quizController) {
        this.quizController = quizController;
    }

    @GetMapping(path = "/")
    public String getWelcome(Model model) {
        model.addAttribute("something", "this is coming from the controller");
        return "welcome";
    }

    @GetMapping(path = "/register")
    public String getRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping(path = "/register")
    public String postRegister(@ModelAttribute @Valid User user, Model model) {
        ResponseEntity<String> response = quizController.registerUser(user);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("regResultText", response.getBody());
            model.addAttribute("regResultAlert", "alert alert-success");
        } else {
            model.addAttribute("regResultText", response.getBody());
            model.addAttribute("regResultAlert", "alert alert-danger");
        }
        return "register";
    }

    @GetMapping(path = "/login")
    public String getLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

//    @PostMapping(path = "/login")
//    public String postLogin(@ModelAttribute @Valid User user, Model model) {
//        ResponseEntity<String> response = quizController.loginUser(user);
//        if (response.getStatusCode().is2xxSuccessful()) {
//            model.addAttribute("loginResultText", response.getBody());
//            model.addAttribute("loginResultAlert", "alert alert-success");
//        } else {
//            model.addAttribute("loginResultText", response.getBody());
//            model.addAttribute("loginResultAlert", "alert alert-danger");
//        }
//        return "login";
//    }

    @GetMapping(path = "/auth/createQuiz")
    public String getCreateQuiz(Model model) {
        model.addAttribute("quiz", new QuizJPAEntity());

        return "createQuiz";
    }

    @PostMapping(path = "/auth/createQuiz")
    public String postCreateQuiz(@ModelAttribute @Valid QuizJPAEntity quiz, Model model, Authentication auth) {
        try {
            QuizJPAEntity createdQuiz = quizController.createQuiz(quiz, auth).getBody();
            model.addAttribute("createQuizText", "Quiz with id: " + Objects.requireNonNull(createdQuiz).getId() + " was created successfully");
            model.addAttribute("createQuizAlert", "alert alert-success");
        } catch (Exception e) {
            model.addAttribute("createQuizText", "An error has happened");
            model.addAttribute("createQuizAlert", "alert alert-danger");
        }
        return "createQuiz";
    }

    @GetMapping(path = "/quiz")
    public String getQuiz() {
        return "getQuiz";
    }

    @PostMapping(path = "/quiz")
    public String postQuiz(@RequestParam String stringId, Model model) {
        int id = Integer.parseInt(stringId);
        if (quizController.getQuiz(id).getStatusCode().equals(HttpStatus.OK)) {
            return "redirect:/solveQuiz/" + id;
        } else {
            model.addAttribute("findQuizText", "Quiz with id: " + id + " does not exist");
            model.addAttribute("findQuizAlert", "alert alert-danger");
            return "getQuiz";
        }
    }

    @GetMapping(path = "/quizzes")
    public String getQuizzes(Model model) {
        return "quizzes";
    }
}
