package engine.database.usertable;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserJPAEntity saveUser(UserJPAEntity userJPAEntity) {
        return userRepository.save(userJPAEntity);
    }

    public UserJPAEntity getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean isUserExist(int id) {
        return userRepository.existsById(id);
    }

    public boolean isUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (userRepository.existsByEmail(email)) {
            return userRepository.findUserByEmail(email);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
