package engine.database.usertable;


import engine.database.quizcompletiontable.QuizCompletionEntity;
import engine.database.quiztable.QuizJPAEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "users")
public class UserJPAEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "authority")
    private GrantedAuthority authority;

    @OneToMany(mappedBy = "user")
    private List<QuizJPAEntity> quizzes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<QuizCompletionEntity> quizCompletions = new ArrayList<>();





    public UserJPAEntity(String email, String password, GrantedAuthority authority) {
        this.email = email;
        this.password = password;
        this.authority = authority;
    }

    public UserJPAEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserJPAEntity() {
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(authority);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }


    public void setUsername(String username) {
        setEmail(username);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GrantedAuthority getAuthority() {
        return authority;
    }

    public void setAuthority(GrantedAuthority authority) {
        this.authority = authority;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public List<QuizJPAEntity> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<QuizJPAEntity> quizzes) {
        this.quizzes = quizzes;
    }

    public List<QuizCompletionEntity> getQuizCompletions() {
        return quizCompletions;
    }
}