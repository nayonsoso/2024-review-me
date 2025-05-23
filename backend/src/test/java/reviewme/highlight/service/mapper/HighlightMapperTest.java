package reviewme.highlight.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static reviewme.fixture.QuestionFixture.서술형_필수_질문;
import static reviewme.fixture.ReviewFixture.비회원_작성_리뷰;
import static reviewme.fixture.ReviewGroupFixture.비회원_리뷰_그룹;
import static reviewme.fixture.SectionFixture.항상_보이는_섹션;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reviewme.highlight.domain.Highlight;
import reviewme.highlight.domain.HighlightRange;
import reviewme.highlight.repository.HighlightRepository;
import reviewme.highlight.service.dto.HighlightIndexRangeRequest;
import reviewme.highlight.service.dto.HighlightRequest;
import reviewme.highlight.service.dto.HighlightedLineRequest;
import reviewme.highlight.service.dto.HighlightsRequest;
import reviewme.review.domain.TextAnswer;
import reviewme.review.repository.ReviewRepository;
import reviewme.reviewgroup.repository.ReviewGroupRepository;
import reviewme.support.ServiceTest;
import reviewme.template.domain.Question;
import reviewme.template.domain.Section;
import reviewme.template.domain.Template;
import reviewme.template.repository.TemplateRepository;

@ServiceTest
class HighlightMapperTest {

    @Autowired
    private HighlightMapper highlightMapper;

    @Autowired
    private HighlightRepository highlightRepository;

    @Autowired
    private ReviewGroupRepository reviewGroupRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Test
    void 하이라이트_요청과_기존_서술형_답변으로_하이라이트를_매핑한다() {
        // given
        Question question = 서술형_필수_질문();
        Section section = 항상_보이는_섹션(List.of(question));
        Template template = templateRepository.save(new Template(List.of(section)));
        long reviewGroupId = reviewGroupRepository.save(비회원_리뷰_그룹()).getId();

        TextAnswer textAnswer1 = new TextAnswer(question.getId(), "text answer1");
        TextAnswer textAnswer2 = new TextAnswer(question.getId(), "text answer2");
        reviewRepository.save(비회원_작성_리뷰(template.getId(), reviewGroupId, List.of(textAnswer1, textAnswer2)));

        highlightRepository.save(new Highlight(1, 1, new HighlightRange(1, 1)));

        int startIndex = 2;
        int endIndex = 2;
        int lineIndex = 0;
        HighlightIndexRangeRequest rangeRequest = new HighlightIndexRangeRequest(startIndex, endIndex);
        HighlightedLineRequest lineRequest1 = new HighlightedLineRequest(lineIndex, List.of(rangeRequest));
        HighlightedLineRequest lineRequest2 = new HighlightedLineRequest(lineIndex, List.of(rangeRequest));
        HighlightRequest highlightRequest1 = new HighlightRequest(textAnswer1.getId(), List.of(lineRequest1));
        HighlightRequest highlightRequest2 = new HighlightRequest(textAnswer2.getId(), List.of(lineRequest2));
        HighlightsRequest highlightsRequest = new HighlightsRequest(
                question.getId(),
                List.of(highlightRequest1, highlightRequest2)
        );

        // when
        List<Highlight> highlights = highlightMapper.mapToHighlights(highlightsRequest);

        // then
        HighlightRange range = new HighlightRange(startIndex, endIndex);
        assertAll(
                () -> assertThat(highlights.get(0).getAnswerId()).isEqualTo(textAnswer1.getId()),
                () -> assertThat(highlights.get(1).getAnswerId()).isEqualTo(textAnswer2.getId()),
                () -> assertThat(highlights.get(0).getHighlightRange()).isEqualTo(range),
                () -> assertThat(highlights.get(1).getHighlightRange()).isEqualTo(range)
        );
    }

    @Test
    void 하이라이트_할_내용이_없는_요청이_오면_매핑_결과_빈_리스트를_반환한다() {
        // given
        HighlightsRequest highlightsRequest = new HighlightsRequest(1L, List.of());

        // when
        List<Highlight> highlights = highlightMapper.mapToHighlights(highlightsRequest);

        // then
        assertThat(highlights).isEmpty();
    }
}
