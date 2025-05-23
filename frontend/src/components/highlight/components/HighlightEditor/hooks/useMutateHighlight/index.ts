import { useMutation, useQueryClient } from '@tanstack/react-query';

import { postHighlight, PostHighlightParams } from '@/apis/highlight';
import { LOCAL_STORAGE_KEY, REVIEW_QUERY_KEY, SESSION_STORAGE_KEY } from '@/constants';
import { EditorAnswerMap } from '@/types';

export interface UseMutateHighlightProps {
  reviewRequestCode: string;
  questionId: number;
  updateEditorAnswerMap: (editorAnswerMap: EditorAnswerMap) => void;
  resetHighlightMenu: () => void;
}

const useMutateHighlight = ({
  reviewRequestCode,
  questionId,
  updateEditorAnswerMap,
  resetHighlightMenu,
}: UseMutateHighlightProps) => {
  const queryClient = useQueryClient();

  /**
   * 형광펜 API 성공 후, 현재 질문에 대한 쿼리 캐시 무효화해서, 변경된 형광펜 데이터 불러오도록 함
   */
  const invalidateCurrentSectionQuery = () => {
    const sectionId = sessionStorage.getItem(SESSION_STORAGE_KEY.currentReviewCollectionSectionId);

    if (sectionId) {
      queryClient.invalidateQueries({
        predicate: (query) =>
          query.queryKey[0] === REVIEW_QUERY_KEY.groupedReviews &&
          query.queryKey[1] === Number(sectionId) &&
          query.queryKey[2] === reviewRequestCode,
      });
    }
  };

  const mutation = useMutation({
    mutationFn: (newEditorAnswerMap: EditorAnswerMap) => {
      const params: PostHighlightParams = {
        dataParams: { editorAnswerMap: newEditorAnswerMap, questionId },
        reviewRequestCode,
      };

      return postHighlight(params);
    },
    onMutate: () => {
      if (mutation.isPending) return;
    },
    onSuccess: (_, variables: EditorAnswerMap) => {
      updateEditorAnswerMap(variables);
      resetHighlightMenu();

      localStorage.removeItem(LOCAL_STORAGE_KEY.isHighlightError);
      // 해당 질문 쿼리 캐시 무효화
      invalidateCurrentSectionQuery();
    },
    onError: (error) => {
      // fallback 실행으로 인한,isEditable 상태 초기화 막음
      localStorage.setItem(LOCAL_STORAGE_KEY.isHighlightError, 'true');
    },
  });

  return mutation;
};

export default useMutateHighlight;
