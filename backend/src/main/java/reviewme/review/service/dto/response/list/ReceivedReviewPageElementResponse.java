package reviewme.review.service.dto.response.list;

import java.time.LocalDate;
import java.util.List;

public record ReceivedReviewPageElementResponse(
        long reviewId,
        LocalDate createdAt,
        String contentPreview,
        List<ReviewCategoryResponse> categories
) {
}
