package engine.database.usertable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<UserJPAEntity, Integer> {
    boolean existsByEmail(String email);

    UserJPAEntity findUserByEmail(String email);
}