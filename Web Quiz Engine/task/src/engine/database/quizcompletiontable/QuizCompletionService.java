package engine.database.quizcompletiontable;

import engine.database.usertable.UserJPAEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class QuizCompletionService {

    private final QuizCompletionRepository quizCompletionRepository;

    public QuizCompletionService(QuizCompletionRepository quizCompletionRepository) {
        this.quizCompletionRepository = quizCompletionRepository;
    }

    public void saveQuizCompletion(QuizCompletionEntity quizCompletionEntity) {
        quizCompletionRepository.save(quizCompletionEntity);
    }

    public Page findAll(Pageable pageable) {
        return quizCompletionRepository.findAll(pageable);
    }

    public Page<QuizCompletionEntity> findQuizCompletionEntitiesByUserId(Pageable pageable, UserJPAEntity user) {
        return quizCompletionRepository.findQuizCompletionEntitiesByUser(pageable, user);
    }

}
