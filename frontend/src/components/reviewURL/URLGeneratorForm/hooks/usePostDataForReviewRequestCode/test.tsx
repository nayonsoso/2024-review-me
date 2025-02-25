import { renderHook, act, waitFor } from '@testing-library/react';

import { VALID_REVIEW_GROUP_REVIEW_REQUEST_CODE } from '@/mocks/mockData/group';
import QueryClientWrapper from '@/queryTestSetup/QueryClientWrapper';

import usePostDataForReviewRequestCode from '.';

describe('usePostDataForReviewRequestCode', () => {
  it('ReviewRequestCode를 발급받을 수 있다.', async () => {
    // given
    const dataForReviewRequestCode = {
      revieweeName: 'ollie',
      projectName: 'review-me',
      groupAccessCode: '1234',
    };

    const { result } = renderHook(
      () =>
        usePostDataForReviewRequestCode({
          handleAPIError: (error: Error) => {
            console.error(error);
          },
          handleAPISuccess: (data: any) => {},
        }),
      { wrapper: QueryClientWrapper },
    );

    // when
    act(() => {
      result.current.mutate(dataForReviewRequestCode);
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    // then
    expect(result.current.data.reviewRequestCode).toEqual(VALID_REVIEW_GROUP_REVIEW_REQUEST_CODE);
  });
});
