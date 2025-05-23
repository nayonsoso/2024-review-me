package reviewme.review.service.dto.response.list;

import java.util.List;

public record ReceivedReviewPageResponse(
        String revieweeName,
        String projectName,
        long lastReviewId,
        boolean isLastPage,
        List<ReceivedReviewPageElementResponse> reviews
) {
}
