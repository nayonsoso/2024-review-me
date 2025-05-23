package reviewme.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import reviewme.auth.domain.GitHubMember;
import reviewme.review.service.dto.request.ReviewRegisterRequest;
import reviewme.review.service.dto.response.gathered.HighlightResponse;
import reviewme.review.service.dto.response.gathered.RangeResponse;
import reviewme.review.service.dto.response.gathered.ReviewsGatheredByQuestionResponse;
import reviewme.review.service.dto.response.gathered.ReviewsGatheredBySectionResponse;
import reviewme.review.service.dto.response.gathered.SimpleQuestionResponse;
import reviewme.review.service.dto.response.gathered.TextResponse;
import reviewme.review.service.dto.response.gathered.VoteResponse;
import reviewme.review.service.dto.response.list.AuthoredReviewElementResponse;
import reviewme.review.service.dto.response.list.AuthoredReviewsResponse;
import reviewme.review.service.dto.response.list.ReceivedReviewPageElementResponse;
import reviewme.review.service.dto.response.list.ReceivedReviewPageResponse;
import reviewme.review.service.dto.response.list.ReceivedReviewsSummaryResponse;
import reviewme.review.service.dto.response.list.SelectedCategoryOptionResponse;
import reviewme.reviewgroup.domain.ReviewGroup;
import reviewme.reviewgroup.service.exception.ReviewGroupNotFoundByReviewRequestCodeException;
import reviewme.template.domain.QuestionType;

class ReviewApiTest extends ApiTest {

    private final String request = """
            {
                "reviewRequestCode": "ABCD1234",
                "answers": [
                    {
                        "questionId": 1,
                        "selectedOptionIds": [1, 2]
                    },
                    {
                        "questionId": 2,
                        "text": "답변 예시 1"
                    }
                ]
            }
            """;

    @Test
    void 비회원이_리뷰를_등록한다() {
        given(reviewRegisterService.registerReview(any(ReviewRegisterRequest.class), nullable(Long.class)))
                .willReturn(1L);

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("reviewRequestCode").description("리뷰 요청 코드"),

                fieldWithPath("answers[]").description("답변 목록"),
                fieldWithPath("answers[].questionId").description("질문 ID"),
                fieldWithPath("answers[].selectedOptionIds").description("선택한 옵션 ID 목록").optional(),
                fieldWithPath("answers[].text").description("서술 답변").optional()
        };

        RestDocumentationResultHandler handler = document(
                "create-review-by-guest",
                requestFields(requestFieldDescriptors)
        );

