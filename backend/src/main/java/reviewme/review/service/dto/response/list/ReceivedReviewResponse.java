package reviewme.review.service.dto.response.list;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(name = "리뷰 내용 응답")
public record ReceivedReviewResponse(

        @Schema(description = "리뷰 ID")
        long reviewId,

        @Schema(description = "리뷰 작성일")
        LocalDate createdAt,

        @Schema(description = "응답 내용 미리보기")
        String contentPreview,

        @Schema(description = "선택된 카테고리 목록")
        List<ReceivedReviewCategoryResponse> categories
) {
}