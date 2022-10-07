package engine.Security;

import engine.database.usertable.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserAuthService implements UserDetailsService {
    UserService userService;

    public UserAuthService(@Autowired UserService userService){
        this.userService = userService;
    }


    /**
     * Method allowing to authenticate user by email
     * @param username the username identifying the user whose data is required.
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.loadUserByUsername(username);
    }
}
