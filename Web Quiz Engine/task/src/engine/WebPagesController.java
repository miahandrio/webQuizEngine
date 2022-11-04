package engine;

import engine.JSONEntities.*;
import engine.database.quiztable.QuizJPAEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
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

    @GetMapping(path = "/solveQuiz/{id}")
    public String getSolveQuiz(Model model, @PathVariable int id) {
        if (!quizController.getQuiz(id).getStatusCode().equals(HttpStatus.OK)) {
            model.addAttribute("findQuizText", "Quiz with id: " + id + " does not exist");
            model.addAttribute("findQuizText", "alert alert-danger");
            return "redirect:/quiz";
        }
        model.addAttribute("quizId", id);
        model.addAttribute("answer", new AnswerWebEntity());

        QuizJPAEntity quiz = quizController.getQuiz(id).getBody();
        String[] options = Objects.requireNonNull(quiz).getOptions();

        List<OptionWebEntity> optionsList = new ArrayList<>(options.length);
        for (int i = 0; i < options.length; i++) {
            optionsList.add(new OptionWebEntity(i, options[i]));
        }

        model.addAttribute("title", Objects.requireNonNull(quiz).getTitle());
        model.addAttribute("text", Objects.requireNonNull(quiz).getText());
        model.addAttribute("options", optionsList);
        return "solveQuiz";
    }

    @PostMapping(path = "/solveQuiz/{id}")
    public String postSolveQuiz(@PathVariable int id, @ModelAttribute @Valid AnswerWebEntity answer, Model model, Authentication auth) {
        if (!quizController.getQuiz(id).getStatusCode().equals(HttpStatus.OK)) {
            model.addAttribute("findQuizText", "Quiz with id: " + id + " does not exist");
            model.addAttribute("findQuizText", "alert alert-danger");
            return "redirect:/quiz";
        }

        model.addAttribute("quizId", id);
        model.addAttribute("answer", new AnswerWebEntity());

        QuizJPAEntity quiz = quizController.getQuiz(id).getBody();
        String[] options = Objects.requireNonNull(quiz).getOptions();

        List<OptionWebEntity> optionsList = new ArrayList<>(options.length);
        for (int i = 0; i < options.length; i++) {
            optionsList.add(new OptionWebEntity(i, options[i]));
        }

        model.addAttribute("title", Objects.requireNonNull(quiz).getTitle());
        model.addAttribute("text", Objects.requireNonNull(quiz).getText());
        model.addAttribute("options", optionsList);

        ResponseEntity<ServerFeedback> response = quizController.solveQuiz(id, answer.toAnswer(), auth);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("solveQuizText", response.getBody().getFeedback());
            model.addAttribute("solveQuizAlert", "alert alert-success");
        } else {
            model.addAttribute("solveQuizText", response.getBody().getFeedback());
            model.addAttribute("solveQuizAlert", "alert alert-danger");
        }
        return "solveQuiz";
    }

    @GetMapping(path = "/quizzes")
    public String getQuizzes(Model model) {
        return "quizzes";
    }
}
