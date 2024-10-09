package reviewme.question.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import reviewme.question.domain.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = """
            SELECT q.id FROM question q
            JOIN section_question sq
            ON q.id = sq.question_id
            JOIN template_section ts
            ON sq.section_id = ts.section_id
            WHERE ts.template_id = :templateId
            """, nativeQuery = true)
    Set<Long> findAllQuestionIdByTemplateId(long templateId);

    @Query(value = """
            SELECT q.* FROM question q
            JOIN section_question sq
            ON q.id = sq.question_id
            JOIN template_section ts
            ON sq.section_id = ts.section_id
            WHERE ts.template_id = :templateId
            """, nativeQuery = true)
    List<Question> findAllByTemplatedId(long templateId);

    @Query(value = """
            SELECT new reviewme.question.repository.QuestionAnswerDto(q, a)
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
            """)
    List<QuestionAnswerDto> findReceivedAnswersGroupedByQuestion(String reviewRequestCode, long sectionId);
}
