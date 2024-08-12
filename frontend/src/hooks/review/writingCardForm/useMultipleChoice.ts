import { useState } from 'react';

import { ReviewWritingAnswer, ReviewWritingCardQuestion } from '@/types';

interface UseMultipleChoiceProps {
  question: ReviewWritingCardQuestion;
  updateAnswerMap: (answer: ReviewWritingAnswer) => void;
}

const useMultipleChoice = ({ question, updateAnswerMap }: UseMultipleChoiceProps) => {
  const [selectedOptionList, setSelectedOptionList] = useState<number[]>([]);
  const [isOpenLimitGuide, setIsOpenLimitGuide] = useState(false);

  const handleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { id } = event.currentTarget;
    // max를 넘어서는 선택하려 할 때
    if (isAboveSelectionLimit(Number(id))) {
      return setIsOpenLimitGuide(true);
    }
    // max를 넘어서는 선택을 하지 않은 경우
    setIsOpenLimitGuide(false);

    const newSelectedOptionList = makeNewSelectedOptionList(event);
    setSelectedOptionList(newSelectedOptionList);
    // 유효한 선택(=객관식 문항의 최소,최대 개수를 지켰을 경우)인지에 따라 answer 변경
    updateAnswerMap({
      questionId: question.questionId,
      selectedOptionIds: isValidatedChoice(newSelectedOptionList) ? newSelectedOptionList : null,
      text: null,
    });
  };

  /**
   * checkbox의 change 이벤트에 따라 새로운 selectedOptionList를 반환하는 함수
   */
  const makeNewSelectedOptionList = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { id, checked } = event.currentTarget;
    const optionId = Number(id);

    if (checked) {
      return selectedOptionList.concat(optionId);
    }
    return selectedOptionList.filter((option) => option !== optionId);
  };

  const isValidatedChoice = (newSelectedOptionList: number[]) => {
    if (!question.optionGroup) return false;
    const { minCount, maxCount } = question.optionGroup;
    const { length } = newSelectedOptionList;
    return length >= minCount && length <= maxCount;
  };

  const isMaxCheckedNumber = () => {
    if (!question.optionGroup) return false;
    return selectedOptionList.length >= question.optionGroup.maxCount;
  };

  /**
   * 선택 가능한 문항 수를 넘어서 문항을 선택하려 하는지 여부
   */
  const isAboveSelectionLimit = (optionId: number) => !!(isMaxCheckedNumber() && !isSelectedCheckbox(optionId));

  const isSelectedCheckbox = (optionId: number) => {
    return selectedOptionList.includes(optionId);
  };

  return {
    isOpenLimitGuide,
    handleCheckboxChange,
    isSelectedCheckbox,
  };
};
export default useMultipleChoice;