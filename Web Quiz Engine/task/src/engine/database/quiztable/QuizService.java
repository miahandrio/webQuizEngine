package engine.database.quiztable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class QuizService {
    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public QuizJPAEntity saveQuiz(QuizJPAEntity quizJPAEntity) {
        return quizRepository.save(quizJPAEntity);
    }

    public QuizJPAEntity getQuizById(int id) {
        return quizRepository.findById(id).orElse(null);
    }

    public void deleteQuizById(int id) {
        quizRepository.deleteById(id);
    }

    public boolean isQuizExist(int id) {
        return quizRepository.existsById(id);
    }

    public Iterable<QuizJPAEntity> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Page<QuizJPAEntity> findAll(Pageable pageable) {
        return quizRepository.findAll(pageable);
    }
}
