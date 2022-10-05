package engine.database.quizcompletiontable;

import engine.database.usertable.UserJPAEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
interface QuizCompletionRepository extends PagingAndSortingRepository<QuizCompletionEntity, Integer> {
    Page<QuizCompletionEntity> findQuizCompletionEntitiesByUser(Pageable pageable, UserJPAEntity user);
}