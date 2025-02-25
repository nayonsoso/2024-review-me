package reviewme.review.service.validator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reviewme.template.domain.Question;
import reviewme.template.repository.QuestionRepository;
import reviewme.review.domain.Answer;
import reviewme.review.domain.TextAnswer;
import reviewme.review.service.exception.InvalidTextAnswerLengthException;
import reviewme.review.service.exception.SubmittedQuestionNotFoundException;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TextTypedAnswerValidator implements TypedAnswerValidator {

    private static final int MIN_LENGTH = 20;
    private static final int MAX_LENGTH = 1_000;

    private final QuestionRepository questionRepository;

    @Override
    public boolean supports(Class<? extends Answer> answerClass) {
        return TextAnswer.class.isAssignableFrom(answerClass);
    }

    @Override
    public void validate(Answer answer) {
        TextAnswer textAnswer = (TextAnswer) answer;
        Question question = questionRepository.findById(textAnswer.getQuestionId())
                .orElseThrow(() -> new SubmittedQuestionNotFoundException(textAnswer.getQuestionId()));

        validateLength(textAnswer, question);
    }

    private void validateLength(TextAnswer textAnswer, Question question) {
        int answerLength = textAnswer.getContent().length();

        if (question.isRequired() && (answerLength < MIN_LENGTH || answerLength > MAX_LENGTH)) {
            throw new InvalidTextAnswerLengthException(question.getId(), answerLength, MIN_LENGTH, MAX_LENGTH);
        }

        if (!question.isRequired() && answerLength > MAX_LENGTH) {
            throw new InvalidTextAnswerLengthException(question.getId(), answerLength, MAX_LENGTH);
        }
    }
}
