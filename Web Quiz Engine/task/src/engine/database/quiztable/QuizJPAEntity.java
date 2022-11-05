package engine.database.quiztable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import engine.database.quizcompletiontable.QuizCompletionEntity;
import engine.database.usertable.UserJPAEntity;
import engine.exceptions.QuizOptionContainingIllegalCharacterExeption;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "quizzes")
@Validated
public class QuizJPAEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "quiz_id", nullable = false)
    private int id;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "text", nullable = false, length = 1024)
    private String text;

    @Size(min = 2, max = 10)
    @NotNull
    @Column(name = "options", nullable = false)
    private String options;

    @Column(name = "answer")
    private String answer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserJPAEntity user;

    @OneToMany(mappedBy = "quiz", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizCompletionEntity> quizCompletions = new ArrayList<>();


    @SuppressWarnings("unused")
    public int getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public String getTitle() {
        return title;
    }

    @SuppressWarnings("unused")
    public String getText() {
        return text;
    }

    @SuppressWarnings("unused")
    public String[] getOptions() {
        return options.split(";");
    }

    @JsonIgnore
    @SuppressWarnings("unused")
    public int[] getAnswer() {
        if (answer == null) {
            return null;
        }
        if (answer.equals("")) {
            return new int[0];
        }
        String[] answerStrings = answer.split(";");
        int[] answerArray = new int[answerStrings.length];
        for (int i = 0; i < answerStrings.length; i++) {
            answerArray[i] = Integer.parseInt(answerStrings[i]);
        }
        return answerArray;
    }


    @SuppressWarnings("unused")
    public void setId(int id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public void setTitle(String title) {
        this.title = title;
    }

    @SuppressWarnings("unused")
    public void setText(String text) {
        this.text = text;
    }

    @SuppressWarnings("unused")
    public void setOptions(String[] options) {
        for (String option : options) {
            if (option.contains(";")) {
                throw new QuizOptionContainingIllegalCharacterExeption("Quiz option cannot contain ';' character");
            }
        }
        this.options = String.join(";", options);
    }

    @JsonSetter
    @SuppressWarnings("unused")
    public void setAnswer(int[] answer) {
        if (answer == null) {
            this.answer = null;
        } else {
            String answerString = "";
            for (int i = 0; i < answer.length; i++) {
                answerString += answer[i];
                if (i != answer.length - 1) {
                    answerString += ";";
                }
            }
            this.answer = answerString;
        }
    }

    @JsonIgnore
    public UserJPAEntity getUser() {
        return user;
    }

    public void setUser(UserJPAEntity user) {
        this.user = user;
    }

    @JsonIgnore
    public List<QuizCompletionEntity> getQuizCompletions() {
        return quizCompletions;
    }
}