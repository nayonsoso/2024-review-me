package reviewme.review.service.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reviewme.template.domain.Question;
import reviewme.template.repository.QuestionRepository;
import reviewme.review.domain.Answer;
import reviewme.review.domain.Review;
import reviewme.review.service.dto.request.ReviewAnswerRequest;
import reviewme.review.service.dto.request.ReviewRegisterRequest;
import reviewme.reviewgroup.service.exception.ReviewGroupNotFoundByReviewRequestCodeException;
import reviewme.review.service.exception.SubmittedQuestionNotFoundException;
import reviewme.reviewgroup.domain.ReviewGroup;
import reviewme.reviewgroup.repository.ReviewGroupRepository;
import reviewme.template.domain.Template;
import reviewme.template.repository.TemplateRepository;
import reviewme.template.service.exception.TemplateNotFoundByReviewGroupException;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewMapper {

    private final AnswerMapperFactory answerMapperFactory;
    private final ReviewGroupRepository reviewGroupRepository;
    private final QuestionRepository questionRepository;
    private final TemplateRepository templateRepository;

    public Review mapToReview(ReviewRegisterRequest request, Long memberId) {
        ReviewGroup reviewGroup = reviewGroupRepository.findByReviewRequestCode(request.reviewRequestCode())
                .orElseThrow(() -> new ReviewGroupNotFoundByReviewRequestCodeException(request.reviewRequestCode()));
        Template template = templateRepository.findById(reviewGroup.getTemplateId())
                .orElseThrow(() -> new TemplateNotFoundByReviewGroupException(
                        reviewGroup.getId(), reviewGroup.getTemplateId()
                ));

        List<Answer> answers = getAnswersByQuestionType(request);
        return new Review(memberId, template.getId(), reviewGroup.getId(), answers);
    }

    private List<Answer> getAnswersByQuestionType(ReviewRegisterRequest request) {
        List<Long> questionIds = request.answers()
                .stream()
                .map(ReviewAnswerRequest::questionId)
                .toList();

        Map<Long, Question> questionMap = questionRepository.findAllById(questionIds)
                .stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        return request.answers()
                .stream()
                .map(answerRequest -> mapRequestToAnswer(questionMap, answerRequest))
                .filter(Objects::nonNull)
                .toList();
    }

    private Answer mapRequestToAnswer(Map<Long, Question> questions, ReviewAnswerRequest answerRequest) {
        Question question = questions.get(answerRequest.questionId());
        if (question == null) {
            throw new SubmittedQuestionNotFoundException(answerRequest.questionId());
        }

        AnswerMapper answerMapper = answerMapperFactory.getAnswerMapper(question.getQuestionType());
        return answerMapper.mapToAnswer(answerRequest);
    }
}
