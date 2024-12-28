package reviewme.template.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reviewme.reviewgroup.domain.ReviewGroup;
import reviewme.reviewgroup.service.ReviewGroupService;
import reviewme.template.service.dto.response.SectionResponse;
import reviewme.template.service.dto.response.TemplateResponse;
import reviewme.template.service.mapper.TemplateMapper;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final ReviewGroupService reviewGroupService;
    private final TemplateMapper templateMapper;

    @Transactional(readOnly = true)
    public TemplateResponse generateReviewForm(String reviewRequestCode) {
        ReviewGroup reviewGroup = reviewGroupService.getReviewGroupByReviewRequestCode(reviewRequestCode);
        List<SectionResponse> sectionResponses = templateMapper.mapSectionResponses(
                reviewGroup.getId(), reviewGroup.getTemplateId()
        );
        return new TemplateResponse(
                reviewGroup.getTemplateId(), reviewGroup.getReviewee(), reviewGroup.getProjectName(), sectionResponses
        );
    }
}
