package reviewme.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static reviewme.fixture.OptionGroupFixture.선택지_그룹;
import static reviewme.fixture.OptionItemFixture.선택지;
import static reviewme.fixture.QuestionFixture.선택형_필수_질문;
import static reviewme.fixture.ReviewGroupFixture.리뷰_그룹;
import static reviewme.fixture.SectionFixture.항상_보이는_섹션;
import static reviewme.fixture.TemplateFixture.템플릿;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reviewme.template.domain.OptionGroup;
import reviewme.template.domain.OptionItem;
import reviewme.template.domain.Question;
import reviewme.template.repository.OptionGroupRepository;
import reviewme.template.repository.OptionItemRepository;
import reviewme.template.repository.QuestionRepository;
import reviewme.review.domain.CheckboxAnswer;
import reviewme.review.domain.Review;
import reviewme.review.domain.TextAnswer;
import reviewme.review.repository.ReviewRepository;
import reviewme.review.service.dto.response.list.ReceivedReviewsResponse;
import reviewme.reviewgroup.domain.ReviewGroup;
import reviewme.reviewgroup.repository.ReviewGroupRepository;
import reviewme.support.ServiceTest;
import reviewme.template.domain.Section;
import reviewme.template.domain.Template;
import reviewme.template.repository.SectionRepository;
import reviewme.template.repository.TemplateRepository;

@ServiceTest
class ReviewListLookupServiceTest {

    @Autowired
    private ReviewListLookupService reviewListLookupService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ReviewGroupRepository reviewGroupRepository;

    @Autowired
    private OptionItemRepository optionItemRepository;

    @Autowired
    private OptionGroupRepository optionGroupRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void 확인_코드에_해당하는_그룹이_존재하면_내가_받은_리뷰_목록을_반환한다() {
        // given - 리뷰 그룹 저장
        String reviewRequestCode = "reviewRequestCode";
        String groupAccessCode = "groupAccessCode";
        ReviewGroup reviewGroup = reviewGroupRepository.save(리뷰_그룹(reviewRequestCode, groupAccessCode));

        // given - 질문 저장
        Question question = questionRepository.save(선택형_필수_질문());
        OptionGroup optionGroup = optionGroupRepository.save(선택지_그룹(question.getId()));
        OptionItem categoryOption = optionItemRepository.save(선택지(optionGroup.getId(), 1));

        // given - 섹션, 템플릿 저장
        Section section = sectionRepository.save(항상_보이는_섹션(List.of(question.getId())));
        Template template = templateRepository.save(템플릿(List.of(section.getId())));

        // given - 리뷰 답변 저장
        CheckboxAnswer categoryAnswer = new CheckboxAnswer(question.getId(), List.of(categoryOption.getId()));
        Review review1 = new Review(template.getId(), reviewGroup.getId(), List.of(categoryAnswer));
        TextAnswer textAnswer = new TextAnswer(question.getId(), "텍스트형 응답");
        Review review2 = new Review(template.getId(), reviewGroup.getId(), List.of(textAnswer));
        reviewRepository.saveAll(List.of(review1, review2));

        // when
        ReceivedReviewsResponse response = reviewListLookupService.getReceivedReviews(
                Long.MAX_VALUE, 5, reviewGroup
        );

        // then
        assertAll(
                () -> assertThat(response.reviews()).hasSize(2),
                () -> assertThat(response.lastReviewId()).isEqualTo(review1.getId()),
                () -> assertThat(response.isLastPage()).isTrue()
        );
    }

    @Test
    void 내가_받은_리뷰_목록을_페이지네이션을_적용하여_반환한다() {
        // given - 리뷰 그룹 저장
        String reviewRequestCode = "reviewRequestCode";
        String groupAccessCode = "groupAccessCode";
        ReviewGroup reviewGroup = reviewGroupRepository.save(리뷰_그룹(reviewRequestCode, groupAccessCode));

        // given - 질문 저장
        Question question = questionRepository.save(선택형_필수_질문());

        // given - 섹션, 템플릿 저장
        Section section = sectionRepository.save(항상_보이는_섹션(List.of(question.getId())));
        Template template = templateRepository.save(템플릿(List.of(section.getId())));

        // given - 리뷰 답변 저장
        TextAnswer textAnswer = new TextAnswer(question.getId(), "텍스트형 응답");
        Review review1 = new Review(template.getId(), reviewGroup.getId(), List.of(textAnswer));
        Review review2 = new Review(template.getId(), reviewGroup.getId(), List.of(textAnswer));
        Review review3 = new Review(template.getId(), reviewGroup.getId(), List.of(textAnswer));
        reviewRepository.saveAll(List.of(review1, review2, review3));

        // when
        ReceivedReviewsResponse response
                = reviewListLookupService.getReceivedReviews(Long.MAX_VALUE, 2, reviewGroup);

        // then
        assertAll(
                () -> assertThat(response.reviews())
                        .hasSize(2)
                        .extracting("reviewId")
                        .containsExactly(review3.getId(), review2.getId()),
                () -> assertThat(response.lastReviewId())
                        .isEqualTo(review2.getId()),
                () -> assertThat(response.isLastPage())
                        .isFalse()
        );
    }
}
