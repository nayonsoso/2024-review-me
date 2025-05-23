package reviewme.review.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reviewme.highlight.domain.Highlight;
import reviewme.highlight.repository.HighlightRepository;
import reviewme.review.domain.Answer;
import reviewme.review.repository.AnswerRepository;
import reviewme.review.service.dto.response.gathered.ReviewsGatheredBySectionResponse;
import reviewme.review.service.exception.SectionNotFoundInTemplateException;
import reviewme.review.service.mapper.ReviewGatherMapper;
import reviewme.reviewgroup.domain.ReviewGroup;
import reviewme.reviewgroup.domain.exception.ReviewGroupNotFoundException;
import reviewme.reviewgroup.repository.ReviewGroupRepository;
import reviewme.template.domain.Question;
import reviewme.template.domain.Section;
import reviewme.template.repository.QuestionRepository;
import reviewme.template.repository.SectionRepository;

@Service
@RequiredArgsConstructor
public class ReviewGatheredLookupService {

    private static final int ANSWER_RESPONSE_LIMIT = 100;

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final SectionRepository sectionRepository;
    private final HighlightRepository highlightRepository;

    private final ReviewGatherMapper reviewGatherMapper;
    private final ReviewGroupRepository reviewGroupRepository;

    @Transactional(readOnly = true)
    public ReviewsGatheredBySectionResponse getReceivedReviewsBySectionId(long reviewGroupId, long sectionId) {
        ReviewGroup reviewGroup = reviewGroupRepository.findById(reviewGroupId)
                .orElseThrow(() -> new ReviewGroupNotFoundException(reviewGroupId));
        Section section = getSectionOrThrow(sectionId, reviewGroup);
        Map<Question, List<Answer>> questionAnswers = getQuestionAnswers(section, reviewGroup);

        List<Long> answerIds = questionAnswers.values()
                .stream()
                .flatMap(List::stream)
                .map(Answer::getId)
                .distinct()
                .toList();
        List<Highlight> highlights = highlightRepository.findAllByAnswerIdsOrderedAsc(answerIds);

        return reviewGatherMapper.mapToReviewsGatheredBySection(questionAnswers, highlights);
    }

    private Section getSectionOrThrow(long sectionId, ReviewGroup reviewGroup) {
        return sectionRepository.findByIdAndTemplateId(sectionId, reviewGroup.getTemplateId())
                .orElseThrow(() -> new SectionNotFoundInTemplateException(sectionId, reviewGroup.getTemplateId()));
    }

    private Map<Question, List<Answer>> getQuestionAnswers(Section section, ReviewGroup reviewGroup) {
        List<Question> questions = questionRepository.findAllBySectionIdOrderByPosition(section.getId());
        Map<Long, Question> questionIdQuestion = new LinkedHashMap<>();
        questions.forEach(question -> questionIdQuestion.put(question.getId(), question));

        Map<Long, List<Answer>> questionIdAnswers = answerRepository.findReceivedAnswersByQuestionIds(
                        reviewGroup.getId(), questionIdQuestion.keySet(), ANSWER_RESPONSE_LIMIT)
                .stream()
                .collect(Collectors.groupingBy(Answer::getQuestionId));

        Map<Question, List<Answer>> questionAnswers = new LinkedHashMap<>();
        questionIdQuestion.values().forEach(
                question -> questionAnswers.put(question, questionIdAnswers.getOrDefault(question.getId(), List.of())));
        return questionAnswers;
    }
}
