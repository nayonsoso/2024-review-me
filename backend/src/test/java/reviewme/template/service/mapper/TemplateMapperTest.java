package reviewme.template.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static reviewme.fixture.OptionGroupFixture.선택지_그룹;
import static reviewme.fixture.OptionItemFixture.선택지;
import static reviewme.fixture.QuestionFixture.서술형_필수_질문;
import static reviewme.fixture.QuestionFixture.선택형_필수_질문;
import static reviewme.fixture.ReviewGroupFixture.리뷰_그룹;
import static reviewme.fixture.SectionFixture.항상_보이는_섹션;
import static reviewme.fixture.TemplateFixture.템플릿;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reviewme.template.domain.OptionGroup;
import reviewme.template.domain.Question;
import reviewme.template.repository.OptionGroupRepository;
import reviewme.template.repository.OptionItemRepository;
import reviewme.template.repository.QuestionRepository;
import reviewme.reviewgroup.domain.ReviewGroup;
import reviewme.reviewgroup.repository.ReviewGroupRepository;
import reviewme.support.ServiceTest;
import reviewme.template.domain.Section;
import reviewme.template.service.exception.MissingOptionItemsInOptionGroupException;
import reviewme.template.service.exception.SectionInTemplateNotFoundException;
import reviewme.template.repository.SectionRepository;
import reviewme.template.repository.TemplateRepository;
import reviewme.template.service.dto.response.QuestionResponse;
import reviewme.template.service.dto.response.SectionResponse;
import reviewme.template.service.dto.response.TemplateResponse;

@ServiceTest
class TemplateMapperTest {

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionGroupRepository optionGroupRepository;

    @Autowired
    private OptionItemRepository optionItemRepository;

    @Autowired
    private ReviewGroupRepository reviewGroupRepository;

    @Test
    void 리뷰_그룹과_템플릿으로_템플릿_응답을_매핑한다() {
        // given
        Question question1 = questionRepository.save(서술형_필수_질문());
        Question question2 = questionRepository.save(서술형_필수_질문());

        OptionGroup optionGroup = optionGroupRepository.save(선택지_그룹(question1.getId()));
        optionItemRepository.save(선택지(optionGroup.getId()));

        Section section1 = sectionRepository.save(항상_보이는_섹션(List.of(question1.getId())));
        Section section2 = sectionRepository.save(항상_보이는_섹션(List.of(question2.getId())));

        templateRepository.save(템플릿(List.of(section1.getId(), section2.getId())));

        ReviewGroup reviewGroup = reviewGroupRepository.save(리뷰_그룹());

        // when
        TemplateResponse templateResponse = templateMapper.mapToTemplateResponse(reviewGroup);

        // then
        assertAll(
                () -> assertThat(templateResponse.revieweeName()).isEqualTo(reviewGroup.getReviewee()),
                () -> assertThat(templateResponse.projectName()).isEqualTo(reviewGroup.getProjectName()),
                () -> assertThat(templateResponse.sections()).hasSize(2),
                () -> assertThat(templateResponse.sections().get(0).header()).isEqualTo(section1.getHeader()),
                () -> assertThat(templateResponse.sections().get(0).questions()).hasSize(1),
                () -> assertThat(templateResponse.sections().get(1).header()).isEqualTo(section2.getHeader()),
                () -> assertThat(templateResponse.sections().get(1).questions()).hasSize(1)
        );
    }

    @Test
    void 섹션의_선택된_옵션이_필요없는_경우_제공하지_않는다() {
        // given
        Question question = questionRepository.save(서술형_필수_질문());
        Section section = sectionRepository.save(항상_보이는_섹션(List.of(question.getId())));
        templateRepository.save(템플릿(List.of(section.getId())));

        ReviewGroup reviewGroup = reviewGroupRepository.save(리뷰_그룹());

        // when
        TemplateResponse templateResponse = templateMapper.mapToTemplateResponse(reviewGroup);

        // then
        SectionResponse sectionResponse = templateResponse.sections().get(0);
        assertThat(sectionResponse.onSelectedOptionId()).isNull();
    }

    @Test
    void 가이드라인이_없는_경우_가이드_라인을_제공하지_않는다() {
        // given
        Question question = questionRepository.save(서술형_필수_질문());
        Section section = sectionRepository.save(항상_보이는_섹션(List.of(question.getId())));
        templateRepository.save(템플릿(List.of(section.getId())));

        ReviewGroup reviewGroup = reviewGroupRepository.save(리뷰_그룹());

        // when
        TemplateResponse templateResponse = templateMapper.mapToTemplateResponse(reviewGroup);

        // then
        QuestionResponse questionResponse = templateResponse.sections().get(0).questions().get(0);
        assertAll(
                () -> assertThat(questionResponse.hasGuideline()).isFalse(),
                () -> assertThat(questionResponse.guideline()).isNull()
        );
    }

    @Test
    void 옵션_그룹이_없는_질문의_경우_옵션_그룹을_제공하지_않는다() {
        // given
        Question question = questionRepository.save(서술형_필수_질문());
        Section section = sectionRepository.save(항상_보이는_섹션(List.of(question.getId())));
        templateRepository.save(템플릿(List.of(section.getId())));

        ReviewGroup reviewGroup = reviewGroupRepository.save(리뷰_그룹());

        // when
        TemplateResponse templateResponse = templateMapper.mapToTemplateResponse(reviewGroup);

        // then
        QuestionResponse questionResponse = templateResponse.sections().get(0).questions().get(0);
        assertThat(questionResponse.optionGroup()).isNull();
    }

    @Test
    void 템플릿_매핑_시_템플릿에_제공할_섹션이_없을_경우_예외가_발생한다() {
        // given
        templateRepository.save(템플릿(List.of(1L)));
        ReviewGroup reviewGroup = reviewGroupRepository.save(리뷰_그룹());

        // when, then
        assertThatThrownBy(() -> templateMapper.mapToTemplateResponse(reviewGroup))
                .isInstanceOf(SectionInTemplateNotFoundException.class);
    }

    @Test
    void 템플릿_매핑_시_옵션_그룹에_해당하는_옵션_아이템이_없을_경우_예외가_발생한다() {
        // given
        Question question = questionRepository.save(선택형_필수_질문());
        optionGroupRepository.save(선택지_그룹(question.getId()));

        Section section = sectionRepository.save(항상_보이는_섹션(List.of(question.getId())));
        templateRepository.save(템플릿(List.of(section.getId())));

        ReviewGroup reviewGroup = reviewGroupRepository.save(리뷰_그룹());

        // when, then
        assertThatThrownBy(() -> templateMapper.mapToTemplateResponse(reviewGroup))
                .isInstanceOf(MissingOptionItemsInOptionGroupException.class);
    }
}
