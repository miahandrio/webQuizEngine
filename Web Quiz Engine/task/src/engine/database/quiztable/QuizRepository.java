package engine.database.quiztable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
interface QuizRepository extends PagingAndSortingRepository<QuizJPAEntity, Integer> {
}