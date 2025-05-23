import { useSuspenseQuery } from '@tanstack/react-query';

import { getReviewLinksApi } from '@/apis/review';
import { REVIEW_QUERY_KEY } from '@/constants';

const useGetReviewLinks = () => {
  const result = useSuspenseQuery({
    queryKey: [REVIEW_QUERY_KEY.reviewLinks],
    queryFn: () => getReviewLinksApi(),
    staleTime: 60 * 60 * 1000,
  });

  return result;
};

export default useGetReviewLinks;
