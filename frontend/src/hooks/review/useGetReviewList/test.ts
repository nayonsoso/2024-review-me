import { renderHook, waitFor } from '@testing-library/react';

import { VALID_REVIEW_REQUEST_CODE } from '@/mocks/mockData';
import QueryClientWrapper from '@/queryTestSetup/QueryClientWrapper';
import { testWithAuthCookie } from '@/utils';

import useGetReviewList from './index';

describe('리뷰 목록 페이지 API 연동 테스트', () => {
  test('성공적으로 데이터를 가져온다', async () => {
    const testReviewListAPI = async () => {
      const { result } = renderHook(
        () => useGetReviewList({ reviewRequestCode: VALID_REVIEW_REQUEST_CODE.nonMember }),
        {
          wrapper: QueryClientWrapper,
        },
      );

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });
    };
    await testWithAuthCookie({ authState: 'both', callback: testReviewListAPI });
  });
});
