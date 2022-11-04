package engine.database.quizcompletiontable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import engine.database.quiztable.QuizJPAEntity;
import engine.database.usertable.UserJPAEntity;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "quiz_completions")
public class QuizCompletionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "quiz_id")
    private QuizJPAEntity quiz;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private UserJPAEntity user;

    @Column(name = "completed_at", nullable = false)
    private Timestamp completedAt;

    public QuizCompletionEntity() {
    }

    public QuizCompletionEntity(QuizJPAEntity quiz, UserJPAEntity user, Timestamp completedAt) {
        this.quiz = quiz;
        this.user = user;
        this.completedAt = completedAt;
    }




    @JsonIgnore
    public Integer getId() {
        return id;
    }

    @JsonSetter
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("id")
    public int getQuizId() {
        return quiz.getId();
    }

    @JsonIgnore
    public int getUserId() {
        return user.getId();
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }
}