//리뷰 작성

import { ReviewData, WritingReviewInfoData } from '@/types';
import endPoint from './endpoints';

export const getInfoToWriteReview = async (reviewerGroupId: number) => {
  const response = await fetch(endPoint.gettingInfoToWriteReview(reviewerGroupId), {
    method: 'GET',
  });

  if (!response.ok) {
    throw new Error('리뷰 쓰기 위한 정보를 가져오는데 실패했습니다.');
  }

  const data = await response.json();
  return data as WritingReviewInfoData;
};

export const postReviewApi = async ({ reviewData }: { reviewData: ReviewData }) => {
  const response = await fetch(endPoint.postingReview, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(reviewData),
  });

  if (!response.ok) {
    throw new Error('리뷰를 작성하는 데 실패했습니다.');
  }

  const data = await response.json();
  return data;
};

// 상세리뷰
export const getDetailedReviewApi = async ({ reviewId }: { reviewId: number }) => {
  const response = await fetch(endPoint.gettingDetailedReview(reviewId), {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('상세 리뷰를 불러오는 데 실패했습니다.');
  }

  const data = await response.json();
  return data;
};