package reviewme.question.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static reviewme.fixture.OptionGroupFixture.선택지_그룹;
import static reviewme.fixture.OptionItemFixture.선택지;
import static reviewme.fixture.QuestionFixture.서술형_옵션_질문;
import static reviewme.fixture.QuestionFixture.서술형_필수_질문;
import static reviewme.fixture.QuestionFixture.선택형_필수_질문;
import static reviewme.fixture.ReviewGroupFixture.리뷰_그룹;
import static reviewme.fixture.SectionFixture.항상_보이는_섹션;
import static reviewme.fixture.TemplateFixture.템플릿;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import reviewme.question.domain.OptionGroup;
import reviewme.question.domain.OptionItem;
import reviewme.question.domain.Question;
import reviewme.review.domain.Answer;
import reviewme.review.domain.CheckboxAnswer;
import reviewme.review.domain.Review;
import reviewme.review.domain.TextAnswer;
import reviewme.review.repository.ReviewRepository;
import reviewme.reviewgroup.domain.ReviewGroup;
import reviewme.reviewgroup.repository.ReviewGroupRepository;
import reviewme.template.domain.Section;
import reviewme.template.domain.Template;
import reviewme.template.repository.SectionRepository;
import reviewme.template.repository.TemplateRepository;

@DataJpaTest
@Import(GatheringReviewRepository.class)
class GatheringReviewRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private ReviewGroupRepository reviewGroupRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OptionGroupRepository optionGroupRepository;

    @Autowired
    private OptionItemRepository optionItemRepository;

    @Autowired
    private GatheringReviewRepository gatheringReviewRepository;

    @Test
    void 리뷰_요청_코드에_해당하는_응답들을_질문_별로_묶어_가져온다() {
        // given - 템플릿 저장
        Question s1question1 = questionRepository.save(서술형_필수_질문(1));
        Question s1question2 = questionRepository.save(서술형_옵션_질문(2));
        Question s2question3 = questionRepository.save(선택형_필수_질문(1));
        OptionGroup optionGroup = optionGroupRepository.save(선택지_그룹(s2question3.getId()));
        OptionItem optionItem1 = optionItemRepository.save(선택지(optionGroup.getId()));
        OptionItem optionItem2 = optionItemRepository.save(선택지(optionGroup.getId()));
        Section section1 = sectionRepository.save(항상_보이는_섹션(List.of(s1question1.getId(), s1question2.getId())));
        Section section2 = sectionRepository.save(항상_보이는_섹션(List.of(s2question3.getId())));
        Template template = templateRepository.save(템플릿(List.of(section1.getId(), section2.getId())));

        // given - 리뷰 그룹 저장
        String reviewRequestCode = "reviewRQ";
        ReviewGroup 리뷰_그룹 = reviewGroupRepository.save(리뷰_그룹(reviewRequestCode, "groupAC"));

        // given - 응답 저장
        TextAnswer textAnswerKB = new TextAnswer(s1question1.getId(), "커비의 답1".repeat(5));
        CheckboxAnswer checkboxAnswerKB = new CheckboxAnswer(s2question3.getId(), List.of(optionItem1.getId()));
        reviewRepository.save(new Review(template.getId(), 리뷰_그룹.getId(), List.of(textAnswerKB, checkboxAnswerKB)));

        TextAnswer textAnswerTD1 = new TextAnswer(s1question1.getId(), "테드의 답1".repeat(5));
        TextAnswer textAnswerTD2 = new TextAnswer(s1question2.getId(), "테드의 답2".repeat(5));
        CheckboxAnswer checkboxAnswerTD = new CheckboxAnswer(s2question3.getId(),List.of(optionItem2.getId()));
        reviewRepository.save(new Review(template.getId(), 리뷰_그룹.getId(),
                List.of(textAnswerTD1, textAnswerTD2, checkboxAnswerTD)));

        // when
        Map<Question, List<Answer>> answersForSection1 = gatheringReviewRepository.findAnswersGroupedByQuestion(
                reviewRequestCode, section1.getId());
        Map<Question, List<Answer>> answersForSection2 = gatheringReviewRepository.findAnswersGroupedByQuestion(
                reviewRequestCode, section2.getId());

        // then
        assertAll(
                () -> assertThat(answersForSection1.get(s1question1)).containsOnly(textAnswerKB, textAnswerTD1),
                () -> assertThat(answersForSection1.get(s1question2)).containsOnly(textAnswerTD2),
                () -> assertThat(answersForSection2.get(s2question3)).containsOnly(checkboxAnswerKB, checkboxAnswerTD)
        );
    }
}
