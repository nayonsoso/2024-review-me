package reviewme.review.controller;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reviewme.review.service.ReviewDetailLookupService;
import reviewme.review.service.ReviewGatheredLookupService;
import reviewme.review.service.ReviewListLookupService;
import reviewme.review.service.ReviewRegisterService;
import reviewme.review.service.ReviewSummaryService;
import reviewme.review.service.dto.request.ReviewRegisterRequest;
import reviewme.review.service.dto.response.detail.ReviewDetailResponse;
import reviewme.review.service.dto.response.gathered.ReviewsGatheredBySectionResponse;
import reviewme.review.service.dto.response.list.AuthoredReviewsResponse;
import reviewme.review.service.dto.response.list.ReceivedReviewPageResponse;
import reviewme.review.service.dto.response.list.ReceivedReviewsSummaryResponse;
import reviewme.reviewgroup.controller.ReviewGroupSession;
import reviewme.reviewgroup.domain.ReviewGroup;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRegisterService reviewRegisterService;
    private final ReviewListLookupService reviewListLookupService;
    private final ReviewDetailLookupService reviewDetailLookupService;
    private final ReviewSummaryService reviewSummaryService;
    private final ReviewGatheredLookupService reviewGatheredLookupService;

    @PostMapping("/v2/reviews")
    public ResponseEntity<Void> createReview(
            @Valid @RequestBody ReviewRegisterRequest request
            /*
            TODO: 회원 세션 임시 사용 방식, 이후 리졸버를 통해 객체로 받아와야 함
            @Nullable @LoginMember Member member
             */
    ) {
        /*
        TODO: 회원 세션 유무에 따른 분기처리 로직
        Long memberId = Optional.ofNullable(member).map(Member::getId).orElse(null);
         */
        long savedReviewId = reviewRegisterService.registerReview(request, null);
        return ResponseEntity.created(URI.create("/reviews/" + savedReviewId)).build();
    }

    @GetMapping("/v2/reviews/received")
    public ResponseEntity<ReceivedReviewPageResponse> findReceivedReviews(
            @RequestParam(required = false) Long lastReviewId,
            @RequestParam(required = false) Integer size,
            @ReviewGroupSession ReviewGroup reviewGroup
    ) {
        ReceivedReviewPageResponse response = reviewListLookupService.getReceivedReviews(lastReviewId, size, reviewGroup);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v2/reviews/{id}")
    public ResponseEntity<ReviewDetailResponse> findReceivedReviewDetail(
            @PathVariable long id,
            @ReviewGroupSession ReviewGroup reviewGroup
    ) {
        ReviewDetailResponse response = reviewDetailLookupService.getReviewDetail(id, reviewGroup);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v2/reviews/summary")
    public ResponseEntity<ReceivedReviewsSummaryResponse> findReceivedReviewOverview(
            @ReviewGroupSession ReviewGroup reviewGroup
    ) {
        ReceivedReviewsSummaryResponse response = reviewSummaryService.getReviewSummary(reviewGroup);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v2/reviews/gather")
    public ResponseEntity<ReviewsGatheredBySectionResponse> getReceivedReviewsBySectionId(
            @RequestParam("sectionId") long sectionId,
            @ReviewGroupSession ReviewGroup reviewGroup
    ) {
        ReviewsGatheredBySectionResponse response =
                reviewGatheredLookupService.getReceivedReviewsBySectionId(reviewGroup, sectionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v2/reviews/authored")
    public ResponseEntity<AuthoredReviewsResponse> findAuthoredReviews(
            @RequestParam(required = false) Long lastReviewId,
            @RequestParam(required = false) Integer size
//            @MemberSession Member member
            // TODO: 세션을 활용한 권한 체계에 따른 추가 조치 필요
    ) {
        AuthoredReviewsResponse response = reviewListLookupService.getAuthoredReviews(lastReviewId, size);
        return ResponseEntity.ok(response);
    }
}
