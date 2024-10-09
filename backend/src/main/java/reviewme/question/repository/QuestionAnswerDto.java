package reviewme.question.repository;

import reviewme.question.domain.Question;
import reviewme.review.domain.Answer;

public record QuestionAnswerDto(
        Question question,
        Answer answer
) {
}
