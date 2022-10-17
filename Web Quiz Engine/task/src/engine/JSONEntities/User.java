package engine.JSONEntities;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;


/**
 * Class user is used in QuizController in register method
 * to receive email and password from the user
 * @author Mykhailo Bubnov
 */

@Validated
public class User {

    @NotBlank
    private String email;
    @NotBlank
    private String password;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
