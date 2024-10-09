package reviewme.question.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import reviewme.question.domain.Question;
import reviewme.review.domain.Answer;

@Repository
public class GatheringReviewRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<Question, List<Answer>> findAnswersGroupedByQuestion(String reviewRequestCode, long sectionId) {
        String jpql = """
                    SELECT q, a
                    FROM (
                        SELECT sq.questionId AS id
                        FROM SectionQuestion sq
                        JOIN Section s ON s.id = sq.sectionId
                        WHERE s.id = :sectionId
                    ) AS filtered_questions
                    JOIN Question q ON q.id = filtered_questions.id
                    LEFT JOIN Answer a ON a.questionId = q.id
                    JOIN Review r ON r.id = a.reviewId
                    JOIN ReviewGroup rg ON rg.id = r.reviewGroupId
                    WHERE rg.reviewRequestCode = :reviewRequestCode
                """;

        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("sectionId", sectionId);
        query.setParameter("reviewRequestCode", reviewRequestCode);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .collect(Collectors.groupingBy(
                        result -> (Question) result[0],
                        Collectors.mapping(result -> (Answer) result[1], Collectors.toList())
                ));
    }
}