        givenWithSpec().log().all()
                .body(request)
                .when().post("/v2/reviews")
                .then().log().all()
                .apply(handler)
                .statusCode(201);
    }

    @Test
    void 회원이_리뷰를_등록한다() {
        given(reviewRegisterService.registerReview(any(ReviewRegisterRequest.class), nullable(Long.class)))
                .willReturn(1L);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("JSESSIONID").description("세션 ID")
        };

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("reviewRequestCode").description("리뷰 요청 코드"),

                fieldWithPath("answers[]").description("답변 목록"),
                fieldWithPath("answers[].questionId").description("질문 ID"),
                fieldWithPath("answers[].selectedOptionIds").description("선택한 옵션 ID 목록").optional(),
                fieldWithPath("answers[].text").description("서술 답변").optional()
        };

        RestDocumentationResultHandler handler = document(
                "create-review-by-member",
                requestCookies(cookieDescriptors),
                requestFields(requestFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("JSESSIONID", "ASVNE1VAKDNV4")
                .body(request)
                .when().post("/v2/reviews")
                .then().log().all()
                .apply(handler)
                .statusCode(201);
    }

    @Test
    void 리뷰_그룹_코드가_올바르지_않은_경우_예외가_발생한다() {
        given(reviewRegisterService.registerReview(any(ReviewRegisterRequest.class), nullable(Long.class)))
                .willThrow(new ReviewGroupNotFoundByReviewRequestCodeException("ABCD1234"));

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("reviewRequestCode").description("리뷰 요청 코드"),

                fieldWithPath("answers[]").description("답변 목록"),
                fieldWithPath("answers[].questionId").description("질문 ID"),
                fieldWithPath("answers[].selectedOptionIds").description("선택한 옵션 ID 목록").optional(),
                fieldWithPath("answers[].text").description("서술형 답변").optional()
        };

        RestDocumentationResultHandler handler = document(
                "create-review-invalid-review-request-code",
                requestFields(requestFieldDescriptors)
        );

        givenWithSpec().log().all()
                .body(request)
                .when().post("/v2/reviews")
                .then().log().all()
                .apply(handler)
                .statusCode(404);
    }

    @Test
    void 자신이_받은_리뷰_한_개를_조회한다() {
        given(reviewDetailLookupService.getReviewDetail(anyLong()))
                .willReturn(TemplateFixture.templateAnswerResponse());

        ParameterDescriptor[] requestPathDescriptors = {
                parameterWithName("id").description("리뷰 ID")
        };

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("JSESSIONID").description("세션 ID")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("createdAt").description("리뷰 작성 날짜"),
                fieldWithPath("formId").description("폼 ID"),
                fieldWithPath("revieweeName").description("리뷰이 이름"),
                fieldWithPath("projectName").description("프로젝트 이름"),

                fieldWithPath("sections[]").description("섹션 목록"),
                fieldWithPath("sections[].sectionId").description("섹션 ID"),
                fieldWithPath("sections[].header").description("섹션 제목"),

                fieldWithPath("sections[].questions[]").description("질문 목록"),
                fieldWithPath("sections[].questions[].questionId").description("질문 ID"),
                fieldWithPath("sections[].questions[].required").description("필수 여부"),
                fieldWithPath("sections[].questions[].content").description("질문 내용"),
                fieldWithPath("sections[].questions[].questionType").description("질문 타입"),

                fieldWithPath("sections[].questions[].optionGroup").description("옵션 그룹").optional(),
                fieldWithPath("sections[].questions[].optionGroup.optionGroupId").description("옵션 그룹 ID"),
                fieldWithPath("sections[].questions[].optionGroup.minCount").description("최소 선택 개수"),
                fieldWithPath("sections[].questions[].optionGroup.maxCount").description("최대 선택 개수"),

                fieldWithPath("sections[].questions[].optionGroup.options[]").description("선택 항목 목록"),
                fieldWithPath("sections[].questions[].optionGroup.options[].optionId").description("선택 항목 ID"),
                fieldWithPath("sections[].questions[].optionGroup.options[].content").description("선택 항목 내용"),
                fieldWithPath("sections[].questions[].optionGroup.options[].isChecked").description("선택 여부"),
                fieldWithPath("sections[].questions[].answer").description("서술형 답변").optional(),
        };

        RestDocumentationResultHandler handler = document(
                "review-detail-with-session",
                requestCookies(cookieDescriptors),
                pathParameters(requestPathDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .pathParam("id", "1")
                .cookie("JSESSIONID", "AVEBNKLCL13TNVZ")
                .when().get("/v2/reviews/{id}")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }

    @Test
    void 자신이_받은_리뷰_목록을_조회한다() {
        ReviewGroup reviewGroup = mock(ReviewGroup.class);
        given(reviewGroup.getId()).willReturn(1L);
        given(reviewGroupService.getReviewGroupByReviewRequestCode(anyString()))
                .willReturn(reviewGroup);

        List<ReceivedReviewPageElementResponse> receivedReviews = List.of(
                new ReceivedReviewPageElementResponse(1L, LocalDateTime.of(2024, 8, 1, 0, 0), "(리뷰 미리보기 1)",
                        List.of(new SelectedCategoryOptionResponse(1L, "카테고리 1"))),
                new ReceivedReviewPageElementResponse(2L, LocalDateTime.of(2024, 8, 2, 0, 0), "(리뷰 미리보기 2)",
                        List.of(new SelectedCategoryOptionResponse(2L, "카테고리 2")))
        );
        ReceivedReviewPageResponse response = new ReceivedReviewPageResponse(
                "아루3", "리뷰미", 1L, true, receivedReviews);
        given(reviewListLookupService.getReceivedReviews(anyLong(), anyLong(), anyInt()))
                .willReturn(response);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("JSESSIONID").description("세션 ID")
        };

        ParameterDescriptor[] requestPathDescriptors = {
                parameterWithName("reviewRequestCode").description("리뷰 요청 코드")
        };

        ParameterDescriptor[] queryParameter = {
                parameterWithName("lastReviewId").description("페이지의 마지막 리뷰 ID - 기본으로 최신순 첫번째 페이지 응답"),
                parameterWithName("size").description("페이지의 크기 - 기본으로 10개씩 응답")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("revieweeName").description("리뷰이 이름"),
                fieldWithPath("projectName").description("프로젝트 이름"),
                fieldWithPath("lastReviewId").description("페이지의 마지막 리뷰 ID"),
                fieldWithPath("isLastPage").description("마지막 페이지 여부"),

                fieldWithPath("reviews[]").description("리뷰 목록"),
                fieldWithPath("reviews[].reviewId").description("리뷰 ID"),
                fieldWithPath("reviews[].createdAt").description("리뷰 작성 날짜"),
                fieldWithPath("reviews[].contentPreview").description("리뷰 미리보기"),

                fieldWithPath("reviews[].categoryOptions[]").description("선택된 카테고리 목록"),
                fieldWithPath("reviews[].categoryOptions[].optionId").description("카테고리 ID"),
                fieldWithPath("reviews[].categoryOptions[].content").description("카테고리 내용")
        };

        RestDocumentationResultHandler handler = document(
                "received-review-list-with-pagination",
                pathParameters(requestPathDescriptors),
                requestCookies(cookieDescriptors),
                queryParameters(queryParameter),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .pathParam("reviewRequestCode", "ABCD1234")
                .cookie("JSESSIONID", "ASVNE1VAKDNV4")
                .queryParam("lastReviewId", "2")
                .queryParam("size", "5")
                .when().get("/v2/groups/{reviewRequestCode}/reviews/received")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }

    @Test
    void 자신이_받은_리뷰의_요약를_조회한다() {
        ReviewGroup reviewGroup = mock(ReviewGroup.class);
        given(reviewGroup.getId()).willReturn(1L);
        given(reviewGroupService.getReviewGroupByReviewRequestCode(anyString())).willReturn(reviewGroup);
        given(reviewSummaryService.getReviewSummary(anyLong()))
                .willReturn(new ReceivedReviewsSummaryResponse("리뷰미", "산초", 5));

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("JSESSIONID").description("세션 ID")
        };
        ParameterDescriptor[] requestPathDescriptors = {
                parameterWithName("reviewRequestCode").description("리뷰 요청 코드")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("projectName").description("프로젝트 이름"),
                fieldWithPath("revieweeName").description("리뷰어 이름"),
                fieldWithPath("totalReviewCount").description("받은 리뷰 전체 개수")
        };

        RestDocumentationResultHandler handler = document(
                "received-review-summary",
                pathParameters(requestPathDescriptors),
                requestCookies(cookieDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .pathParam("reviewRequestCode", "abcd1234")
                .cookie("JSESSIONID", "ABCDEFGHI1234")
                .when().get("/v2/groups/{reviewRequestCode}/reviews/summary")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }

    @Test
    void 자신이_받은_리뷰의_요약를_섹션별로_조회한다() {
        ReviewGroup reviewGroup = mock(ReviewGroup.class);
        given(reviewGroup.getId()).willReturn(1L);
        given(reviewGroupService.getReviewGroupByReviewRequestCode(anyString())).willReturn(reviewGroup);

        ReviewsGatheredBySectionResponse response = new ReviewsGatheredBySectionResponse(List.of(
                new ReviewsGatheredByQuestionResponse(
                        new SimpleQuestionResponse(1L, "서술형 질문", QuestionType.TEXT),
                        List.of(
                                new TextResponse(1L, "산초의 답변", List.of(
                                        new HighlightResponse(1, List.of(new RangeResponse(1, 10))),
                                        new HighlightResponse(2, List.of(new RangeResponse(1, 4)))
                                )),
                                new TextResponse(2L, "삼촌의 답변", List.of())),
                        null),
                new ReviewsGatheredByQuestionResponse(
                        new SimpleQuestionResponse(2L, "선택형 질문", QuestionType.CHECKBOX),
                        null,
                        List.of(
                                new VoteResponse("짜장", 3),
                                new VoteResponse("짬뽕", 5))))
        );
        given(reviewGatheredLookupService.getReceivedReviewsBySectionId(anyLong(), anyLong()))
                .willReturn(response);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("JSESSIONID").description("세션 ID")
        };
        ParameterDescriptor[] requestPathDescriptors = {
                parameterWithName("reviewRequestCode").description("리뷰 요청 코드")
        };
        ParameterDescriptor[] queryParameterDescriptors = {
                parameterWithName("sectionId").description("섹션 ID")
        };
        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("reviews").description("리뷰 목록"),
                fieldWithPath("reviews[].question").description("질문 정보"),
                fieldWithPath("reviews[].question.id").description("질문 ID"),
                fieldWithPath("reviews[].question.name").description("질문 이름"),
                fieldWithPath("reviews[].question.type").description("질문 유형"),
                fieldWithPath("reviews[].answers").description("서술형 답변 목록 - question.type이 TEXT가 아니면 null").optional(),
                fieldWithPath("reviews[].answers[].id").description("답변 ID").optional(),
                fieldWithPath("reviews[].answers[].content").description("서술형 답변 내용"),
                fieldWithPath("reviews[].answers[].highlights").description("형광펜 정보"),
                fieldWithPath("reviews[].answers[].highlights[].lineIndex").description("개행으로 구분되는 라인 번호, 0-based"),
                fieldWithPath("reviews[].answers[].highlights[].ranges").description("형광펜 범위"),
                fieldWithPath("reviews[].answers[].highlights[].ranges[].startIndex").description(
                        "하이라이트 시작 인덱스, 0-based"),
                fieldWithPath("reviews[].answers[].highlights[].ranges[].endIndex").description("하이라이트 끝 인덱스, 0-based"),
                fieldWithPath("reviews[].votes").description(
                        "객관식 답변 목록 - question.type이 CHECKBOX가 아니면 null").optional(),
                fieldWithPath("reviews[].votes[].content").description("객관식 항목"),
                fieldWithPath("reviews[].votes[].count").description("선택한 사람 수"),
        };
        RestDocumentationResultHandler handler = document(
                "received-review-by-section",
                pathParameters(requestPathDescriptors),
                requestCookies(cookieDescriptors),
                queryParameters(queryParameterDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .pathParam("reviewRequestCode", "abcd4321")
                .cookie("JSESSIONID", "ABCDEFGHI1234")
                .queryParam("sectionId", 1)
                .when().get("/v2/groups/{reviewRequestCode}/reviews/gather")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }

    @Test
    void 자신이_작성한_리뷰_목록을_조회한다() {
        given(sessionManager.getGitHubMember(any()))
                .willReturn(new GitHubMember(1L, "githubName", "githubURL"));

        List<AuthoredReviewElementResponse> authoredReviews = List.of(
                new AuthoredReviewElementResponse(1L, "테드1", "리뷰미", LocalDateTime.of(2024, 8, 2, 0, 0), "(리뷰 미리보기 1)",
                        List.of(new SelectedCategoryOptionResponse(1L, "카테고리 1"))),
                new AuthoredReviewElementResponse(2L, "테드2", "리뷰미", LocalDateTime.of(2024, 8, 1, 0, 0), "(리뷰 미리보기 2)",
                        List.of(new SelectedCategoryOptionResponse(2L, "카테고리 2")))
        );

        AuthoredReviewsResponse response = new AuthoredReviewsResponse(1L, true, authoredReviews);
        given(reviewListLookupService.getAuthoredReviews(anyLong(), nullable(Long.class), nullable(Integer.class)))
                .willReturn(response);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("JSESSIONID").description("세션 ID")
        };

        ParameterDescriptor[] queryParameter = {
                parameterWithName("lastReviewId").description("페이지의 마지막 리뷰 ID - 기본으로 최신순 첫번째 페이지 응답"),
                parameterWithName("size").description("페이지의 크기 - 기본으로 10개씩 응답")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("lastReviewId").description("페이지의 마지막 리뷰 ID"),
                fieldWithPath("isLastPage").description("마지막 페이지 여부"),

                fieldWithPath("reviews[]").description("리뷰 목록 (생성일 기준 내림차순 정렬)"),
                fieldWithPath("reviews[].reviewId").description("리뷰 ID"),
                fieldWithPath("reviews[].revieweeName").description("리뷰이 이름"),
                fieldWithPath("reviews[].projectName").description("프로젝트명"),
                fieldWithPath("reviews[].createdAt").description("리뷰 작성 날짜"),
                fieldWithPath("reviews[].contentPreview").description("리뷰 미리보기"),

                fieldWithPath("reviews[].categoryOptions[]").description("선택된 카테고리 목록"),
                fieldWithPath("reviews[].categoryOptions[].optionId").description("카테고리 ID"),
                fieldWithPath("reviews[].categoryOptions[].content").description("카테고리 내용")
        };

        RestDocumentationResultHandler handler = document(
                "authored-review-list-with-pagination",
                requestCookies(cookieDescriptors),
                queryParameters(queryParameter),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("JSESSIONID", "ASVNE1VAKDNV4")
//                .queryParam("reviewRequestCode", "hello!!")
                .queryParam("lastReviewId", "2")
                .queryParam("size", "5")
                .when().get("/v2/reviews/authored")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }
}
