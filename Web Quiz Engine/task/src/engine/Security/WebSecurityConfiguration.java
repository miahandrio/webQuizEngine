package engine.Security;

import engine.database.usertable.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final PasswordEncoder encoder;

    public WebSecurityConfiguration(@Autowired UserService userService, @Autowired PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/api/**", "/auth/**").authenticated()
                .mvcMatchers("/api/register", "/**").permitAll()
                .and()
                .csrf().disable()
                .httpBasic()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
//                .loginProcessingUrl("/login")
//                .defaultSuccessUrl("/homepage.html", true)
//                .failureUrl("/login.html?error=true")
//                .failureHandler(authenticationFailureHandler())
//                .and()
//                .logout()
//                .logoutUrl("/perform_logout")
//                .deleteCookies("JSESSIONID")
//                .logoutSuccessHandler(logoutSuccessHandler())
                  ;
    }
}
