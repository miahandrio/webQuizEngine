package engine.database.quiztable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import engine.database.quizcompletiontable.QuizCompletionEntity;
import engine.database.usertable.UserJPAEntity;
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
    private String[] options;

    @Column(name = "answer")
    private int[] answer;

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
        return options;
    }

    @JsonIgnore
    @SuppressWarnings("unused")
    public int[] getAnswer() {
        return answer;
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
        this.options = options;
    }

    @JsonSetter
    @SuppressWarnings("unused")
    public void setAnswer(int[] answer) {
        this.answer = answer;
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