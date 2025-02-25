import { http, HttpResponse } from 'msw';

import endPoint, {
  DETAILED_REVIEW_API_PARAMS,
  DETAILED_REVIEW_API_URL,
  REVIEW_GROUP_API_PARAMS,
  REVIEW_GROUP_API_URL,
  REVIEW_WRITING_API_PARAMS,
  REVIEW_WRITING_API_URL,
  VERSION2,
} from '@/apis/endpoints';

import {
  DETAILED_REVIEW_MOCK_DATA,
  DETAILED_PAGE_MOCK_API_SETTING_VALUES,
  REVIEW_REQUEST_CODE,
  REVIEW_QUESTION_DATA,
  REVIEW_LIST,
  MOCK_REVIEW_INFO_DATA,
} from '../mockData';
import { GROUPED_REVIEWS_MOCK_DATA, GROUPED_SECTION_MOCK_DATA } from '../mockData/reviewCollection';

import { authorizeWithCookie } from './cookies';

export const PAGE = {
  firstPageNumber: 1,
  firstPageStartIndex: 0,
};

const getReviewInfoData = () =>
  http.get(endPoint.gettingReviewInfoData, ({ cookies }) => {
    return authorizeWithCookie(cookies, () => HttpResponse.json(MOCK_REVIEW_INFO_DATA));
  });

const getDetailedReview = () =>
  http.get(new RegExp(`^${DETAILED_REVIEW_API_URL}/\\d+$`), ({ request, cookies }) => {
    const handleAPI = () => {
      //요청 url에서 reviewId, memberId 추출
      const url = new URL(request.url);
      const urlReviewId = url.pathname.replace(`/${VERSION2}/${DETAILED_REVIEW_API_PARAMS.resource}/`, '');

      const { reviewId } = DETAILED_PAGE_MOCK_API_SETTING_VALUES;
      // 유효한 reviewId, memberId일 경우에만 데이터 반환
      if (Number(urlReviewId) == reviewId) {
        return HttpResponse.json(DETAILED_REVIEW_MOCK_DATA);
      }

      return HttpResponse.json({ error: '잘못된 상세리뷰 요청' }, { status: 404 });
    };

    return authorizeWithCookie(cookies, handleAPI);
  });

const getDataToWriteReview = () =>
  http.get(new RegExp(`^${REVIEW_WRITING_API_URL}/${REVIEW_WRITING_API_PARAMS.queryString.write}`), ({ request }) => {
    //요청 url에서 reviewId, memberId 추출
    const url = new URL(request.url);
    const urlRequestCode = url.searchParams.get(REVIEW_WRITING_API_PARAMS.queryString.reviewRequestCode);

    if (REVIEW_REQUEST_CODE === urlRequestCode) {
      return HttpResponse.json(REVIEW_QUESTION_DATA);
    }
    return HttpResponse.json({ error: '잘못된 리뷰 작성 데이터 요청' }, { status: 404 });
  });

// TODO: 추후 getReviewList API에서 리뷰 정보(이름, 개수...)를 내려주지 않는 경우 핸들러도 수정 필요
const getReviewList = (lastReviewId: number | null, size: number) => {
  return http.get(endPoint.gettingReviewList(lastReviewId, size), ({ request, cookies }) => {
    const handleAPI = () => {
      const url = new URL(request.url);

      const lastReviewIdParam = url.searchParams.get('lastReviewId');
      const lastReviewId = lastReviewIdParam === 'null' ? 0 : Number(lastReviewIdParam);

      const isFirstPage = lastReviewId === 0;
      const startIndex = isFirstPage
        ? PAGE.firstPageStartIndex
        : REVIEW_LIST.reviews.findIndex((review) => review.reviewId === lastReviewId) + 1;

      const endIndex = startIndex + size;

      const paginatedReviews = REVIEW_LIST.reviews.slice(startIndex, endIndex);

      const isLastPage = endIndex >= REVIEW_LIST.reviews.length;

      return HttpResponse.json({
        revieweeName: REVIEW_LIST.revieweeName,
        projectName: REVIEW_LIST.projectName,
        lastReviewId: paginatedReviews.length > 0 ? paginatedReviews[paginatedReviews.length - 1].reviewId : 0,
        isLastPage: isLastPage,
        reviews: paginatedReviews,
      });
    };

    return authorizeWithCookie(cookies, handleAPI);
  });
};

const postReview = () =>
  http.post(endPoint.postingReview, () => {
    return HttpResponse.json({ message: 'post 성공' }, { status: 201 });
  });

const getSectionList = () =>
  http.get(endPoint.gettingSectionList, ({ cookies }) => {
    return authorizeWithCookie(cookies, () => HttpResponse.json(GROUPED_SECTION_MOCK_DATA));
  });

const getGroupedReviews = () => {
  return http.get(new RegExp(`^${REVIEW_GROUP_API_URL}`), ({ request, cookies }) => {
    const url = new URL(request.url);
    const sectionId = url.searchParams.get(REVIEW_GROUP_API_PARAMS.queryString.sectionId);
    const { length } = GROUPED_REVIEWS_MOCK_DATA;
    const index = (Number(sectionId) + length) % length;

    return authorizeWithCookie(cookies, () => HttpResponse.json(GROUPED_REVIEWS_MOCK_DATA[index]));
  });
};

const reviewHandler = [
  getDetailedReview(),
  getReviewList(null, 10),
  getDataToWriteReview(),
  getSectionList(),
  getGroupedReviews(),
  getReviewInfoData(),
  postReview(),
];

export default reviewHandler;
